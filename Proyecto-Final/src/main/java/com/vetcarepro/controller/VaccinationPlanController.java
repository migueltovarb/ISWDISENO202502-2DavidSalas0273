package com.vetcarepro.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.VaccinationPlan;
import com.vetcarepro.dto.VaccinationPlanRequest;
import com.vetcarepro.service.VaccinationPlanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vaccination-plans")
@RequiredArgsConstructor
@Validated
public class VaccinationPlanController {

    private final VaccinationPlanService planService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VaccinationPlan create(@RequestBody @Valid VaccinationPlanRequest request) {
        return planService.create(request);
    }

    @GetMapping("/pet/{petId}")
    public List<VaccinationPlan> listByPet(@PathVariable String petId) {
        return planService.findByPet(petId);
    }

    @GetMapping("/veterinarian/{veterinarianId}")
    public List<VaccinationPlan> listByVeterinarian(@PathVariable String veterinarianId) {
        return planService.findByVeterinarian(veterinarianId);
    }

    @PostMapping("/{id}/complete")
    public VaccinationPlan markCompleted(
        @PathVariable String id,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completionDate
    ) {
        return planService.markCompleted(id, completionDate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        planService.delete(id);
    }
}
