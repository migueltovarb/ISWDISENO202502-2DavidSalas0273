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

import com.vetcarepro.domain.entity.Veterinarian;
import com.vetcarepro.service.VeterinarianService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/veterinarians")
@RequiredArgsConstructor
public class VeterinarianController {

    private final VeterinarianService veterinarianService;

    @GetMapping
    public List<Veterinarian> list() {
        return veterinarianService.findAll();
    }

    @PostMapping
    public Veterinarian create(@RequestBody Veterinarian veterinarian) {
        return veterinarianService.create(veterinarian);
    }

    @GetMapping("/{id}")
    public Veterinarian get(@PathVariable String id) {
        return veterinarianService.findById(id);
    }

    @PutMapping("/{id}")
    public Veterinarian update(@PathVariable String id, @RequestBody Veterinarian payload) {
        return veterinarianService.update(id, payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        veterinarianService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
