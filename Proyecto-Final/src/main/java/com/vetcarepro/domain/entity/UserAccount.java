package com.vetcarepro.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vetcarepro.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserAccount {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("username")
    private String email;

    private String password;

    private Role role;

    @Builder.Default
    private boolean enabled = true;

    private String referenceId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
