package com.vetcarepro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VaccineRequest {
    @NotBlank
    private String name;
    private String manufacturer;
    private String description;
    @Min(1)
    private int validityDays;
    @Min(0)
    private int reminderWindowDays;
}
