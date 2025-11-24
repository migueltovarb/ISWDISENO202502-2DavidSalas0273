package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.Vaccine;
import com.vetcarepro.dto.VaccineRequest;
import com.vetcarepro.service.VaccineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
@Validated
public class VaccineController {

    private final VaccineService vaccineService;

    @GetMapping
    public List<Vaccine> list() {
        return vaccineService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vaccine create(@RequestBody @Valid VaccineRequest request) {
        Vaccine vaccine = Vaccine.builder()
            .name(request.getName())
            .manufacturer(request.getManufacturer())
            .description(request.getDescription())
            .validityDays(request.getValidityDays())
            .reminderWindowDays(request.getReminderWindowDays())
            .build();
        return vaccineService.create(vaccine);
    }
}
