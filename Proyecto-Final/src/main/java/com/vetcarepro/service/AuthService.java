package com.vetcarepro.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vetcarepro.domain.entity.PetOwner;
import com.vetcarepro.domain.entity.UserAccount;
import com.vetcarepro.domain.entity.Veterinarian;
import com.vetcarepro.domain.enums.Role;
import com.vetcarepro.dto.AuthRequest;
import com.vetcarepro.dto.AuthResponse;
import com.vetcarepro.dto.RegisterOwnerRequest;
import com.vetcarepro.dto.RegisterVeterinarianRequest;
import com.vetcarepro.exception.BusinessRuleException;
import com.vetcarepro.repository.UserAccountRepository;
import com.vetcarepro.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userRepository;
    private final PetOwnerService petOwnerService;
    private final VeterinarianService veterinarianService;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        UserAccount user = userRepository.findFirstByEmailIgnoreCase(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getRole(), user.getId());
    }

    public PetOwner registerOwner(RegisterOwnerRequest request) {
        ensureEmailAvailable(request.getEmail());
        UserAccount user = UserAccount.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.OWNER)
            .build();
        user = userRepository.save(user);
        PetOwner owner = PetOwner.builder()
            .userAccountId(user.getId())
            .fullName(request.getFullName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .build();
        owner = petOwnerService.create(owner);
        user.setReferenceId(owner.getId());
        userRepository.save(user);
        return owner;
    }

    public Veterinarian registerVeterinarian(RegisterVeterinarianRequest request) {
        ensureEmailAvailable(request.getEmail());
        UserAccount user = UserAccount.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.VETERINARIAN)
            .build();
        user = userRepository.save(user);
        Veterinarian veterinarian = Veterinarian.builder()
            .userAccountId(user.getId())
            .fullName(request.getFullName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .licenseNumber(request.getLicenseNumber())
            .specialization(request.getSpecialization())
            .build();
        veterinarian = veterinarianService.create(veterinarian);
        user.setReferenceId(veterinarian.getId());
        userRepository.save(user);
        return veterinarian;
    }

    private void ensureEmailAvailable(String email) {
        userRepository.findFirstByEmailIgnoreCase(email)
            .ifPresent(existing -> {
                throw new BusinessRuleException("Email already registered: " + email);
            });
    }
}
