package com.vetcarepro.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medical_history")
public class MedicalHistoryRecord {

    @Id
    private String id;

    private String petId;

    private String veterinarianId;

    private LocalDateTime visitDate;

    private String summary;

    private String diagnosis;

    private List<String> treatments;

    private String recommendations;
}
