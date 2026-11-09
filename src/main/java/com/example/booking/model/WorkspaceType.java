package com.example.booking.model;

/**
 * Типы рабочих пространств, которые можно забронировать.
 */
public enum WorkspaceType {

    MEETING_ROOM("Переговорная"),
    COWORKING_DESK("Коворкинг-место");

    private final String displayName;

    WorkspaceType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает название типа для отображения.
     *
     * @return название типа на русском языке
     */
    public String getDisplayName() {
        return displayName;
    }
}
