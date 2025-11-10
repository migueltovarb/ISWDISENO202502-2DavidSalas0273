package com.vetcarepro.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.PetOwner;

public interface PetOwnerRepository extends MongoRepository<PetOwner, String> {
    Optional<PetOwner> findByUserAccountId(String userAccountId);

    boolean existsByEmail(String email);
}
