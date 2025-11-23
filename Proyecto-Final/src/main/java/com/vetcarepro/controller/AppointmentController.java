package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
import com.vetcarepro.domain.enums.Role;
import com.vetcarepro.dto.AppointmentRequest;
import com.vetcarepro.security.SecurityUtils;
import com.vetcarepro.security.UserAccountPrincipal;
import com.vetcarepro.service.AppointmentService;
import com.vetcarepro.service.PetOwnerService;
import com.vetcarepro.service.VeterinarianService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PetOwnerService petOwnerService;
    private final VeterinarianService veterinarianService;

    @GetMapping
    public List<Appointment> list(
        @RequestParam(required = false) String ownerId,
        @RequestParam(required = false) String vetId
    ) {
        UserAccountPrincipal currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            if (ownerId != null) {
                return appointmentService.findByOwner(ownerId);
            }
            if (vetId != null) {
                return appointmentService.findByVeterinarian(vetId);
            }
            return appointmentService.findAll();
        }
        if (currentUser.getRole() == Role.OWNER) {
            return appointmentService.findByOwner(resolveOwnerId(currentUser));
        }
        return appointmentService.findByVeterinarian(resolveVeterinarianId(currentUser));
    }

    @PostMapping
    public Appointment create(@Valid @RequestBody AppointmentRequest request) {
        UserAccountPrincipal currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole() == Role.OWNER) {
            String ownerId = resolveOwnerId(currentUser);
            if (!ownerId.equals(request.getOwnerId())) {
                throw new AccessDeniedException("Owners can only create appointments for themselves");
            }
        }
        if (currentUser.getRole() == Role.VETERINARIAN) {
            String veterinarianId = resolveVeterinarianId(currentUser);
            if (!veterinarianId.equals(request.getVeterinarianId())) {
                throw new AccessDeniedException("Veterinarians can only create their own appointments");
            }
        }
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
        Appointment existing = appointmentService.findById(id);
        UserAccountPrincipal currentUser = SecurityUtils.getCurrentUser();
        verifyAppointmentAccess(existing, currentUser);

        String ownerId = request.getOwnerId();
        String veterinarianId = request.getVeterinarianId();
        if (currentUser.getRole() == Role.OWNER) {
            ownerId = resolveOwnerId(currentUser);
        }
        if (currentUser.getRole() == Role.VETERINARIAN) {
            veterinarianId = resolveVeterinarianId(currentUser);
        }

        Appointment appointment = Appointment.builder()
            .ownerId(ownerId)
            .petId(request.getPetId())
            .veterinarianId(veterinarianId)
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
        Appointment appointment = appointmentService.findById(id);
        verifyAppointmentAccess(appointment, SecurityUtils.getCurrentUser());
        return appointmentService.completeAppointment(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        Appointment appointment = appointmentService.findById(id);
        verifyAppointmentAccess(appointment, SecurityUtils.getCurrentUser());
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void verifyAppointmentAccess(Appointment appointment, UserAccountPrincipal currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.OWNER) {
            String ownerId = resolveOwnerId(currentUser);
            if (!appointment.getOwnerId().equals(ownerId)) {
                throw new AccessDeniedException("Owners can only manage their own appointments");
            }
            return;
        }
        String veterinarianId = resolveVeterinarianId(currentUser);
        if (!appointment.getVeterinarianId().equals(veterinarianId)) {
            throw new AccessDeniedException("Veterinarians can only manage their own appointments");
        }
    }

    private String resolveOwnerId(UserAccountPrincipal currentUser) {
        if (currentUser.getReferenceId() != null) {
            return currentUser.getReferenceId();
        }
        return petOwnerService.findByUserAccountId(currentUser.getId()).getId();
    }

    private String resolveVeterinarianId(UserAccountPrincipal currentUser) {
        if (currentUser.getReferenceId() != null) {
            return currentUser.getReferenceId();
        }
        return veterinarianService.findByUserAccountId(currentUser.getId()).getId();
    }
}
