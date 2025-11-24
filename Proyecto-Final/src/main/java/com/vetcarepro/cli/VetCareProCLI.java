package com.vetcarepro.cli;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.vetcarepro.cli.actions.AppointmentActions;
import com.vetcarepro.cli.actions.AuthActions;
import com.vetcarepro.cli.actions.CertificateActions;
import com.vetcarepro.cli.actions.MedicalHistoryActions;
import com.vetcarepro.cli.actions.PetActions;
import com.vetcarepro.cli.actions.ReminderActions;
import com.vetcarepro.cli.actions.VaccineActions;
import com.vetcarepro.cli.actions.VeterinarianActions;

/**
 * CLI principal que arranca con el CommandLineRunner de la aplicación.
 */
@Component
public class VetCareProCLI {

    private static final String BASE_URL = "http://localhost:8080";

    private final Scanner scanner = new Scanner(System.in);
    private final MenuRenderer ui = new MenuRenderer();
    private final SessionManager session = new SessionManager();
    private final ApiClient api = new ApiClient(BASE_URL, session, ui);

    private final AuthActions authActions = new AuthActions(api, session, ui, scanner);
    private final PetActions petActions = new PetActions(api, ui, scanner);
    private final VeterinarianActions vetActions = new VeterinarianActions(api, ui, scanner);
    private final AppointmentActions appointmentActions = new AppointmentActions(api, ui, scanner);
    private final VaccineActions vaccineActions = new VaccineActions(api, ui, scanner);
    private final MedicalHistoryActions historyActions = new MedicalHistoryActions(api, ui, scanner);
    private final CertificateActions certificateActions = new CertificateActions(api, ui, scanner);
    private final ReminderActions reminderActions = new ReminderActions(api, ui);

    public void start() {
        while (true) {
            printMainMenu();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> authActions.login();
                case "2" -> authActions.registerOwner();
                case "3" -> authActions.registerVeterinarian();
                case "4" -> petActions.createPet();
                case "5" -> petActions.listPets();
                case "6" -> {
                    authActions.logout();
                    ui.success("Saliendo CLI...");
                    return;
                }
                default -> ui.warning("Opción no válida");
            }
        }
    }

    private void printMainMenu() {
        ui.separator();
        ui.title("     \uD83D\uDC3E VET CARE PRO CLI \uD83D\uDC3E");
        ui.separator();
        System.out.println("[1] Login");
        System.out.println("[2] Registrar dueño");
        System.out.println("[3] Registrar veterinario");
        System.out.println("[4] Registrar mascota");
        System.out.println("[5] Ver mascotas");
        System.out.println("[6] Salir");
        ui.separator();
        System.out.print("Seleccione una opción: ");
    }
}
