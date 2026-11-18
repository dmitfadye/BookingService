package com.example.booking.repository;

import com.example.booking.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA-репозиторий рабочих пространств (лабораторная работа №2).
 */
public interface WorkspaceJpaRepository extends JpaRepository<Workspace, Long> {

    /**
     * Возвращает пространства по признаку доступности.
     *
     * @param active признак доступности
     * @return список пространств
     */
    List<Workspace> findByActive(boolean active);
}
