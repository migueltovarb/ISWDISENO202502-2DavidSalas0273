package com.vetcarepro.cli.actions;

import java.util.Scanner;

import com.vetcarepro.cli.ApiClient;
import com.vetcarepro.cli.MenuRenderer;

public class CertificateActions {
    private final ApiClient api;
    private final MenuRenderer ui;
    private final Scanner scanner;

    public CertificateActions(ApiClient api, MenuRenderer ui, Scanner scanner) {
        this.api = api;
        this.ui = ui;
        this.scanner = scanner;
    }

    public void generateCertificate() {
        try {
            System.out.print("vaccinationCertificateId o appointmentId (según tu API): ");
            String id = scanner.nextLine();
            String resp = api.get("/api/vaccination-certificates/" + id);
            ui.success(resp);
        } catch (Exception e) {
            ui.error("Error generando/obteniendo certificado: " + e.getMessage());
        }
    }
}
