package com.example.booking.util;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Утилиты нормализации и проверки строковых значений.
 *
 * <p>Класс отвечает только за преобразование строк: приводит e-mail к нижнему
 * регистру, убирает лишние пробелы в ФИО и проверяет формат данных.</p>
 */
public final class TextUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private TextUtils() {
    }

    /**
     * Убирает пробелы по краям, приводит e-mail к нижнему регистру и проверяет формат.
     *
     * @param email исходный e-mail
     * @return нормализованный e-mail
     */
    public static String normalizeEmail(String email) {
        String normalized = requireText(email, "email").toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Некорректный e-mail: " + email);
        }
        return normalized;
    }

    /**
     * Убирает лишние пробелы и делает первую букву каждого слова заглавной.
     *
     * @param fullName исходное ФИО
     * @return нормализованное ФИО
     */
    public static String normalizePersonName(String fullName) {
        String trimmed = requireText(fullName, "ФИО");
        String[] words = trimmed.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(capitalize(word));
        }
        return result.toString();
    }

    /**
     * Проверяет, что строка не пустая, и возвращает её без пробелов по краям.
     *
     * @param value     проверяемое значение
     * @param fieldName название поля для сообщения об ошибке
     * @return строка без пробелов по краям
     */
    public static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, "Поле «" + fieldName + "» не может быть null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Поле «" + fieldName + "» не может быть пустым");
        }
        return trimmed;
    }

    /**
     * Маскирует e-mail для вывода в консоль: {@code ivan@mail.ru -> i***@mail.ru}.
     *
     * @param email e-mail
     * @return замаскированная строка
     */
    public static String maskEmail(String email) {
        String normalized = normalizeEmail(email);
        int atIndex = normalized.indexOf('@');
        return normalized.charAt(0) + "***" + normalized.substring(atIndex);
    }

    private static String capitalize(String word) {
        if (word.isEmpty()) {
            return word;
        }
        String lowerCase = word.toLowerCase(Locale.ROOT);
        return lowerCase.substring(0, 1).toUpperCase(Locale.ROOT) + lowerCase.substring(1);
    }
}
