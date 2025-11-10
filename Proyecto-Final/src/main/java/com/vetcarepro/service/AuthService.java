package com.vetcarepro.service;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtService.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal(), Map.of());
        Role role = userRepository.findFirstByUsernameIgnoreCase(request.getUsername())
            .map(UserAccount::getRole)
            .orElse(Role.OWNER);
        return new AuthResponse(token, role);
    }

    public PetOwner registerOwner(RegisterOwnerRequest request) {
        ensureUsernameAvailable(request.getUsername());
        UserAccount user = UserAccount.builder()
            .username(request.getUsername())
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
        return petOwnerService.create(owner);
    }

    public Veterinarian registerVeterinarian(RegisterVeterinarianRequest request) {
        ensureUsernameAvailable(request.getUsername());
        UserAccount user = UserAccount.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.VET)
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
        return veterinarianService.create(veterinarian);
    }

    private void ensureUsernameAvailable(String username) {
        userRepository.findFirstByUsernameIgnoreCase(username)
            .ifPresent(existing -> {
                throw new BusinessRuleException("Username already registered: " + username);
            });
    }
}
