package com.aquasmart.userservice.dto.response;

import com.aquasmart.userservice.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO pour les informations utilisateur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<Role> roles;
    private boolean enabled;
    private String organizationName;
    private String address;
    private String city;
    private String region;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
