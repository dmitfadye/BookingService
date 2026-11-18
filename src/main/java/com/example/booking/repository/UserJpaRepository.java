package com.example.booking.repository;

import com.example.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA-репозиторий пользователей (лабораторная работа №2).
 *
 * <p>Иерархия SINGLE_TABLE позволяет хранить администраторов, менеджеров
 * и клиентов в одной таблице {@code app_users} и работать с ними через
 * базовый тип {@link User}.</p>
 */
public interface UserJpaRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по e-mail без учёта регистра.
     *
     * @param email e-mail
     * @return пользователь или пустой Optional
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Возвращает пользователей по признаку активности.
     *
     * @param active признак активности
     * @return список пользователей
     */
    List<User> findByActive(boolean active);
}
