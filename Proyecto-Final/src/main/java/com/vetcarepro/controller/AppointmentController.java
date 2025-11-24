package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.Appointment;
import com.vetcarepro.dto.AppointmentRequest;
import com.vetcarepro.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public List<Appointment> list() {
        return appointmentService.findAll();
    }

    @GetMapping("/owner/{ownerId}")
    public List<Appointment> listByOwner(@PathVariable String ownerId) {
        return appointmentService.findByOwner(ownerId);
    }

    @GetMapping("/veterinarian/{veterinarianId}")
    public List<Appointment> listByVeterinarian(@PathVariable String veterinarianId) {
        return appointmentService.findByVeterinarian(veterinarianId);
    }

    @GetMapping("/pet/{petId}")
    public List<Appointment> listByPet(@PathVariable String petId) {
        return appointmentService.findByPet(petId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(@RequestBody @Valid AppointmentRequest request) {
        Appointment appointment = Appointment.builder()
            .ownerId(request.getOwnerId())
            .petId(request.getPetId())
            .veterinarianId(request.getVeterinarianId())
            .type(request.getType())
            .appointmentDate(request.getAppointmentDate())
            .reason(request.getReason())
            .vaccineId(request.getVaccineId())
            .notes(request.getNotes())
            .build();
        return appointmentService.create(appointment);
    }

    @PostMapping("/{id}/complete")
    public Appointment complete(@PathVariable String id) {
        return appointmentService.completeAppointment(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        appointmentService.delete(id);
    }
}
