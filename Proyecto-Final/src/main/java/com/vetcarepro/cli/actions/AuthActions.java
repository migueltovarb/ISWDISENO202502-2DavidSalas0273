package com.vetcarepro.cli.actions;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import com.vetcarepro.cli.ApiClient;
import com.vetcarepro.cli.MenuRenderer;
import com.vetcarepro.cli.SessionManager;

/**
 * Maneja login/logout y registro básico.
 */
public class AuthActions {

    private final ApiClient api;
    private final SessionManager session;
    private final MenuRenderer ui;
    private final Scanner scanner;

    public AuthActions(ApiClient api, SessionManager session, MenuRenderer ui, Scanner scanner) {
        this.api = api;
        this.session = session;
        this.ui = ui;
        this.scanner = scanner;
    }

    public void login() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();
            String body = json(Map.of("email", email, "password", password));
            String resp = api.post("/auth/login", body, false);
            ui.success(resp);
            String token = extract(resp, "\"token\"");
            String role = extract(resp, "\"role\"");
            if (token != null && role != null) {
                session.save(token, role, email);
                ui.success("JWT guardado. Rol: " + role);
            } else {
                ui.warning("No se pudo extraer token/rol de la respuesta.");
            }
        } catch (IOException | InterruptedException e) {
            ui.error("Error login: " + e.getMessage());
        }
    }

    public void logout() {
        session.clear();
        ui.success("Sesión cerrada.");
    }

    public void registerOwner() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();
            System.out.print("Nombre completo: ");
            String fullName = scanner.nextLine();
            System.out.print("Teléfono: ");
            String phone = scanner.nextLine();
            System.out.print("Dirección: ");
            String address = scanner.nextLine();
            String body = json(Map.of(
                "email", email,
                "password", password,
                "fullName", fullName,
                "phone", phone,
                "address", address
            ));
            String resp = api.post("/auth/register-owner", body, false);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error registrando dueño: " + e.getMessage());
        }
    }

    public void registerVeterinarian() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();
            System.out.print("Nombre completo: ");
            String fullName = scanner.nextLine();
            System.out.print("Teléfono: ");
            String phone = scanner.nextLine();
            System.out.print("Licencia: ");
            String license = scanner.nextLine();
            System.out.print("Especialización: ");
            String spec = scanner.nextLine();
            String body = json(Map.of(
                "email", email,
                "password", password,
                "fullName", fullName,
                "phone", phone,
                "licenseNumber", license,
                "specialization", spec
            ));
            String resp = api.post("/auth/register-veterinarian", body, false);
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

    private String extract(String body, String key) {
        int idx = body.indexOf(key);
        if (idx == -1) return null;
        int colon = body.indexOf(":", idx);
        int q1 = body.indexOf("\"", colon);
        int q2 = body.indexOf("\"", q1 + 1);
        if (q1 == -1 || q2 == -1) return null;
        return body.substring(q1 + 1, q2);
    }
}
