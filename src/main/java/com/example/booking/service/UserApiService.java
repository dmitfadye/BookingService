package com.example.booking.service;

import com.example.booking.dto.UserRequest;
import com.example.booking.dto.UserResponse;
import com.example.booking.model.UserRole;

import java.util.List;

/**
 * Сервис каталога пользователей на Spring Data JPA (лабораторная работа №2).
 */
public interface UserApiService {

    /**
     * Возвращает пользователей с фильтрами.
     *
     * @param role   роль или null
     * @param active признак активности или null
     * @return список пользователей
     */
    List<UserResponse> findAll(UserRole role, Boolean active);

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор
     * @return пользователь
     */
    UserResponse findById(Long id);

    /**
     * Создаёт пользователя.
     *
     * @param request запрос
     * @return созданный пользователь
     */
    UserResponse create(UserRequest request);

    /**
     * Обновляет пользователя.
     *
     * @param id      идентификатор
     * @param request запрос
     * @return обновлённый пользователь
     */
    UserResponse update(Long id, UserRequest request);

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор
     */
    void delete(Long id);
}
