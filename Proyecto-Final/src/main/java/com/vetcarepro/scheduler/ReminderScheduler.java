package com.vetcarepro.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vetcarepro.domain.entity.Appointment;
import com.vetcarepro.domain.entity.PetOwner;
import com.vetcarepro.domain.entity.VaccinationCertificate;
import com.vetcarepro.repository.VaccinationCertificateRepository;
import com.vetcarepro.service.AppointmentService;
import com.vetcarepro.service.PetOwnerService;
import com.vetcarepro.service.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final AppointmentService appointmentService;
    private final PetOwnerService ownerService;
    private final VaccinationCertificateRepository certificateRepository;
    private final NotificationService notificationService;

    @Value("${reminder.appointment-ahead-minutes:1440}")
    private long appointmentAheadMinutes;

    @Value("${reminder.vaccine-ahead-days:7}")
    private long vaccineAheadDays;

    @Scheduled(fixedDelayString = "${reminder.scheduler.delay:60000}")
    public void processReminders() {
        sendAppointmentReminders();
        sendVaccineReminders();
    }

    private void sendAppointmentReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusMinutes(appointmentAheadMinutes);
        List<Appointment> appointments = appointmentService.findPendingReminders(now, windowEnd);
        appointments.forEach(appointment -> {
            PetOwner owner = ownerService.findById(appointment.getOwnerId());
            String message = "Reminder: appointment for pet " + appointment.getPetId() + " at " + appointment.getAppointmentDate();
            notificationService.notifyOwner(owner, message);
            appointmentService.markReminderSent(appointment.getId());
        });
    }

    private void sendVaccineReminders() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(vaccineAheadDays);
        List<VaccinationCertificate> certificates = certificateRepository.findByExpirationDateBetween(start, end);
        certificates.forEach(certificate -> {
            PetOwner owner = ownerService.findById(certificate.getOwnerId());
            String message = "Reminder: vaccine for pet " + certificate.getPetId() + " expires on " + certificate.getExpirationDate();
            notificationService.notifyOwner(owner, message);
        });
    }
}
