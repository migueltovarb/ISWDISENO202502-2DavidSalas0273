package com.vetcarepro.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.VaccinationCertificate;
import com.vetcarepro.service.VaccinationCertificateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class VaccinationCertificateController {

    private final VaccinationCertificateService certificateService;

    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
        VaccinationCertificate certificate = certificateService.download(id);
        Path path = Path.of(certificate.getStoragePath());
        byte[] file = Files.exists(path) ? Files.readAllBytes(path) : certificate.getPdfContent();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName())
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(file.length)
            .body(new ByteArrayResource(file));
    }
}
