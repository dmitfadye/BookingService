package com.example.booking.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Запрос на создание или обновление бронирования.
 *
 * @param customerId  идентификатор клиента
 * @param workspaceId идентификатор пространства
 * @param start       начало бронирования
 * @param end         окончание бронирования
 */
public record BookingRequest(
        @NotNull Long customerId,
        @NotNull Long workspaceId,
        @NotNull LocalDateTime start,
        @NotNull LocalDateTime end
) {
}
