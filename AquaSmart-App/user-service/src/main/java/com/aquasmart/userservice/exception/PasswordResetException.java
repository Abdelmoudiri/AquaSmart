package com.aquasmart.userservice.exception;

/**
 * Exception pour les erreurs de réinitialisation de mot de passe
 */
public class PasswordResetException extends RuntimeException {

    public PasswordResetException(String message) {
        super(message);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
