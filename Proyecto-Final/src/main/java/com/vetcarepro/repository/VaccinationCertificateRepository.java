package com.vetcarepro.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.VaccinationCertificate;

public interface VaccinationCertificateRepository extends MongoRepository<VaccinationCertificate, String> {
    Optional<VaccinationCertificate> findByAppointmentId(String appointmentId);

    List<VaccinationCertificate> findByExpirationDateBetween(LocalDate start, LocalDate end);

    List<VaccinationCertificate> findByPetId(String petId);
}
