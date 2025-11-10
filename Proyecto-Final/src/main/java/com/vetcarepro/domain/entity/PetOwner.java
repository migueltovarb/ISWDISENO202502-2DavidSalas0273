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
@Document(collection = "pet_owners")
public class PetOwner {

    @Id
    private String id;

    private String userAccountId;

    private String fullName;

    private String email;

    private String phone;

    private String address;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
