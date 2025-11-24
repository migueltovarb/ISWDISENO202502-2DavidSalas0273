package com.vetcarepro.service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userRepository;
    private final PetOwnerService petOwnerService;
    private final VeterinarianService veterinarianService;

    public AuthResponse login(AuthRequest request) {
        UserAccount user = userRepository.findFirstByEmailIgnoreCase(request.getEmail())
            .orElseThrow(() -> new BusinessRuleException("Usuario o contraseña inválidos"));
        if (!user.isEnabled()) {
            throw new BusinessRuleException("Cuenta deshabilitada");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessRuleException("Usuario o contraseña inválidos");
        }
        return new AuthResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getReferenceId());
    }

    public PetOwner registerOwner(RegisterOwnerRequest request) {
        ensureEmailAvailable(request.getEmail());
        UserAccount user = UserAccount.builder()
            .email(request.getEmail())
            .fullName(request.getFullName())
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
            .fullName(request.getFullName())
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

    public UserAccount findByEmail(String email) {
        return userRepository.findFirstByEmailIgnoreCase(email)
            .orElseThrow(() -> new BusinessRuleException("Usuario no encontrado: " + email));
    }
}
