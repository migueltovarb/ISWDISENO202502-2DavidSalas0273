package com.vetcarepro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vetcarepro.domain.entity.Vaccine;
import com.vetcarepro.exception.BusinessRuleException;
import com.vetcarepro.exception.ResourceNotFoundException;
import com.vetcarepro.repository.VaccineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VaccineService {

    private final VaccineRepository vaccineRepository;

    public Vaccine create(Vaccine vaccine) {
        if (vaccineRepository.existsByNameIgnoreCase(vaccine.getName())) {
            throw new BusinessRuleException("Vaccine already registered: " + vaccine.getName());
        }
        return vaccineRepository.save(vaccine);
    }

    public List<Vaccine> findAll() {
        return vaccineRepository.findAll();
    }

    public Vaccine findById(String id) {
        return vaccineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vaccine not found: " + id));
    }

    public Vaccine update(String id, Vaccine payload) {
        Vaccine existing = findById(id);
        existing.setName(payload.getName());
        existing.setManufacturer(payload.getManufacturer());
        existing.setDescription(payload.getDescription());
        existing.setValidityDays(payload.getValidityDays());
        existing.setReminderWindowDays(payload.getReminderWindowDays());
        return vaccineRepository.save(existing);
    }

    public void delete(String id) {
        vaccineRepository.deleteById(id);
    }
}
