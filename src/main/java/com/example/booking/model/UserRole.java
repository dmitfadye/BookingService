package com.example.booking.model;

/**
 * Роли пользователей сервиса бронирования.
 */
public enum UserRole {

    ADMIN("Администратор"),
    MANAGER("Менеджер"),
    CUSTOMER("Клиент");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает название роли для отображения.
     *
     * @return название роли на русском языке
     */
    public String getDisplayName() {
        return displayName;
    }
}
