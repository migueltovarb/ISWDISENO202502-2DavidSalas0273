package com.vetcarepro.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Persistencia simple de sesión en archivo local.
 */
public class SessionManager {
    private static final Path SESSION_FILE = Path.of(".vetcarepro_session");

    private String token;
    private String email;
    private String role;

    public void save(String email, String role) {
        saveInternal(null, email, role);
    }

    public void save(String token, String role, String email) {
        saveInternal(token, email, role);
    }

    private void saveInternal(String token, String email, String role) {
        this.token = token;
        this.email = email;
        this.role = role;
        Map<String, String> data = new HashMap<>();
        if (token != null) data.put("TOKEN", token);
        if (email != null) data.put("EMAIL", email);
        if (role != null) data.put("ROLE", role);
        try {
            StringBuilder sb = new StringBuilder();
            data.forEach((k, v) -> sb.append(k).append("=").append(v).append("\n"));
            Files.writeString(SESSION_FILE, sb.toString());
        } catch (IOException ignored) {}
    }

    public void clear() {
        token = null;
        email = null;
        role = null;
        try {
            Files.deleteIfExists(SESSION_FILE);
        } catch (IOException ignored) {}
    }

    public void load() {
        if (!Files.exists(SESSION_FILE)) return;
        try {
            for (String line : Files.readAllLines(SESSION_FILE)) {
                if (line.startsWith("TOKEN=")) token = line.substring("TOKEN=".length());
                if (line.startsWith("EMAIL=")) email = line.substring("EMAIL=".length());
                if (line.startsWith("ROLE=")) role = line.substring("ROLE=".length());
            }
        } catch (IOException ignored) {}
    }

    public boolean hasSession() {
        return email != null && role != null;
    }

    public boolean hasToken() {
        return token != null && !token.isBlank();
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
