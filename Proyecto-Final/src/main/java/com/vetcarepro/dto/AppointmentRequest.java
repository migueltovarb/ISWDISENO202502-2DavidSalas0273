package com.vetcarepro.dto;

import java.time.LocalDateTime;

import com.vetcarepro.domain.enums.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AppointmentRequest {
    @NotBlank
    private String ownerId;
    @NotBlank
    private String petId;
    @NotBlank
    private String veterinarianId;
    @NotNull
    private AppointmentType type;
    @NotNull
    @Future
    private LocalDateTime appointmentDate;
    private String reason;
    private String vaccineId;
    private String notes;
}
