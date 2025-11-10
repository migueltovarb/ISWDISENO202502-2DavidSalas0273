package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.vetcarepro.domain.entity.Appointment;
import com.vetcarepro.dto.AppointmentRequest;
import com.vetcarepro.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public List<Appointment> list(
        @RequestParam(required = false) String ownerId,
        @RequestParam(required = false) String vetId
    ) {
        if (ownerId != null) {
            return appointmentService.findByOwner(ownerId);
        }
        if (vetId != null) {
            return appointmentService.findByVeterinarian(vetId);
        }
        return appointmentService.findAll();
    }

    @PostMapping
    public Appointment create(@Valid @RequestBody AppointmentRequest request) {
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

    @PutMapping("/{id}")
    public Appointment update(@PathVariable String id, @Valid @RequestBody AppointmentRequest request) {
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
        return appointmentService.update(id, appointment);
    }

    @PostMapping("/{id}/complete")
    public Appointment complete(@PathVariable String id) {
        return appointmentService.completeAppointment(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
