package com.vetcarepro.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Document(collection = "vaccination_plans")
public class VaccinationPlan {

    @Id
    private String id;

    private String petId;

    private String veterinarianId;

    private String vaccineName;

    private LocalDate dueDate;

    private String notes;

    @Builder.Default
    private boolean completed = false;

    private LocalDate completionDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
