package com.vetcarepro.cli.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.vetcarepro.cli.ApiClient;
import com.vetcarepro.cli.MenuRenderer;

public class MedicalHistoryActions {
    private final ApiClient api;
    private final MenuRenderer ui;
    private final Scanner scanner;

    public MedicalHistoryActions(ApiClient api, MenuRenderer ui, Scanner scanner) {
        this.api = api;
        this.ui = ui;
        this.scanner = scanner;
    }

    public void viewHistory() {
        try {
            System.out.print("petId: ");
            String petId = scanner.nextLine();
            String resp = api.get("/api/medical-history?petId=" + petId);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error consultando historial: " + e.getMessage());
        }
    }

    public void registerHistory() {
        try {
            Map<String, Object> payload = new HashMap<>();
            System.out.print("petId: ");
            payload.put("petId", scanner.nextLine());
            System.out.print("veterinarianId: ");
            payload.put("veterinarianId", scanner.nextLine());
            System.out.print("visitDate (ISO-8601): ");
            payload.put("visitDate", scanner.nextLine());
            System.out.print("Resumen: ");
            payload.put("summary", scanner.nextLine());
            System.out.print("Diagnóstico: ");
            payload.put("diagnosis", scanner.nextLine());
            System.out.print("Tratamientos: ");
            payload.put("treatments", scanner.nextLine());
            System.out.print("Recomendaciones: ");
            payload.put("recommendations", scanner.nextLine());
            String resp = api.post("/api/medical-history", json(payload), true);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error registrando historial: " + e.getMessage());
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
