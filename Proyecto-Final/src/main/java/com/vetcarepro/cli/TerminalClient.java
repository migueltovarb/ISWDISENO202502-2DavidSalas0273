package com.vetcarepro.cli;

import java.util.Scanner;

import com.vetcarepro.cli.actions.AppointmentActions;
import com.vetcarepro.cli.actions.AuthActions;
import com.vetcarepro.cli.actions.CertificateActions;
import com.vetcarepro.cli.actions.MedicalHistoryActions;
import com.vetcarepro.cli.actions.PetActions;
import com.vetcarepro.cli.actions.ReminderActions;
import com.vetcarepro.cli.actions.VaccineActions;
import com.vetcarepro.cli.actions.VeterinarianActions;

/**
 * Punto de entrada del cliente de terminal.
 */
public class TerminalClient {

    private static final String BASE_URL = "http://localhost:8080";

    public static void main(String[] args) {
        new TerminalClient().run();
    }

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

    private void run() {
        while (true) {
            printMainMenu();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> authActions.login();
                case "2" -> authActions.registerOwner();
                case "3" -> authActions.registerVeterinarian();
                case "4" -> petMenu();
                case "5" -> appointmentMenu();
                case "6" -> veterinarianMenu();
                case "7" -> vaccineMenu();
                case "8" -> historyMenu();
                case "9" -> certificateMenu();
                case "10" -> reminderActions.viewReminders();
                case "11" -> {
                    authActions.logout();
                    ui.success("Adiós.");
                    return;
                }
                default -> ui.warning("Opción no válida");
            }
        }
    }

    private void printMainMenu() {
        ui.separator();
        ui.title("      \uD83D\uDC3E VETCARE PRO TERMINAL \uD83D\uDC3E");
        ui.separator();
        System.out.println("[1] Iniciar sesión");
        System.out.println("[2] Registrar dueño");
        System.out.println("[3] Registrar veterinario");
        System.out.println("[4] Gestión de Mascotas");
        System.out.println("[5] Gestión de Citas");
        System.out.println("[6] Gestión Veterinarios");
        System.out.println("[7] Vacunas");
        System.out.println("[8] Historial Médico");
        System.out.println("[9] Certificados");
        System.out.println("[10] Recordatorios");
        System.out.println("[11] Salir");
        ui.separator();
        System.out.print("Seleccione una opción: ");
    }

    private void petMenu() {
        ui.title("Mascotas");
        System.out.println("[1] Listar mascotas");
        System.out.println("[2] Registrar mascota");
        System.out.println("[3] Volver");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> petActions.listPets();
            case "2" -> petActions.createPet();
            default -> ui.warning("Volviendo...");
        }
    }

    private void appointmentMenu() {
        ui.title("Citas");
        System.out.println("[1] Listar citas");
        System.out.println("[2] Registrar cita");
        System.out.println("[3] Completar cita");
        System.out.println("[4] Volver");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> appointmentActions.listAppointments();
            case "2" -> appointmentActions.createAppointment();
            case "3" -> appointmentActions.completeAppointment();
            default -> ui.warning("Volviendo...");
        }
    }

    private void veterinarianMenu() {
        ui.title("Veterinarios");
        System.out.println("[1] Listar veterinarios");
        System.out.println("[2] Registrar veterinario");
        System.out.println("[3] Volver");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> vetActions.listVets();
            case "2" -> vetActions.createVet();
            default -> ui.warning("Volviendo...");
        }
    }

    private void vaccineMenu() {
        ui.title("Vacunas");
        System.out.println("[1] Listar vacunas");
        System.out.println("[2] Registrar vacuna");
        System.out.println("[3] Volver");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> vaccineActions.listVaccines();
            case "2" -> vaccineActions.registerVaccine();
            default -> ui.warning("Volviendo...");
        }
    }

    private void historyMenu() {
        ui.title("Historial Médico");
        System.out.println("[1] Ver historial");
        System.out.println("[2] Registrar entrada");
        System.out.println("[3] Volver");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> historyActions.viewHistory();
            case "2" -> historyActions.registerHistory();
            default -> ui.warning("Volviendo...");
        }
    }

    private void certificateMenu() {
        ui.title("Certificados");
        System.out.println("[1] Obtener certificado vacunación");
        System.out.println("[2] Volver");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> certificateActions.generateCertificate();
            default -> ui.warning("Volviendo...");
        }
    }
}
