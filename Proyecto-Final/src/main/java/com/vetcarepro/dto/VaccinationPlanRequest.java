package com.vetcarepro.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VaccinationPlanRequest {
    @NotBlank
    private String petId;
    @NotBlank
    private String veterinarianId;
    @NotBlank
    private String vaccineName;
    @NotNull
    @FutureOrPresent
    private LocalDate dueDate;
    private String notes;
}
