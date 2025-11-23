package com.vetcarepro.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vetcarepro.domain.entity.UserAccount;
import com.vetcarepro.domain.enums.Role;
import com.vetcarepro.repository.UserAccountRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedAdmin(UserAccountRepository repository, PasswordEncoder encoder) {
        return args -> repository.findFirstByEmailIgnoreCase("admin@vetcarepro.local")
            .ifPresentOrElse(
                user -> {},
                () -> repository.save(UserAccount.builder()
                    .email("admin@vetcarepro.local")
                    .password(encoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build())
            );
    }
}
