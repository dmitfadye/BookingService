package com.example.booking.model;

import java.math.BigDecimal;

/**
 * Переговорная комната с проектором и, возможно, системой видеосвязи.
 */
public class MeetingRoom extends Workspace {

    private final boolean projector;
    private final boolean videoConference;

    /**
     * Создаёт переговорную.
     *
     * @param id              идентификатор
     * @param name            название
     * @param location        расположение
     * @param capacity        вместимость
     * @param pricePerHour    цена за час
     * @param active          признак доступности
     * @param projector       наличие проектора
     * @param videoConference наличие видеосвязи
     */
    public MeetingRoom(Long id, String name, String location, int capacity,
                       BigDecimal pricePerHour, boolean active, boolean projector,
                       boolean videoConference) {
        super(id, name, location, capacity, pricePerHour, active);
        this.projector = projector;
        this.videoConference = videoConference;
    }

    public boolean isProjector() {
        return projector;
    }

    public boolean isVideoConference() {
        return videoConference;
    }

    @Override
    public WorkspaceType getType() {
        return WorkspaceType.MEETING_ROOM;
    }

    @Override
    public String getEquipmentDescription() {
        StringBuilder description = new StringBuilder("проектор");
        if (projector) {
            description.append(": да");
        } else {
            description.append(": нет");
        }
        description.append(", видеосвязь: ");
        description.append(videoConference ? "да" : "нет");
        return description.toString();
    }
}
