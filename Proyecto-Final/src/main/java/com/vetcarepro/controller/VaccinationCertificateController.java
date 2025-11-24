package com.vetcarepro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.dto.VaccinationCertificateResponse;
import com.vetcarepro.service.VaccinationCertificateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vaccination-certificates")
@RequiredArgsConstructor
public class VaccinationCertificateController {

    private final VaccinationCertificateService certificateService;

    @GetMapping("/{id}")
    public VaccinationCertificateResponse getCertificate(@PathVariable String id) {
        return certificateService.toResponse(certificateService.download(id));
    }

    @GetMapping("/pet/{petId}")
    public List<VaccinationCertificateResponse> listByPet(@PathVariable String petId) {
        return certificateService.findByPet(petId).stream()
            .map(certificateService::toResponse)
            .toList();
    }
}
