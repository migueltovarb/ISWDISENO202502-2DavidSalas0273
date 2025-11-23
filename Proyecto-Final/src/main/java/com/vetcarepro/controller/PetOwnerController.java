package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.PetOwner;
import com.vetcarepro.service.PetOwnerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class PetOwnerController {

    private final PetOwnerService petOwnerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN')")
    public List<PetOwner> listAll() {
        return petOwnerService.findAll();
    }

    @GetMapping("/{id}")
    public PetOwner get(@PathVariable String id) {
        return petOwnerService.findById(id);
    }

    @PutMapping("/{id}")
    public PetOwner update(@PathVariable String id, @Valid @RequestBody PetOwner owner) {
        return petOwnerService.update(id, owner);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        petOwnerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
