package com.vetcarepro.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vetcarepro.domain.enums.PetSpecies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pets")
public class Pet {

    @Id
    private String id;

    private String ownerId;

    private String name;

    private PetSpecies species;

    private String breed;

    private LocalDate birthDate;

    private String microchipId;

    private boolean neutered;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
