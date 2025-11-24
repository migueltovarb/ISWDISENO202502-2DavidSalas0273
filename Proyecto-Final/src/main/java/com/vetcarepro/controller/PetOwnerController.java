package com.vetcarepro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.PetOwner;
import com.vetcarepro.service.PetOwnerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class PetOwnerController {

    private final PetOwnerService petOwnerService;

    @GetMapping
    public List<PetOwner> list() {
        return petOwnerService.findAll();
    }
}
