package com.aquasmart.userservice.controller;

import com.aquasmart.userservice.dto.request.ForgotPasswordRequest;
import com.aquasmart.userservice.dto.request.LoginRequest;
import com.aquasmart.userservice.dto.request.RefreshTokenRequest;
import com.aquasmart.userservice.dto.request.RegisterRequest;
import com.aquasmart.userservice.dto.request.ResetPasswordRequest;
import com.aquasmart.userservice.dto.response.AuthResponse;
import com.aquasmart.userservice.service.AuthService;
import com.aquasmart.userservice.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller pour l'authentification
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    /**
     * Inscription d'un nouvel utilisateur
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Requête d'inscription reçue pour: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Connexion d'un utilisateur
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Requête de connexion reçue pour: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Rafraîchit le token d'accès
     * POST /auth/refresh-token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Requête de refresh token reçue");
        AuthResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Déconnexion - invalide le refresh token
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestParam String userId) {
        log.info("Requête de déconnexion pour: {}", userId);
        authService.logout(userId);
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }

    /**
     * Demande de réinitialisation de mot de passe
     * POST /auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Requête de réinitialisation de mot de passe pour: {}", request.getEmail());
        passwordResetService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Si un compte existe avec cet email, un lien de réinitialisation a été envoyé"));
    }

    /**
     * Réinitialisation du mot de passe
     * POST /auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Requête de changement de mot de passe");
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
    }

    /**
     * Endpoint de santé
     * GET /auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service - Auth Controller OK");
    }
}
