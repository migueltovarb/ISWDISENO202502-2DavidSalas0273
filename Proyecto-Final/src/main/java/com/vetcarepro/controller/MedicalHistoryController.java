package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.MedicalHistoryRecord;
import com.vetcarepro.dto.MedicalHistoryRequest;
import com.vetcarepro.service.MedicalHistoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medical-history")
@RequiredArgsConstructor
@Validated
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    @GetMapping
    public List<MedicalHistoryRecord> listByPet(@RequestParam @NotBlank String petId) {
        return medicalHistoryService.findByPet(petId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalHistoryRecord create(@RequestBody @Valid MedicalHistoryRequest request) {
        MedicalHistoryRecord record = MedicalHistoryRecord.builder()
            .petId(request.getPetId())
            .veterinarianId(request.getVeterinarianId())
            .visitDate(request.getVisitDate())
            .summary(request.getSummary())
            .diagnosis(request.getDiagnosis())
            .treatments(request.getTreatments())
            .recommendations(request.getRecommendations())
            .build();
        return medicalHistoryService.create(record);
    }
}
