package com.example.booking.dto;

import com.example.booking.model.WorkspaceType;

import java.math.BigDecimal;

/**
 * Ответ с данными рабочего пространства.
 *
 * @param id              идентификатор
 * @param name            название
 * @param location        расположение
 * @param capacity        вместимость
 * @param pricePerHour    цена за час
 * @param type            тип пространства
 * @param active          признак доступности
 * @param projector       наличие проектора
 * @param videoConference наличие видеосвязи
 * @param monitorIncluded наличие монитора
 * @param nearWindow      расположение у окна
 */
public record WorkspaceResponse(
        Long id,
        String name,
        String location,
        int capacity,
        BigDecimal pricePerHour,
        WorkspaceType type,
        boolean active,
        Boolean projector,
        Boolean videoConference,
        Boolean monitorIncluded,
        Boolean nearWindow
) {
}
