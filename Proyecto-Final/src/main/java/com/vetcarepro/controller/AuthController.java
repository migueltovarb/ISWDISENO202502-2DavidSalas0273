package com.vetcarepro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetcarepro.dto.AuthRequest;
import com.vetcarepro.dto.AuthResponse;
import com.vetcarepro.dto.RegisterOwnerRequest;
import com.vetcarepro.dto.RegisterVeterinarianRequest;
import com.vetcarepro.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/owner")
    public ResponseEntity<?> registerOwner(@Valid @RequestBody RegisterOwnerRequest request) {
        return ResponseEntity.ok(authService.registerOwner(request));
    }

    @PostMapping("/register/vet")
    public ResponseEntity<?> registerVeterinarian(@Valid @RequestBody RegisterVeterinarianRequest request) {
        return ResponseEntity.ok(authService.registerVeterinarian(request));
    }
}
