package com.example.booking.repository;

import com.example.booking.model.User;
import com.example.booking.model.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Хранилище пользователей.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Ищет пользователя по e-mail.
     *
     * @param email e-mail
     * @return пользователь или пустой Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Ищет пользователей по роли.
     *
     * @param role роль
     * @return список пользователей
     */
    List<User> findByRole(UserRole role);

    /**
     * Возвращает активных пользователей.
     *
     * @return список активных пользователей
     */
    List<User> findActiveUsers();

    /**
     * Генерирует следующий идентификатор.
     *
     * @return идентификатор
     */
    Long nextId();
}
