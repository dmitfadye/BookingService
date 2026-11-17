package com.example.booking.controller;

import com.example.booking.service.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Преобразует исключения сервисного слоя в HTTP-ответы.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Обрабатывает отсутствие ресурса.
     *
     * @param exception исключение
     * @return ответ 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Обрабатывает конфликты данных.
     *
     * @param exception исключение
     * @return ответ 409
     */
    @ExceptionHandler({IllegalStateException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * Обрабатывает ошибки валидации тела запроса.
     *
     * @param exception исключение
     * @return ответ 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Обрабатывает нарушения ограничений валидации.
     *
     * @param exception исключение
     * @return ответ 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * Обрабатывает некорректные аргументы.
     *
     * @param exception исключение
     * @return ответ 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), message));
    }
}
