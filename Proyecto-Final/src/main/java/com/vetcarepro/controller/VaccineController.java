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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.vetcarepro.domain.entity.Vaccine;
import com.vetcarepro.dto.VaccineRequest;
import com.vetcarepro.service.VaccineService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
public class VaccineController {

    private final VaccineService vaccineService;

    @GetMapping
    public List<Vaccine> list() {
        return vaccineService.findAll();
    }

    @PostMapping
    public Vaccine create(@Valid @RequestBody VaccineRequest request) {
        Vaccine vaccine = Vaccine.builder()
            .name(request.getName())
            .manufacturer(request.getManufacturer())
            .description(request.getDescription())
            .validityDays(request.getValidityDays())
            .reminderWindowDays(request.getReminderWindowDays())
            .build();
        return vaccineService.create(vaccine);
    }

    @GetMapping("/{id}")
    public Vaccine get(@PathVariable String id) {
        return vaccineService.findById(id);
    }

    @PutMapping("/{id}")
    public Vaccine update(@PathVariable String id, @Valid @RequestBody VaccineRequest request) {
        Vaccine payload = Vaccine.builder()
            .name(request.getName())
            .manufacturer(request.getManufacturer())
            .description(request.getDescription())
            .validityDays(request.getValidityDays())
            .reminderWindowDays(request.getReminderWindowDays())
            .build();
        return vaccineService.update(id, payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vaccineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
