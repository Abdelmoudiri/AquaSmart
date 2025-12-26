package com.aquasmart.userservice.service;

import com.aquasmart.userservice.dto.request.LoginRequest;
import com.aquasmart.userservice.dto.request.RegisterRequest;
import com.aquasmart.userservice.dto.response.AuthResponse;
import com.aquasmart.userservice.exception.EmailAlreadyExistsException;
import com.aquasmart.userservice.model.User;
import com.aquasmart.userservice.model.enums.Role;
import com.aquasmart.userservice.repository.UserRepository;
import com.aquasmart.userservice.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Service d'authentification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Inscription d'un nouveau utilisateur
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Un compte avec cet email existe déjà");
        }

        // Créer l'utilisateur
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .roles(request.getRoles() != null ? request.getRoles() : getDefaultRoles())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .organizationName(request.getOrganizationName())
                .address(request.getAddress())
                .city(request.getCity())
                .region(request.getRegion())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .loginAttempts(0)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès: {}", savedUser.getEmail());

        // Générer le token JWT
        String token = generateToken(savedUser);

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRoles(),
                jwtUtil.getExpiration()
        );
    }

    /**
     * Connexion d'un utilisateur
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour l'email: {}", request.getEmail());

        // Authentifier l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mettre à jour la dernière connexion
        user.setLastLogin(LocalDateTime.now().toString());
        user.setLoginAttempts(0);
        userRepository.save(user);

        log.info("Connexion réussie pour: {}", user.getEmail());

        // Générer le token JWT
        String token = generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                jwtUtil.getExpiration()
        );
    }

    /**
     * Génère un token JWT avec les informations utilisateur
     */
    private String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getRoles());
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        
        return jwtUtil.generateToken(user.getEmail(), user.getId(), extraClaims);
    }

    /**
     * Rôles par défaut pour un nouvel utilisateur
     */
    private Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.AGRICULTEUR);
        return roles;
    }
}
