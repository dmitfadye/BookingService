package com.example.booking.service;

/**
 * Хеширование и проверка паролей.
 */
public interface PasswordHasher {

    /**
     * Хеширует пароль.
     *
     * @param rawPassword пароль в открытом виде
     * @return строка хеша с солью
     */
    String hash(String rawPassword);

    /**
     * Проверяет пароль по сохранённому хешу.
     *
     * @param rawPassword  пароль в открытом виде
     * @param passwordHash сохранённый хеш
     * @return {@code true}, если пароль совпадает
     */
    boolean matches(String rawPassword, String passwordHash);
}
