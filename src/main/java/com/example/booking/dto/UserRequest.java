package com.example.booking.dto;

import com.example.booking.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на создание или обновление пользователя.
 *
 * @param fullName   ФИО
 * @param email      e-mail
 * @param password   пароль в открытом виде (хешируется на сервере)
 * @param role       роль пользователя
 * @param position   должность (для ADMIN)
 * @param department отдел (для MANAGER)
 * @param phone      телефон (для CUSTOMER)
 * @param company    компания (для CUSTOMER)
 */
public record UserRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull UserRole role,
        String position,
        String department,
        String phone,
        String company
) {
}
