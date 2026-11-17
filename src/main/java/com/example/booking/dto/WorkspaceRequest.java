package com.example.booking.dto;

import com.example.booking.model.WorkspaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Запрос на создание или обновление рабочего пространства.
 *
 * @param name            название
 * @param location        расположение
 * @param capacity        вместимость
 * @param pricePerHour    цена за час
 * @param type            тип пространства
 * @param projector       наличие проектора (для переговорной)
 * @param videoConference наличие видеосвязи (для переговорной)
 * @param monitorIncluded наличие монитора (для коворкинг-места)
 * @param nearWindow      расположение у окна (для коворкинг-места)
 */
public record WorkspaceRequest(
        @NotBlank String name,
        @NotBlank String location,
        @NotNull @Positive Integer capacity,
        @NotNull @Positive BigDecimal pricePerHour,
        @NotNull WorkspaceType type,
        Boolean projector,
        Boolean videoConference,
        Boolean monitorIncluded,
        Boolean nearWindow
) {
}
