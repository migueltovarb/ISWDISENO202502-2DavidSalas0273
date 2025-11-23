package com.vetcarepro.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vetcarepro.domain.entity.UserAccount;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findFirstByEmailIgnoreCase(String email);
}
