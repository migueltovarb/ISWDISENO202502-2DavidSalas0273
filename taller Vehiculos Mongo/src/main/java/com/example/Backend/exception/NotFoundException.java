package com.example.Backend.exception;

/**
 * Excepción para recursos no encontrados.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
