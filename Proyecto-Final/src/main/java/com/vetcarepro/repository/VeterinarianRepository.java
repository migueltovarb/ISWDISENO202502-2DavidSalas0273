package com.vetcarepro.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.Veterinarian;

public interface VeterinarianRepository extends MongoRepository<Veterinarian, String> {
    Optional<Veterinarian> findByLicenseNumber(String licenseNumber);
}
