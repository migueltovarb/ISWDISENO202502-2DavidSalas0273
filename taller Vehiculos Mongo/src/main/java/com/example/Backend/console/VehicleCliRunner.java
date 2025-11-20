package com.example.Backend.console;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.cli.enabled", havingValue = "true")
public class VehicleCliRunner implements CommandLineRunner {

    private final VehicleConsoleMenu menu;
    private final ConfigurableApplicationContext context;

    public VehicleCliRunner(VehicleConsoleMenu menu, ConfigurableApplicationContext context) {
        this.menu = menu;
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        menu.start();
        SpringApplication.exit(context, () -> 0);
    }
}
