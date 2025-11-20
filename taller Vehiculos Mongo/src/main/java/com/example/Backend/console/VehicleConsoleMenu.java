package com.example.Backend.console;

import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.example.Backend.dto.VehicleDTO;
import com.example.Backend.service.VehicleService;

/**
 * Menu de consola para interactuar con VehicleService.
 */
@Component
public class VehicleConsoleMenu {

    private final VehicleService service;

    public VehicleConsoleMenu(VehicleService service) {
        this.service = service;
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            System.out.println("=== Cars CLI ===");
            while (running) {
                printOptions();
                String option = scanner.nextLine().trim();
                try {
                    switch (option) {
                        case "1" -> listVehicles();
                        case "2" -> getVehicleById(scanner);
                        case "3" -> createVehicle(scanner);
                        case "4" -> updateVehicle(scanner);
                        case "5" -> deleteVehicle(scanner);
                        case "0" -> running = false;
                        default -> System.out.println("Opción no válida.");
                    }
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
                System.out.println();
            }
        }
        System.out.println("Saliendo...");
    }

    private void printOptions() {
        System.out.println("Seleccione una opción:");
        System.out.println("1 - Listar vehículos");
        System.out.println("2 - Buscar por ID");
        System.out.println("3 - Crear vehículo");
        System.out.println("4 - Actualizar vehículo");
        System.out.println("5 - Eliminar vehículo");
        System.out.println("0 - Salir");
        System.out.print("> ");
    }

    private void listVehicles() {
        List<VehicleDTO> vehicles = service.getAll();
        if (vehicles.isEmpty()) {
            System.out.println("No hay vehículos registrados.");
            return;
        }
        vehicles.forEach(VehicleConsoleMenu::printVehicle);
    }

    private void getVehicleById(Scanner scanner) {
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        VehicleDTO dto = service.getById(id);
        printVehicle(dto);
    }

    private void createVehicle(Scanner scanner) {
        VehicleDTO dto = readVehicleData(scanner, null);
        VehicleDTO created = service.create(dto);
        System.out.println("Vehículo creado:");
        printVehicle(created);
    }

    private void updateVehicle(Scanner scanner) {
        System.out.print("ID del vehículo a actualizar: ");
        String id = scanner.nextLine().trim();
        VehicleDTO dto = readVehicleData(scanner, id);
        VehicleDTO updated = service.update(id, dto);
        System.out.println("Vehículo actualizado:");
        printVehicle(updated);
    }

    private void deleteVehicle(Scanner scanner) {
        System.out.print("ID del vehículo a eliminar: ");
        String id = scanner.nextLine().trim();
        service.delete(id);
        System.out.println("Vehículo eliminado.");
    }

    private VehicleDTO readVehicleData(Scanner scanner, String existingId) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(existingId);
        System.out.print("Marca: ");
        dto.setMarca(scanner.nextLine().trim());
        System.out.print("Modelo: ");
        dto.setModelo(scanner.nextLine().trim());
        dto.setAnio(readYear(scanner));
        System.out.print("Color: ");
        dto.setColor(scanner.nextLine().trim());
        return dto;
    }

    private int readYear(Scanner scanner) {
        while (true) {
            System.out.print("Año: ");
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Ingrese un año válido (número).");
            }
        }
    }

    private static void printVehicle(VehicleDTO dto) {
        System.out.printf("ID: %s | Marca: %s | Modelo: %s | Año: %d | Color: %s%n",
                dto.getId(), dto.getMarca(), dto.getModelo(), dto.getAnio(), dto.getColor());
    }
}
