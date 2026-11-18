package com.example.booking.dto;

import com.example.booking.model.BookingStatus;
import com.example.booking.model.WorkspaceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ответ с данными бронирования.
 *
 * @param id            идентификатор
 * @param customerId    идентификатор клиента
 * @param customerEmail e-mail клиента
 * @param workspaceId   идентификатор пространства
 * @param workspaceName название пространства
 * @param workspaceType тип пространства
 * @param start         начало
 * @param end           окончание
 * @param createdAt     дата создания
 * @param totalPrice    стоимость
 * @param status        статус
 */
public record BookingResponse(
        Long id,
        Long customerId,
        String customerEmail,
        Long workspaceId,
        String workspaceName,
        WorkspaceType workspaceType,
        LocalDateTime start,
        LocalDateTime end,
        LocalDateTime createdAt,
        BigDecimal totalPrice,
        BookingStatus status
) {
}
