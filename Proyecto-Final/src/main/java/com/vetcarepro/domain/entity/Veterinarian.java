package com.vetcarepro.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "veterinarians")
public class Veterinarian {

    @Id
    private String id;

    private String userAccountId;

    private String fullName;

    private String licenseNumber;

    private String specialization;

    private String email;

    private String phone;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
