package com.example.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/**
 * Переговорная комната с проектором и, возможно, системой видеосвязи.
 */
@Entity
@DiscriminatorValue("MEETING_ROOM")
public class MeetingRoom extends Workspace {

    @Column
    private boolean projector;

    @Column
    private boolean videoConference;

    /**
     * Конструктор для JPA. Не использовать в прикладном коде.
     */
    protected MeetingRoom() {
    }

    /**
     * Создаёт переговорную.
     *
     * @param id              идентификатор (для JPA допускается null)
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
