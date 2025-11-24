package com.vetcarepro.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vetcarepro.domain.entity.VaccinationPlan;
import com.vetcarepro.dto.VaccinationPlanRequest;
import com.vetcarepro.exception.ResourceNotFoundException;
import com.vetcarepro.repository.VaccinationPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VaccinationPlanService {

    private final VaccinationPlanRepository planRepository;

    public VaccinationPlan create(VaccinationPlanRequest request) {
        VaccinationPlan plan = VaccinationPlan.builder()
            .petId(request.getPetId())
            .veterinarianId(request.getVeterinarianId())
            .vaccineName(request.getVaccineName())
            .dueDate(request.getDueDate())
            .notes(request.getNotes())
            .build();
        return planRepository.save(plan);
    }

    public List<VaccinationPlan> findByPet(String petId) {
        return planRepository.findByPetId(petId);
    }

    public List<VaccinationPlan> findByVeterinarian(String veterinarianId) {
        return planRepository.findByVeterinarianId(veterinarianId);
    }

    public VaccinationPlan markCompleted(String id, LocalDate completionDate) {
        VaccinationPlan plan = planRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vaccination plan not found: " + id));
        plan.setCompleted(true);
        plan.setCompletionDate(completionDate != null ? completionDate : LocalDate.now());
        return planRepository.save(plan);
    }

    public void delete(String id) {
        planRepository.deleteById(id);
    }
}
