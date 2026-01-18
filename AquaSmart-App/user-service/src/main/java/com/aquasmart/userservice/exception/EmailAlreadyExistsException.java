package com.aquasmart.userservice.exception;

/**
 * Exception levée lorsqu'un email existe déjà
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
