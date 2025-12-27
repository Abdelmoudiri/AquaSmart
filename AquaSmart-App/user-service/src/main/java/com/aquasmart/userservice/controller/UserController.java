package com.aquasmart.userservice.controller;

import com.aquasmart.userservice.dto.response.UserDTO;
import com.aquasmart.userservice.model.enums.Role;
import com.aquasmart.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion des utilisateurs
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Récupère tous les utilisateurs
     * GET /users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère un utilisateur par ID
     * GET /users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        log.info("Récupération de l'utilisateur: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupère un utilisateur par email
     * GET /users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        log.info("Récupération de l'utilisateur par email: {}", email);
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupère les utilisateurs par rôle
     * GET /users/role/{role}
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable Role role) {
        log.info("Récupération des utilisateurs avec le rôle: {}", role);
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère les utilisateurs actifs
     * GET /users/active
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        log.info("Récupération des utilisateurs actifs");
        List<UserDTO> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère les utilisateurs par région
     * GET /users/region/{region}
     */
    @GetMapping("/region/{region}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ONG')")
    public ResponseEntity<List<UserDTO>> getUsersByRegion(@PathVariable String region) {
        log.info("Récupération des utilisateurs de la région: {}", region);
        List<UserDTO> users = userService.getUsersByRegion(region);
        return ResponseEntity.ok(users);
    }

    /**
     * Met à jour un utilisateur
     * PUT /users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String id,
            @RequestBody UserDTO userDTO) {
        log.info("Mise à jour de l'utilisateur: {}", id);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Active/désactive un utilisateur
     * PATCH /users/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> toggleUserStatus(@PathVariable String id) {
        log.info("Changement de statut pour l'utilisateur: {}", id);
        UserDTO user = userService.toggleUserStatus(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Ajoute un rôle à un utilisateur
     * POST /users/{id}/roles/{role}
     */
    @PostMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> addRole(
            @PathVariable String id,
            @PathVariable Role role) {
        log.info("Ajout du rôle {} à l'utilisateur: {}", role, id);
        UserDTO user = userService.addRoleToUser(id, role);
        return ResponseEntity.ok(user);
    }

    /**
     * Supprime un rôle d'un utilisateur
     * DELETE /users/{id}/roles/{role}
     */
    @DeleteMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> removeRole(
            @PathVariable String id,
            @PathVariable Role role) {
        log.info("Suppression du rôle {} de l'utilisateur: {}", role, id);
        UserDTO user = userService.removeRoleFromUser(id, role);
        return ResponseEntity.ok(user);
    }

    /**
     * Supprime un utilisateur
     * DELETE /users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("Suppression de l'utilisateur: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
