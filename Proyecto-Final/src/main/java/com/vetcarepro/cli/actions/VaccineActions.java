package com.vetcarepro.cli.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.vetcarepro.cli.ApiClient;
import com.vetcarepro.cli.MenuRenderer;

public class VaccineActions {
    private final ApiClient api;
    private final MenuRenderer ui;
    private final Scanner scanner;

    public VaccineActions(ApiClient api, MenuRenderer ui, Scanner scanner) {
        this.api = api;
        this.ui = ui;
        this.scanner = scanner;
    }

    public void listVaccines() {
        try {
            String resp = api.get("/api/vaccines");
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error listando vacunas: " + e.getMessage());
        }
    }

    public void registerVaccine() {
        try {
            Map<String, Object> payload = new HashMap<>();
            System.out.print("Nombre: ");
            payload.put("name", scanner.nextLine());
            System.out.print("Fabricante: ");
            payload.put("manufacturer", scanner.nextLine());
            System.out.print("Descripción: ");
            payload.put("description", scanner.nextLine());
            System.out.print("Días validez: ");
            payload.put("validityDays", Integer.parseInt(scanner.nextLine()));
            System.out.print("Días ventana recordatorio: ");
            payload.put("reminderWindowDays", Integer.parseInt(scanner.nextLine()));
            String resp = api.post("/api/vaccines", json(payload), true);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error registrando vacuna: " + e.getMessage());
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
