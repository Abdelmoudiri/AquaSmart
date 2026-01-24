package com.aquasmart.userservice.repository;

import com.aquasmart.userservice.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour les tokens de réinitialisation de mot de passe
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.email = :email")
    void deleteByEmail(String email);

    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiryDate < CURRENT_TIMESTAMP")
    void deleteAllExpired();
}
