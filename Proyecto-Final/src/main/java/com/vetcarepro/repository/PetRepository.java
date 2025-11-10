package com.vetcarepro.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.Pet;

public interface PetRepository extends MongoRepository<Pet, String> {
    List<Pet> findByOwnerId(String ownerId);
}
