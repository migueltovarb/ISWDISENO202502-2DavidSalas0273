package com.vetcarepro.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicalHistoryRequest {
    @NotBlank
    private String petId;
    @NotBlank
    private String veterinarianId;
    @NotNull
    private LocalDateTime visitDate;
    @NotBlank
    private String summary;
    private String diagnosis;
    private List<String> treatments;
    private String recommendations;
}
