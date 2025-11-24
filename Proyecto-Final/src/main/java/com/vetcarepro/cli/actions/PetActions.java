package com.vetcarepro.cli.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.vetcarepro.cli.ApiClient;
import com.vetcarepro.cli.MenuRenderer;

public class PetActions {
    private final ApiClient api;
    private final MenuRenderer ui;
    private final Scanner scanner;

    public PetActions(ApiClient api, MenuRenderer ui, Scanner scanner) {
        this.api = api;
        this.ui = ui;
        this.scanner = scanner;
    }

    public void listPets() {
        try {
            String resp = api.get("/api/pets");
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error listando mascotas: " + e.getMessage());
        }
    }

    public void createPet() {
        try {
            Map<String, Object> payload = new HashMap<>();
            System.out.print("ownerId: ");
            payload.put("ownerId", scanner.nextLine());
            System.out.print("Nombre: ");
            payload.put("name", scanner.nextLine());
            System.out.print("Especie (DOG/CAT/...): ");
            payload.put("species", scanner.nextLine());
            System.out.print("Raza: ");
            payload.put("breed", scanner.nextLine());
            System.out.print("¿Castrado? (true/false): ");
            payload.put("neutered", Boolean.parseBoolean(scanner.nextLine()));
            String resp = api.post("/api/pets", json(payload), true);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error registrando mascota: " + e.getMessage());
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
