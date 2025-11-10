package com.vetcarepro.service.git;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("!test")
@ConditionalOnProperty(prefix = "git.auto-push", name = "enabled", havingValue = "true")
public class GitAutoPushService {

    private final Path projectRoot;
    private final Path scriptPath;
    private final long debounceMs;

    private WatchService watchService;
    private final ExecutorService watcherPool = Executors.newSingleThreadExecutor();
    private final ExecutorService pushPool = Executors.newSingleThreadExecutor();
    private final AtomicBoolean pushInProgress = new AtomicBoolean(false);
    private volatile Instant lastExecution = Instant.MIN;

    public GitAutoPushService(
        @Value("${git.auto-push.watch-root:.}") String watchRoot,
        @Value("${git.auto-push.script:./scripts/auto-push.sh}") String script,
        @Value("${git.auto-push.debounce-ms:4000}") long debounceMs) {

        this.projectRoot = Paths.get(watchRoot).toAbsolutePath().normalize();
        this.scriptPath = Paths.get(script).toAbsolutePath().normalize();
        this.debounceMs = debounceMs;
    }

    @PostConstruct
    void initialize() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        registerRecursively(projectRoot);
        watcherPool.submit(this::watchLoop);
        log.info("Git auto-push watchdog ready at {}", projectRoot);
    }

    @PreDestroy
    void shutdown() {
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException ignored) {
        }
        watcherPool.shutdownNow();
        pushPool.shutdownNow();
    }

    private void watchLoop() {
        while (true) {
            try {
                WatchKey key = watchService.take();
                Path dir = (Path) key.watchable();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    Path child = dir.resolve((Path) event.context());
                    if (Files.isDirectory(child)) {
                        registerRecursively(child);
                    }
                    if (!isInsideGitFolder(child)) {
                        triggerAutoPush();
                    }
                }
                if (!key.reset()) {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (IOException e) {
                log.error("Auto-push watcher failure", e);
            }
        }
    }

    private void triggerAutoPush() {
        Instant now = Instant.now();
        if (Duration.between(lastExecution, now).toMillis() < debounceMs) {
            return;
        }
        if (!pushInProgress.compareAndSet(false, true)) {
            return;
        }
        lastExecution = now;
        CompletableFuture.runAsync(this::runPushScript, pushPool)
            .whenComplete((ignored, throwable) -> pushInProgress.set(false));
    }

    private void runPushScript() {
        if (!Files.isExecutable(scriptPath)) {
            log.warn("Auto-push script not executable: {}", scriptPath);
            return;
        }
        ProcessBuilder builder = new ProcessBuilder("bash", scriptPath.toString());
        builder.directory(projectRoot.toFile());
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.warn("Auto-push script exited with {}: {}", exitCode, output);
            } else {
                log.debug("Auto-push output: {}", output.trim());
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Auto-push execution failed", e);
        }
    }

    private void registerRecursively(Path start) throws IOException {
        if (!Files.exists(start)) {
            return;
        }
        Files.walk(start)
            .filter(Files::isDirectory)
            .filter(path -> !isInsideGitFolder(path))
            .forEach(path -> {
                try {
                    path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY
                    );
                } catch (IOException e) {
                    log.warn("Unable to watch path {}", path, e);
                }
            });
    }

    private boolean isInsideGitFolder(Path path) {
        return Objects.nonNull(path) && path.toString().contains(FileSystems.getDefault().getSeparator() + ".git");
    }
}
