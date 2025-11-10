package com.vetcarepro.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.Vaccine;

public interface VaccineRepository extends MongoRepository<Vaccine, String> {
    boolean existsByNameIgnoreCase(String name);
}
