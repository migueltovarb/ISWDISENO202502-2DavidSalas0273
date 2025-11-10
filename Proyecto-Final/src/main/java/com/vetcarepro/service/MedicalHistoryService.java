package com.vetcarepro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vetcarepro.domain.entity.MedicalHistoryRecord;
import com.vetcarepro.exception.ResourceNotFoundException;
import com.vetcarepro.repository.MedicalHistoryRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicalHistoryService {

    private final MedicalHistoryRecordRepository medicalHistoryRecordRepository;

    public MedicalHistoryRecord create(MedicalHistoryRecord record) {
        return medicalHistoryRecordRepository.save(record);
    }

    public List<MedicalHistoryRecord> findByPet(String petId) {
        return medicalHistoryRecordRepository.findByPetId(petId);
    }

    public MedicalHistoryRecord findById(String id) {
        return medicalHistoryRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Medical history not found: " + id));
    }

    public void delete(String id) {
        medicalHistoryRecordRepository.deleteById(id);
    }
}
