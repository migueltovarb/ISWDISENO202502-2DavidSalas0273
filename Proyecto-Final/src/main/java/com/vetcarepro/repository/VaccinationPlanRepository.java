package com.vetcarepro.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.VaccinationPlan;

public interface VaccinationPlanRepository extends MongoRepository<VaccinationPlan, String> {

    List<VaccinationPlan> findByPetId(String petId);

    List<VaccinationPlan> findByVeterinarianId(String veterinarianId);
}
