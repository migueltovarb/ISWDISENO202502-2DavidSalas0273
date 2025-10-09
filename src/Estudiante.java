import java.util.ArrayList;
import java.util.List;

class Estudiante {
    private String nombre;
    private String codigoInstitucional;
    private String programaAcademico;

    public Estudiante(String nombre, String codigo, String programa) {
        this.nombre = nombre;
        this.codigoInstitucional = codigo;
        this.programaAcademico = programa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoInstitucional() {
        return codigoInstitucional;
    }

    public void setCodigoInstitucional(String codigo) {
        this.codigoInstitucional = codigo;
    }

    public String getProgramaAcademico() {
        return programaAcademico;
    }

    public void setProgramaAcademico(String programa) {
        this.programaAcademico = programa;
    }

    public void registrar() {
        System.out.println("Estudiante registrado: " + this.toString());
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "nombre='" + nombre + '\'' +
                ", codigo='" + codigoInstitucional + '\'' +
                ", programa='" + programaAcademico + '\'' +
                '}';
    }
}

class SalaEstudio {
    private int numeroSala;
    private int capacidadMaxima;
    private boolean disponible;

    public SalaEstudio(int numero, int capacidad) {
        this.numeroSala = numero;
        this.capacidadMaxima = capacidad;
        this.disponible = true;
    }

    public int getNumeroSala() {
        return numeroSala;
    }

    public void setNumeroSala(int numero) {
        this.numeroSala = numero;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidad) {
        this.capacidadMaxima = capacidad;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean verificarDisponibilidad(String fecha, String hora) {
        return this.disponible;
    }

    public void registrar() {
        System.out.println("Sala registrada: " + this.toString());
    }

    @Override
    public String toString() {
        return "SalaEstudio{" +
                "numero=" + numeroSala +
                ", capacidad=" + capacidadMaxima +
                ", disponible=" + disponible +
                '}';
    }
}

class Reserva {
    private Estudiante estudiante;
    private SalaEstudio sala;
    private String fecha;
    private String hora;

    public Reserva(Estudiante estudiante, SalaEstudio sala, String fecha, String hora) {
        this.estudiante = estudiante;
        this.sala = sala;
        this.fecha = fecha;
        this.hora = hora;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public SalaEstudio getSala() {
        return sala;
    }

    public void setSala(SalaEstudio sala) {
        this.sala = sala;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void crear() {
        System.out.println("Reserva creada: " + this.toString());
    }

    public boolean validar() {
        return estudiante != null && sala != null &&
                fecha != null && !fecha.isEmpty() &&
                hora != null && !hora.isEmpty();
    }

    public void cancelar() {
        System.out.println("Reserva cancelada: " + this.toString());
        sala.setDisponible(true);
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "estudiante=" + estudiante.getNombre() +
                ", sala=" + sala.getNumeroSala() +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                '}';
    }
}


class SistemaReservas {
    private List<SalaEstudio> salas;
    private List<Estudiante> estudiantes;
    private List<Reserva> reservas;

    public SistemaReservas() {
        this.salas = new ArrayList<>();
        this.estudiantes = new ArrayList<>();
        this.reservas = new ArrayList<>();
    }

    public List<SalaEstudio> getSalas() {
        return salas;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void registrarNuevaSala(SalaEstudio sala) {
        salas.add(sala);
        sala.registrar();
    }

    public void registrarEstudiante(Estudiante estudiante) {
        estudiantes.add(estudiante);
        estudiante.registrar();
    }

    public void reservarSala(Reserva reserva) {
        if (!validarCamposCompletos(reserva)) {
            System.out.println("Error: Campos vacíos en la reserva");
            return;
        }

        if (!validarDisponibilidad(reserva.getSala(), reserva.getFecha(), reserva.getHora())) {
            System.out.println("Error: Sala no disponible en esa fecha y hora");
            return;
        }

        if (!validarNoDuplicacion(reserva.getEstudiante(), reserva.getSala(),
                reserva.getFecha(), reserva.getHora())) {
            System.out.println("Error: Reserva duplicada");
            return;
        }

        reservas.add(reserva);
        reserva.getSala().setDisponible(false);
        reserva.crear();
    }

    public List<Reserva> consultarHistorial(Estudiante estudiante) {
        List<Reserva> historial = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getEstudiante().equals(estudiante)) {
                historial.add(reserva);
            }
        }
        return historial;
    }

    public List<SalaEstudio> mostrarSalasDisponibles() {
        List<SalaEstudio> disponibles = new ArrayList<>();
        for (SalaEstudio sala : salas) {
            if (sala.isDisponible()) {
                disponibles.add(sala);
            }
        }
        return disponibles;
    }

    public boolean validarDisponibilidad(SalaEstudio sala, String fecha, String hora) {
        for (Reserva reserva : reservas) {
            if (reserva.getSala().equals(sala) &&
                    reserva.getFecha().equals(fecha) &&
                    reserva.getHora().equals(hora)) {
                return false;
            }
        }
        return true;
    }

    public boolean validarNoDuplicacion(Estudiante estudiante, SalaEstudio sala,
                                        String fecha, String hora) {
        for (Reserva reserva : reservas) {
            if (reserva.getEstudiante().equals(estudiante) &&
                    reserva.getSala().equals(sala) &&
                    reserva.getFecha().equals(fecha) &&
                    reserva.getHora().equals(hora)) {
                return false;
            }
        }
        return true;
    }

    public boolean validarCamposCompletos(Reserva reserva) {
        return reserva.validar();
    }
}
