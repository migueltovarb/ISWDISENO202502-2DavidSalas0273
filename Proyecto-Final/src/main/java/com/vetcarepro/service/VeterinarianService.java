package com.vetcarepro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vetcarepro.domain.entity.Veterinarian;
import com.vetcarepro.exception.ResourceNotFoundException;
import com.vetcarepro.repository.VeterinarianRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;

    public Veterinarian create(Veterinarian veterinarian) {
        return veterinarianRepository.save(veterinarian);
    }

    public List<Veterinarian> findAll() {
        return veterinarianRepository.findAll();
    }

    public Veterinarian findById(String id) {
        return veterinarianRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found: " + id));
    }

    public Veterinarian findByUserAccountId(String userAccountId) {
        return veterinarianRepository.findByUserAccountId(userAccountId)
            .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found for user: " + userAccountId));
    }

    public Veterinarian update(String id, Veterinarian payload) {
        Veterinarian existing = findById(id);
        existing.setFullName(payload.getFullName());
        existing.setLicenseNumber(payload.getLicenseNumber());
        existing.setSpecialization(payload.getSpecialization());
        existing.setEmail(payload.getEmail());
        existing.setPhone(payload.getPhone());
        return veterinarianRepository.save(existing);
    }

    public void delete(String id) {
        veterinarianRepository.deleteById(id);
    }
}
