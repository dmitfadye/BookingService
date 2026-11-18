package com.example.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/**
 * Коворкинг-место с монитором и/или местом у окна.
 */
@Entity
@DiscriminatorValue("COWORKING_DESK")
public class CoworkingDesk extends Workspace {

    @Column
    private boolean monitorIncluded;

    @Column
    private boolean nearWindow;

    /**
     * Конструктор для JPA. Не использовать в прикладном коде.
     */
    protected CoworkingDesk() {
    }

    /**
     * Создаёт коворкинг-место.
     *
     * @param id              идентификатор (для JPA допускается null)
     * @param name            название
     * @param location        расположение
     * @param capacity        вместимость
     * @param pricePerHour    цена за час
     * @param active          признак доступности
     * @param monitorIncluded наличие монитора
     * @param nearWindow      расположение у окна
     */
    public CoworkingDesk(Long id, String name, String location, int capacity,
                         BigDecimal pricePerHour, boolean active, boolean monitorIncluded,
                         boolean nearWindow) {
        super(id, name, location, capacity, pricePerHour, active);
        this.monitorIncluded = monitorIncluded;
        this.nearWindow = nearWindow;
    }

    public boolean isMonitorIncluded() {
        return monitorIncluded;
    }

    public boolean isNearWindow() {
        return nearWindow;
    }

    @Override
    public WorkspaceType getType() {
        return WorkspaceType.COWORKING_DESK;
    }

    @Override
    public String getEquipmentDescription() {
        StringBuilder description = new StringBuilder("монитор: ");
        description.append(monitorIncluded ? "да" : "нет");
        description.append(", у окна: ");
        description.append(nearWindow ? "да" : "нет");
        return description.toString();
    }
}
