package com.vetcarepro.cli.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.vetcarepro.cli.ApiClient;
import com.vetcarepro.cli.MenuRenderer;

public class AppointmentActions {
    private final ApiClient api;
    private final MenuRenderer ui;
    private final Scanner scanner;

    public AppointmentActions(ApiClient api, MenuRenderer ui, Scanner scanner) {
        this.api = api;
        this.ui = ui;
        this.scanner = scanner;
    }

    public void listAppointments() {
        try {
            String resp = api.get("/api/appointments");
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error listando citas: " + e.getMessage());
        }
    }

    public void createAppointment() {
        try {
            Map<String, Object> payload = new HashMap<>();
            System.out.print("ownerId: ");
            payload.put("ownerId", scanner.nextLine());
            System.out.print("petId: ");
            payload.put("petId", scanner.nextLine());
            System.out.print("veterinarianId: ");
            payload.put("veterinarianId", scanner.nextLine());
            System.out.print("type (CHECKUP/VACCINATION/...): ");
            payload.put("type", scanner.nextLine());
            System.out.print("Fecha cita (ISO-8601, ej 2025-12-31T10:00:00): ");
            payload.put("appointmentDate", scanner.nextLine());
            System.out.print("Motivo: ");
            payload.put("reason", scanner.nextLine());
            String resp = api.post("/api/appointments", json(payload), true);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error registrando cita: " + e.getMessage());
        }
    }

    public void completeAppointment() {
        try {
            System.out.print("ID de cita: ");
            String id = scanner.nextLine();
            String resp = api.post("/api/appointments/" + id + "/complete", "{}", true);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error completando cita: " + e.getMessage());
        }
    }

    private String json(Map<String, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object val = entry.getValue();
            if (val == null) sb.append("null");
            else if (val instanceof Number || val instanceof Boolean) sb.append(val.toString());
            else sb.append("\"").append(escape(val.toString())).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
