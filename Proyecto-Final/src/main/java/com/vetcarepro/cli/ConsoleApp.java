package com.vetcarepro.cli;

import java.time.LocalDateTime;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vetcarepro.domain.entity.Appointment;
import com.vetcarepro.domain.entity.Pet;
import com.vetcarepro.domain.enums.AppointmentType;
import com.vetcarepro.domain.enums.Role;
import com.vetcarepro.dto.AppointmentRequest;
import com.vetcarepro.dto.AuthRequest;
import com.vetcarepro.dto.AuthResponse;
import com.vetcarepro.dto.PetRequest;
import com.vetcarepro.dto.RegisterOwnerRequest;
import com.vetcarepro.dto.RegisterVeterinarianRequest;
import com.vetcarepro.service.AppointmentService;
import com.vetcarepro.service.AuthService;
import com.vetcarepro.service.PetOwnerService;
import com.vetcarepro.service.PetService;
import com.vetcarepro.service.VaccinationCertificateService;
import com.vetcarepro.service.VaccineService;
import com.vetcarepro.service.VeterinarianService;

@Component
public class ConsoleApp implements CommandLineRunner {

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    private final AuthService authService;
    private final PetService petService;
    private final PetOwnerService ownerService;
    private final VeterinarianService vetService;
    private final AppointmentService appointmentService;
    private final VaccineService vaccineService;
    private final VaccinationCertificateService certificateService;

    private final Scanner scanner = new Scanner(System.in);
    private final SessionManager sessionManager = new SessionManager();

    private String currentEmail;
    private Role currentRole;
    private String currentUserId;

    public ConsoleApp(AuthService authService,
                      PetService petService,
                      PetOwnerService ownerService,
                      VeterinarianService vetService,
                      AppointmentService appointmentService,
                      VaccineService vaccineService,
                      VaccinationCertificateService certificateService) {
        this.authService = authService;
        this.petService = petService;
        this.ownerService = ownerService;
        this.vetService = vetService;
        this.appointmentService = appointmentService;
        this.vaccineService = vaccineService;
        this.certificateService = certificateService;
    }

