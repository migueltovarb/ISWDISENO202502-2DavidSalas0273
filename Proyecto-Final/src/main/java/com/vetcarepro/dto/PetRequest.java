package com.vetcarepro.dto;

import java.time.LocalDate;

import com.vetcarepro.domain.enums.PetSpecies;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PetRequest {
    @NotBlank
    private String ownerId;
    @NotBlank
    private String name;
    @NotNull
    private PetSpecies species;
    private String breed;
    private LocalDate birthDate;
    private String microchipId;
    private boolean neutered;
}
