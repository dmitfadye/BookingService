package com.example.booking.model;

/**
 * Статусы жизненного цикла бронирования.
 */
public enum BookingStatus {

    PENDING("Ожидает подтверждения"),
    CONFIRMED("Подтверждено"),
    CANCELLED("Отменено"),
    COMPLETED("Завершено");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает название статуса для отображения.
     *
     * @return название статуса на русском языке
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Проверяет, занимает ли бронирование время в расписании.
     *
     * @return {@code true} для статусов PENDING и CONFIRMED
     */
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED;
    }
}
