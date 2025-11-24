package com.vetcarepro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.domain.entity.PetOwner;
import com.vetcarepro.domain.entity.Veterinarian;
import com.vetcarepro.dto.AuthRequest;
import com.vetcarepro.dto.AuthResponse;
import com.vetcarepro.dto.RegisterOwnerRequest;
import com.vetcarepro.dto.RegisterVeterinarianRequest;
import com.vetcarepro.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register-owner")
    @ResponseStatus(HttpStatus.CREATED)
    public PetOwner registerOwner(@RequestBody @Valid RegisterOwnerRequest request) {
        return authService.registerOwner(request);
    }

    @PostMapping("/register-veterinarian")
    @ResponseStatus(HttpStatus.CREATED)
    public Veterinarian registerVeterinarian(@RequestBody @Valid RegisterVeterinarianRequest request) {
        return authService.registerVeterinarian(request);
    }
}
