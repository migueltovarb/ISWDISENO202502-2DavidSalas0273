package com.vetcarepro.cli.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.vetcarepro.cli.ApiClient;
import com.vetcarepro.cli.MenuRenderer;

public class VeterinarianActions {
    private final ApiClient api;
    private final MenuRenderer ui;
    private final Scanner scanner;

    public VeterinarianActions(ApiClient api, MenuRenderer ui, Scanner scanner) {
        this.api = api;
        this.ui = ui;
        this.scanner = scanner;
    }

    public void listVets() {
        try {
            String resp = api.get("/api/veterinarians");
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error listando veterinarios: " + e.getMessage());
        }
    }

    public void createVet() {
        try {
            Map<String, Object> payload = new HashMap<>();
            System.out.print("Email: ");
            payload.put("email", scanner.nextLine());
            System.out.print("Password: ");
            payload.put("password", scanner.nextLine());
            System.out.print("Nombre completo: ");
            payload.put("fullName", scanner.nextLine());
            System.out.print("Teléfono: ");
            payload.put("phone", scanner.nextLine());
            System.out.print("Licencia: ");
            payload.put("licenseNumber", scanner.nextLine());
            System.out.print("Especialización: ");
            payload.put("specialization", scanner.nextLine());
            String resp = api.post("/auth/register-veterinarian", json(payload), false);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error registrando veterinario: " + e.getMessage());
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
