package com.aquasmart.userservice.service;

import com.aquasmart.userservice.dto.mapper.UserMapper;
import com.aquasmart.userservice.dto.response.UserDTO;
import com.aquasmart.userservice.exception.UserNotFoundException;
import com.aquasmart.userservice.model.User;
import com.aquasmart.userservice.model.enums.Role;
import com.aquasmart.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des utilisateurs
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Récupère un utilisateur par son ID
     */
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        return userMapper.toDTO(user);
    }

    /**
     * Récupère un utilisateur par son email
     */
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        return userMapper.toDTO(user);
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }

    /**
     * Récupère les utilisateurs par rôle
     */
    public List<UserDTO> getUsersByRole(Role role) {
        List<User> users = userRepository.findByRolesContaining(role);
        return userMapper.toDTOList(users);
    }

    /**
     * Récupère les utilisateurs actifs
     */
    public List<UserDTO> getActiveUsers() {
        List<User> users = userRepository.findByEnabledTrue();
        return userMapper.toDTOList(users);
    }

    /**
     * Récupère les utilisateurs par région
     */
    public List<UserDTO> getUsersByRegion(String region) {
        List<User> users = userRepository.findByRegion(region);
        return userMapper.toDTOList(users);
    }

    /**
     * Met à jour un utilisateur
     */
    public UserDTO updateUser(String id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        // Mise à jour des champs
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getOrganizationName() != null) {
            user.setOrganizationName(userDTO.getOrganizationName());
        }
        if (userDTO.getAddress() != null) {
            user.setAddress(userDTO.getAddress());
        }
        if (userDTO.getCity() != null) {
            user.setCity(userDTO.getCity());
        }
        if (userDTO.getRegion() != null) {
            user.setRegion(userDTO.getRegion());
        }

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Utilisateur mis à jour: {}", updatedUser.getEmail());

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Active/désactive un utilisateur
     */
    public UserDTO toggleUserStatus(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        user.setEnabled(!user.isEnabled());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Statut utilisateur modifié: {} - Actif: {}", updatedUser.getEmail(), updatedUser.isEnabled());

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Ajoute un rôle à un utilisateur
     */
    public UserDTO addRoleToUser(String id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        user.addRole(role);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Rôle {} ajouté à l'utilisateur: {}", role, updatedUser.getEmail());

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Supprime un rôle d'un utilisateur
     */
    public UserDTO removeRoleFromUser(String id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        user.removeRole(role);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Rôle {} supprimé de l'utilisateur: {}", role, updatedUser.getEmail());

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Supprime un utilisateur
     */
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("Utilisateur supprimé avec l'ID: {}", id);
    }
}