    @Override
    public void run(String... args) {
        // Restaurar sesión si existe
        sessionManager.load();
        if (sessionManager.hasSession()) {
            try {
                var user = authService.findByEmail(sessionManager.getEmail());
                currentEmail = user.getEmail();
                currentRole = user.getRole();
                currentUserId = user.getId();
                printlnGreen("Sesión restaurada: " + currentEmail + " (rol: " + currentRole + ")");
            } catch (Exception e) {
                sessionManager.clear();
            }
        }
        while (true) {
            printMenu();
            String opt = scanner.nextLine().trim();
            try {
                switch (opt) {
                    case "1" -> doLogin();
                    case "2" -> registerOwner();
                    case "3" -> registerVeterinarian();
                    case "4" -> registerPet();
                    case "5" -> listPets();
                    case "6" -> appointmentsMenu();
                    case "7" -> vaccinesMenu();
                    case "8" -> historyMenu();
                    case "9" -> {
                        printlnGreen("Saliendo...");
                        return;
                    }
                    default -> printlnRed("Opción no válida");
                }
            } catch (Exception e) {
                printlnRed("Error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println(BLUE + "===================================" + RESET);
        System.out.println(BLUE + "        VET CARE PRO - CLI" + RESET);
        System.out.println(BLUE + "===================================" + RESET);
        System.out.println("1. Login");
        System.out.println("2. Registrar dueño");
        System.out.println("3. Registrar veterinario");
        System.out.println("4. Registrar mascota");
        System.out.println("5. Ver mascotas");
        System.out.println("6. Citas");
        System.out.println("7. Vacunas");
        System.out.println("8. Historial médico");
        System.out.println("9. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void doLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        AuthRequest req = new AuthRequest();
        req.setEmail(email);
        req.setPassword(pass);
        AuthResponse resp = authService.login(req);
        this.currentEmail = resp.getEmail();
        this.currentRole = resp.getRole();
        this.currentUserId = resp.getUserId();
        sessionManager.save(currentEmail, currentRole.name());
        printlnGreen("Login OK. Rol: " + currentRole);
    }

    private void ensureLogged() {
        if (currentEmail == null) throw new IllegalStateException("Debe iniciar sesión primero.");
    }

    private void requireAdmin() {
        ensureLogged();
        if (currentRole != Role.ADMIN) throw new SecurityException("Solo ADMIN puede realizar esta acción.");
    }

    private void registerOwner() {
        requireAdmin();
        RegisterOwnerRequest req = new RegisterOwnerRequest();
        System.out.print("Email: ");
        req.setEmail(scanner.nextLine());
        System.out.print("Password: ");
        req.setPassword(scanner.nextLine());
        System.out.print("Nombre completo: ");
        req.setFullName(scanner.nextLine());
        System.out.print("Teléfono: ");
        req.setPhone(scanner.nextLine());
        System.out.print("Dirección: ");
        req.setAddress(scanner.nextLine());
        var owner = authService.registerOwner(req);
        printlnGreen("Dueño registrado. ID: " + owner.getId());
    }

    private void registerVeterinarian() {
        requireAdmin();
        RegisterVeterinarianRequest req = new RegisterVeterinarianRequest();
        System.out.print("Email: ");
        req.setEmail(scanner.nextLine());
        System.out.print("Password: ");
        req.setPassword(scanner.nextLine());
        System.out.print("Nombre completo: ");
        req.setFullName(scanner.nextLine());
        System.out.print("Teléfono: ");
        req.setPhone(scanner.nextLine());
        System.out.print("Licencia: ");
        req.setLicenseNumber(scanner.nextLine());
        System.out.print("Especialización: ");
        req.setSpecialization(scanner.nextLine());
        var vet = authService.registerVeterinarian(req);
        printlnGreen("Veterinario registrado. ID: " + vet.getId());
    }

    private String resolveOwnerId() {
        if (currentRole == Role.ADMIN && currentUserId == null) return null;
        return ownerService.findByUserAccountId(currentUserId).getId();
    }

    private String resolveVetId() {
        return vetService.findByUserAccountId(currentUserId).getId();
    }

    private void registerPet() {
        ensureLogged();
        if (currentRole == Role.VETERINARIAN) {
            throw new SecurityException("Un veterinario no puede registrar mascotas.");
        }
        PetRequest req = new PetRequest();
        String ownerId = null;
        if (currentRole == Role.OWNER) {
            ownerId = resolveOwnerId();
            printlnGreen("Usando tu ownerId: " + ownerId);
        } else {
            System.out.print("ownerId: ");
            ownerId = scanner.nextLine();
        }
        req.setOwnerId(ownerId);
        System.out.print("Nombre: ");
        req.setName(scanner.nextLine());
        System.out.print("Especie (DOG/CAT/...): ");
        req.setSpecies(com.vetcarepro.domain.enums.PetSpecies.valueOf(scanner.nextLine().trim().toUpperCase()));
        System.out.print("Raza: ");
        req.setBreed(scanner.nextLine());
        System.out.print("¿Castrado? (true/false): ");
        req.setNeutered(Boolean.parseBoolean(scanner.nextLine()));
        Pet pet = petService.create(Pet.builder()
            .ownerId(req.getOwnerId())
            .name(req.getName())
            .species(req.getSpecies())
            .breed(req.getBreed())
            .neutered(req.isNeutered())
            .build());
        printlnGreen("Mascota registrada: " + pet.getId());
    }

    private void listPets() {
        ensureLogged();
        petService.findAll().forEach(p -> System.out.println(p.getId() + " - " + p.getName() + " (owner " + p.getOwnerId() + ")"));
    }

    private void appointmentsMenu() {
        ensureLogged();
        System.out.println("[1] Crear cita");
        System.out.println("[2] Ver citas");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        if ("1".equals(opt)) {
            createAppointment();
        } else if ("2".equals(opt)) {
            appointmentService.findAll().forEach(a -> System.out.println(a.getId() + " - " + a.getAppointmentDate() + " vet " + a.getVeterinarianId() + " pet " + a.getPetId()));
        }
    }

    private void createAppointment() {
        AppointmentRequest req = new AppointmentRequest();
        System.out.print("ownerId: ");
        req.setOwnerId(scanner.nextLine());
        System.out.print("petId: ");
        req.setPetId(scanner.nextLine());
        System.out.print("veterinarianId: ");
        req.setVeterinarianId(scanner.nextLine());
        System.out.print("type (CHECKUP/VACCINATION/...): ");
        req.setType(AppointmentType.valueOf(scanner.nextLine().trim().toUpperCase()));
        System.out.print("Fecha cita (ISO-8601, ej 2025-12-31T10:00:00): ");
        req.setAppointmentDate(LocalDateTime.parse(scanner.nextLine()));
        System.out.print("Motivo: ");
        req.setReason(scanner.nextLine());
        Appointment appt = appointmentService.create(Appointment.builder()
            .ownerId(req.getOwnerId())
            .petId(req.getPetId())
            .veterinarianId(req.getVeterinarianId())
            .type(req.getType())
            .appointmentDate(req.getAppointmentDate())
            .reason(req.getReason())
            .build());
        printlnGreen("Cita creada: " + appt.getId());
    }

    private void vaccinesMenu() {
        ensureLogged();
        System.out.println("[1] Registrar vacuna");
        System.out.println("[2] Listar vacunas");
        System.out.println("[3] Volver");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> {
                System.out.print("Nombre: ");
                String name = scanner.nextLine();
                System.out.print("Fabricante: ");
                String manufacturer = scanner.nextLine();
                var vaccine = vaccineService.create(com.vetcarepro.domain.entity.Vaccine.builder()
                    .name(name)
                    .manufacturer(manufacturer)
                    .build());
                printlnGreen("Vacuna registrada: " + vaccine.getId());
            }
            case "2" -> vaccineService.findAll().forEach(v -> System.out.println(v.getId() + " - " + v.getName()));
            default -> {}
        }
    }

    private void historyMenu() {
        ensureLogged();
        System.out.println("[1] Ver historial por petId");
        System.out.println("[2] Volver");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();
        if ("1".equals(opt)) {
            System.out.print("petId: ");
            String petId = scanner.nextLine();
            System.out.println("Historial: (placeholder) petId=" + petId);
        }
    }

    private void printlnGreen(String msg) { System.out.println(GREEN + msg + RESET); }
    private void printlnRed(String msg) { System.out.println(RED + msg + RESET); }
}
