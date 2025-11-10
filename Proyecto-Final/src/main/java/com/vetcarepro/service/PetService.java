package com.vetcarepro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vetcarepro.domain.entity.Pet;
import com.vetcarepro.exception.ResourceNotFoundException;
import com.vetcarepro.repository.PetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public Pet create(Pet pet) {
        return petRepository.save(pet);
    }

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public List<Pet> findByOwner(String ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    public Pet findById(String id) {
        return petRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pet not found: " + id));
    }

    public Pet update(String id, Pet pet) {
        Pet existing = findById(id);
        existing.setName(pet.getName());
        existing.setSpecies(pet.getSpecies());
        existing.setBreed(pet.getBreed());
        existing.setBirthDate(pet.getBirthDate());
        existing.setMicrochipId(pet.getMicrochipId());
        existing.setNeutered(pet.isNeutered());
        return petRepository.save(existing);
    }

    public void delete(String id) {
        petRepository.deleteById(id);
    }
}
