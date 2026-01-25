package com.aquasmart.userservice.service;

import com.aquasmart.userservice.exception.PasswordResetException;
import com.aquasmart.userservice.model.PasswordResetToken;
import com.aquasmart.userservice.model.User;
import com.aquasmart.userservice.repository.PasswordResetTokenRepository;
import com.aquasmart.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service pour la réinitialisation de mot de passe
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.reset-token-expiration:3600000}") // 1 heure par défaut
    private Long resetTokenDurationMs;

    /**
     * Crée un token de réinitialisation pour un email
     */
    @Transactional
    public String createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetException("Aucun compte trouvé avec cet email"));

        // Supprimer les anciens tokens pour cet email
        tokenRepository.deleteByEmail(email);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .email(email)
                .expiryDate(Instant.now().plusMillis(resetTokenDurationMs))
                .build();

        tokenRepository.save(resetToken);
        log.info("Token de réinitialisation créé pour: {}", email);

        // Dans un environnement de production, envoyer un email ici
        // Pour l'instant, on log le token (à remplacer par un vrai service d'email)
        log.info("=== TOKEN DE RÉINITIALISATION (DEV ONLY) ===");
        log.info("Email: {}", email);
        log.info("Token: {}", resetToken.getToken());
        log.info("Expire dans: 1 heure");
        log.info("URL: http://localhost:4200/reset-password?token={}", resetToken.getToken());
        log.info("============================================");

        return resetToken.getToken();
    }

    /**
     * Valide un token de réinitialisation
     */
    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new PasswordResetException("Token invalide"));

        if (!resetToken.isValid()) {
            throw new PasswordResetException("Le token a expiré ou a déjà été utilisé");
        }

        return resetToken;
    }

    /**
     * Réinitialise le mot de passe
     */
    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordResetException("Les mots de passe ne correspondent pas");
        }

        PasswordResetToken resetToken = validateToken(token);

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new PasswordResetException("Utilisateur non trouvé"));

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marquer le token comme utilisé
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Mot de passe réinitialisé avec succès pour: {}", user.getEmail());
    }

    /**
     * Nettoie les tokens expirés
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteAllExpired();
        log.info("Tokens de réinitialisation expirés nettoyés");
    }
}
