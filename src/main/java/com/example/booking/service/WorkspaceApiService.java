package com.example.booking.service;

import com.example.booking.dto.WorkspaceRequest;
import com.example.booking.dto.WorkspaceResponse;
import com.example.booking.model.WorkspaceType;

import java.util.List;

/**
 * Сервис каталога рабочих пространств на Spring Data JPA (лабораторная работа №2).
 */
public interface WorkspaceApiService {

    /**
     * Возвращает пространства с фильтрами.
     *
     * @param type   тип или null
     * @param active признак доступности или null
     * @return список пространств
     */
    List<WorkspaceResponse> findAll(WorkspaceType type, Boolean active);

    /**
     * Возвращает пространство по идентификатору.
     *
     * @param id идентификатор
     * @return пространство
     */
    WorkspaceResponse findById(Long id);

    /**
     * Создаёт пространство.
     *
     * @param request запрос
     * @return созданное пространство
     */
    WorkspaceResponse create(WorkspaceRequest request);

    /**
     * Обновляет пространство.
     *
     * @param id      идентификатор
     * @param request запрос
     * @return обновлённое пространство
     */
    WorkspaceResponse update(Long id, WorkspaceRequest request);

    /**
     * Удаляет пространство.
     *
     * @param id идентификатор
     */
    void delete(Long id);
}
