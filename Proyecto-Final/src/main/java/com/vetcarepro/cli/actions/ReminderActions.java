package com.vetcarepro.cli.actions;

import com.vetcarepro.cli.ApiClient;
import com.vetcarepro.cli.MenuRenderer;

public class ReminderActions {
    private final ApiClient api;
    private final MenuRenderer ui;

    public ReminderActions(ApiClient api, MenuRenderer ui) {
        this.api = api;
        this.ui = ui;
    }

    public void viewReminders() {
        try {
            String resp = api.get("/api/appointments"); // usando citas como base para próximos recordatorios
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error obteniendo recordatorios: " + e.getMessage());
        }
    }
}
