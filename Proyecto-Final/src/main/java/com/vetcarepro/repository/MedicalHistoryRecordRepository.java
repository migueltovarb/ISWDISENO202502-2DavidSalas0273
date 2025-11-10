package com.vetcarepro.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.MedicalHistoryRecord;

public interface MedicalHistoryRecordRepository extends MongoRepository<MedicalHistoryRecord, String> {
    List<MedicalHistoryRecord> findByPetId(String petId);
}
