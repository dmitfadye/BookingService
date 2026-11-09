package com.example.booking.service;

import com.example.booking.model.CoworkingDesk;
import com.example.booking.model.MeetingRoom;
import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис управления рабочими пространствами.
 */
public interface WorkspaceService {

    /**
     * Добавляет переговорную.
     *
     * @param name            название
     * @param location        расположение
     * @param capacity        вместимость
     * @param pricePerHour    цена за час
     * @param projector       наличие проектора
     * @param videoConference наличие видеосвязи
     * @return созданная переговорная
     */
    MeetingRoom addMeetingRoom(String name, String location, int capacity,
                               BigDecimal pricePerHour, boolean projector,
                               boolean videoConference);

    /**
     * Добавляет коворкинг-место.
     *
     * @param name            название
     * @param location        расположение
     * @param pricePerHour    цена за час
     * @param monitorIncluded наличие монитора
     * @param nearWindow      расположение у окна
     * @return созданное коворкинг-место
     */
    CoworkingDesk addCoworkingDesk(String name, String location, BigDecimal pricePerHour,
                                   boolean monitorIncluded, boolean nearWindow);

    /**
     * Возвращает пространство по идентификатору.
     *
     * @param id идентификатор
     * @return пространство
     */
    Workspace getById(Long id);

    /**
     * Возвращает все пространства.
     *
     * @return список пространств
     */
    List<Workspace> findAll();

    /**
     * Ищет пространства по типу.
     *
     * @param type тип
     * @return список пространств
     */
    List<Workspace> findByType(WorkspaceType type);

    /**
     * Изменяет цену пространства.
     *
     * @param id       идентификатор
     * @param newPrice новая цена
     */
    void changePrice(Long id, BigDecimal newPrice);

    /**
     * Деактивирует пространство.
     *
     * @param id идентификатор
     */
    void deactivate(Long id);

    /**
     * Удаляет пространство.
     *
     * @param id идентификатор
     * @return {@code true}, если пространство удалено
     */
    boolean delete(Long id);
}
