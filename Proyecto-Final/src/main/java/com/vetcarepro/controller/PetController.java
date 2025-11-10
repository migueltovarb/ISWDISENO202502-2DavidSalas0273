package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.vetcarepro.domain.entity.Pet;
import com.vetcarepro.dto.PetRequest;
import com.vetcarepro.service.PetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping
    public List<Pet> list(@RequestParam(required = false) String ownerId) {
        if (ownerId != null) {
            return petService.findByOwner(ownerId);
        }
        return petService.findAll();
    }

    @PostMapping
    public Pet create(@Valid @RequestBody PetRequest request) {
        Pet pet = Pet.builder()
            .ownerId(request.getOwnerId())
            .name(request.getName())
            .species(request.getSpecies())
            .breed(request.getBreed())
            .birthDate(request.getBirthDate())
            .microchipId(request.getMicrochipId())
            .neutered(request.isNeutered())
            .build();
        return petService.create(pet);
    }

    @GetMapping("/{id}")
    public Pet get(@PathVariable String id) {
        return petService.findById(id);
    }

    @PutMapping("/{id}")
    public Pet update(@PathVariable String id, @Valid @RequestBody PetRequest request) {
        Pet pet = Pet.builder()
            .ownerId(request.getOwnerId())
            .name(request.getName())
            .species(request.getSpecies())
            .breed(request.getBreed())
            .birthDate(request.getBirthDate())
            .microchipId(request.getMicrochipId())
            .neutered(request.isNeutered())
            .build();
        return petService.update(id, pet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
