package com.vetcarepro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vetcarepro.domain.entity.PetOwner;
import com.vetcarepro.exception.ResourceNotFoundException;
import com.vetcarepro.repository.PetOwnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetOwnerService {

    private final PetOwnerRepository petOwnerRepository;

    public PetOwner create(PetOwner owner) {
        return petOwnerRepository.save(owner);
    }

    public List<PetOwner> findAll() {
        return petOwnerRepository.findAll();
    }

    public PetOwner findById(String id) {
        return petOwnerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + id));
    }

    public PetOwner findByUserAccountId(String userAccountId) {
        return petOwnerRepository.findByUserAccountId(userAccountId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found for user: " + userAccountId));
    }

    public PetOwner update(String id, PetOwner owner) {
        PetOwner existing = findById(id);
        existing.setFullName(owner.getFullName());
        existing.setEmail(owner.getEmail());
        existing.setPhone(owner.getPhone());
        existing.setAddress(owner.getAddress());
        return petOwnerRepository.save(existing);
    }

    public void delete(String id) {
        petOwnerRepository.deleteById(id);
    }
}
