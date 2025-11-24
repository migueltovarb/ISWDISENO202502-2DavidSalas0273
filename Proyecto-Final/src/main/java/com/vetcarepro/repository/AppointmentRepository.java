package com.vetcarepro.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.Appointment;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByOwnerId(String ownerId);

    List<Appointment> findByVeterinarianId(String veterinarianId);

    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByReminderSentFalseAndAppointmentDateBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPetIdAndAppointmentDateBetween(String petId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPetId(String petId);
}
