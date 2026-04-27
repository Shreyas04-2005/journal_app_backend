package com.journalApp.repository;

import com.journalApp.entity.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordRepository extends MongoRepository<PasswordResetToken,String> {

    Optional<PasswordResetToken> findByToken(String token);
}
