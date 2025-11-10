package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.vetcarepro.domain.entity.MedicalHistoryRecord;
import com.vetcarepro.dto.MedicalHistoryRequest;
import com.vetcarepro.service.MedicalHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medical-history")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    @GetMapping
    public List<MedicalHistoryRecord> list(@RequestParam String petId) {
        return medicalHistoryService.findByPet(petId);
    }

    @PostMapping
    public MedicalHistoryRecord create(@Valid @RequestBody MedicalHistoryRequest request) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        medicalHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
