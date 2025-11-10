package com.vetcarepro.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetcarepro.domain.entity.Appointment;
import com.vetcarepro.domain.entity.VaccinationCertificate;
import com.vetcarepro.domain.enums.AppointmentStatus;
import com.vetcarepro.domain.enums.AppointmentType;
import com.vetcarepro.exception.BusinessRuleException;
import com.vetcarepro.exception.ResourceNotFoundException;
import com.vetcarepro.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final VaccinationCertificateService certificateService;

    public Appointment create(Appointment appointment) {
        BusinessRuleChecks.validateAppointment(appointment);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Appointment findById(String id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
    }

    public Appointment update(String id, Appointment payload) {
        Appointment existing = findById(id);
        existing.setAppointmentDate(payload.getAppointmentDate());
        existing.setVeterinarianId(payload.getVeterinarianId());
        existing.setReason(payload.getReason());
        existing.setNotes(payload.getNotes());
        existing.setType(payload.getType());
        existing.setVaccineId(payload.getVaccineId());
        BusinessRuleChecks.validateAppointment(existing);
        return appointmentRepository.save(existing);
    }

    @Transactional
    public Appointment completeAppointment(String id) {
        Appointment appointment = findById(id);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Appointment already completed");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        if (appointment.getType() == AppointmentType.VACCINATION) {
            VaccinationCertificate certificate = certificateService.generateFromAppointment(appointment);
            log.info("Certificate {} generated for appointment {}", certificate.getId(), appointment.getId());
        }
        return appointment;
    }

    public void delete(String id) {
        appointmentRepository.deleteById(id);
    }

    public void markReminderSent(String id) {
        Appointment appointment = findById(id);
        appointment.setReminderSent(true);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> findUpcoming(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateBetween(start, end);
    }

    public List<Appointment> findPendingReminders(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByReminderSentFalseAndAppointmentDateBetween(start, end);
    }

    public List<Appointment> findByOwner(String ownerId) {
        return appointmentRepository.findByOwnerId(ownerId);
    }

    public List<Appointment> findByVeterinarian(String veterinarianId) {
        return appointmentRepository.findByVeterinarianId(veterinarianId);
    }

    private static class BusinessRuleChecks {
        static void validateAppointment(Appointment appointment) {
            if (appointment.getType() == AppointmentType.VACCINATION && appointment.getVaccineId() == null) {
                throw new BusinessRuleException("Vaccine must be specified for vaccination appointments");
            }
        }
    }
}
