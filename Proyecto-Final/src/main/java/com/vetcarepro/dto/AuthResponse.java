package com.vetcarepro.dto;

import com.vetcarepro.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String userId;
    private String email;
    private String fullName;
    private Role role;
    private String referenceId;
}
