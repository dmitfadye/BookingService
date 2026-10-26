package com.example.booking.repository;

import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;

import java.util.List;

/**
 * Хранилище рабочих пространств.
 */
public interface WorkspaceRepository extends CrudRepository<Workspace, Long> {

    /**
     * Ищет пространства по типу.
     *
     * @param type тип пространства
     * @return список пространств
     */
    List<Workspace> findByType(WorkspaceType type);

    /**
     * Возвращает активные пространства.
     *
     * @return список активных пространств
     */
    List<Workspace> findActive();

    /**
     * Генерирует следующий идентификатор.
     *
     * @return идентификатор
     */
    Long nextId();
}
