package com.example.booking.controller;

import java.time.Instant;

/**
 * Тело ответа об ошибке REST API.
 *
 * @param timestamp время ошибки
 * @param message   описание ошибки
 */
public record ApiError(Instant timestamp, String message) {
}
