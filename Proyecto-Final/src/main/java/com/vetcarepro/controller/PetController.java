package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.Pet;
import com.vetcarepro.dto.PetRequest;
import com.vetcarepro.service.PetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Validated
public class PetController {

    private final PetService petService;

    @GetMapping
    public List<Pet> list() {
        return petService.findAll();
    }

    @GetMapping("/{id}")
    public Pet getById(@PathVariable String id) {
        return petService.findById(id);
    }

    @GetMapping("/owner/{ownerId}")
    public List<Pet> listByOwner(@PathVariable String ownerId) {
        return petService.findByOwner(ownerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet create(@RequestBody @Valid PetRequest request) {
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
}
