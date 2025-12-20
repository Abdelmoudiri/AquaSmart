package com.aquasmart.userservice.repository;

import com.aquasmart.userservice.model.User;
import com.aquasmart.userservice.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des utilisateurs
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Recherche un utilisateur par email
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Recherche les utilisateurs par rôle
     * Utilisation d'une requête JPQL car 'roles' est une ElementCollection
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRoles(@Param("role") Role role);

    // Compatibilité gardée pour le service si nécessaire ou changement du service
    default List<User> findByRolesContaining(Role role) {
        return findByRoles(role);
    }

    /**
     * Recherche les utilisateurs actifs
     */
    List<User> findByEnabledTrue();

    /**
     * Recherche par nom de famille
     */
    List<User> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Recherche par ville
     */
    List<User> findByCity(String city);

    /**
     * Recherche par région
     */
    List<User> findByRegion(String region);

    /**
     * Recherche par nom d'organisation
     */
    List<User> findByOrganizationNameContainingIgnoreCase(String organizationName);
}
