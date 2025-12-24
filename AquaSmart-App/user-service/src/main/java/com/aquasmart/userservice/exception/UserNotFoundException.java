package com.aquasmart.userservice.exception;

/**
 * Exception levée lorsqu'un utilisateur n'est pas trouvé
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
