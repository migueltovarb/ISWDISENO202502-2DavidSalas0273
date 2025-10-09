import java.util.List;
public class Main {

    public static void main(String[] args) {

        SistemaReservas sistema = new SistemaReservas();

        SalaEstudio sala1 = new SalaEstudio(101, 8);
        SalaEstudio sala2 = new SalaEstudio(102, 12);
        sistema.registrarNuevaSala(sala1);
        sistema.registrarNuevaSala(sala2);

        Estudiante estudiante1 = new Estudiante("Juan Pérez", "2021001", "Ingeniería de Sistemas");
        Estudiante estudiante2 = new Estudiante("María García", "2021002", "Medicina");
        sistema.registrarEstudiante(estudiante1);
        sistema.registrarEstudiante(estudiante2);

        System.out.println("\n--- Realizando Reservas ---");
        Reserva reserva1 = new Reserva(estudiante1, sala1, "2025-10-10", "14:00");
        sistema.reservarSala(reserva1);

        Reserva reserva2 = new Reserva(estudiante2, sala2, "2025-10-10", "15:00");
        sistema.reservarSala(reserva2);

        System.out.println("\n--- Intentando Reserva Duplicada ---");
        Reserva reserva3 = new Reserva(estudiante1, sala1, "2025-10-10", "14:00");
        sistema.reservarSala(reserva3);

        System.out.println("\n--- Salas Disponibles ---");

        List<SalaEstudio> disponibles = sistema.mostrarSalasDisponibles();
        for (SalaEstudio sala : disponibles) {
            System.out.println(sala);
        }

        // Consultar historial
        System.out.println("\n--- Historial de Reservas de " + estudiante1.getNombre() + " ---");
        List<Reserva> historial = sistema.consultarHistorial(estudiante1);
        for (Reserva reserva : historial) {
            System.out.println(reserva);
        }
    }
}