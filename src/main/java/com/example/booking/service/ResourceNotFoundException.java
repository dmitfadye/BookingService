package com.example.booking.service;

/**
 * Исключение «ресурс не найден» для REST-слоя.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
