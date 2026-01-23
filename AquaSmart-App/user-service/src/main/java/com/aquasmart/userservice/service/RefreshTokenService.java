package com.aquasmart.userservice.service;

import com.aquasmart.userservice.exception.TokenRefreshException;
import com.aquasmart.userservice.model.RefreshToken;
import com.aquasmart.userservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour gérer les refresh tokens
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration:604800000}") // 7 jours par défaut
    private Long refreshTokenDurationMs;

    /**
     * Crée un nouveau refresh token pour un utilisateur
     */
    @Transactional
    public RefreshToken createRefreshToken(String userId) {
        // Supprimer les anciens refresh tokens de cet utilisateur
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token créé pour l'utilisateur: {}", userId);

        return refreshToken;
    }

    /**
     * Trouve un refresh token par sa valeur
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Vérifie si le refresh token est valide (non expiré)
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Le refresh token a expiré. Veuillez vous reconnecter.");
        }
        return token;
    }

    /**
     * Supprime les refresh tokens d'un utilisateur
     */
    @Transactional
    public void deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Refresh tokens supprimés pour l'utilisateur: {}", userId);
    }

    /**
     * Nettoie les tokens expirés
     */
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpired();
        log.info("Tokens expirés nettoyés");
    }
}
