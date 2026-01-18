package com.aquasmart.userservice.dto.response;

import com.aquasmart.userservice.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO pour la réponse d'authentification avec JWT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
    private Long expiresIn;

    public AuthResponse(String token, String userId, String email, String firstName, 
                       String lastName, Set<Role> roles, Long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.expiresIn = expiresIn;
        this.type = "Bearer";
    }
}
