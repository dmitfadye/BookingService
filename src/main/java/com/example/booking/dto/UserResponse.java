package com.example.booking.dto;

import com.example.booking.model.UserRole;

import java.time.LocalDateTime;

/**
 * Ответ с данными пользователя (без хеша пароля).
 *
 * @param id         идентификатор
 * @param fullName   ФИО
 * @param email      e-mail
 * @param role       роль
 * @param active     признак активности
 * @param createdAt  дата регистрации
 * @param position   должность (для ADMIN)
 * @param department отдел (для MANAGER)
 * @param phone      телефон (для CUSTOMER)
 * @param company    компания (для CUSTOMER)
 */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        boolean active,
        LocalDateTime createdAt,
        String position,
        String department,
        String phone,
        String company
) {
}
