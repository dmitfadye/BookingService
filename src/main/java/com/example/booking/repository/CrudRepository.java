package com.example.booking.repository;

import com.example.booking.model.Identifiable;

import java.util.List;
import java.util.Optional;

/**
 * Общий контракт CRUD-хранилища.
 *
 * @param <T>  тип сущности
 * @param <ID> тип идентификатора
 */
public interface CrudRepository<T extends Identifiable<ID>, ID> {

    /**
     * Сохраняет сущность (создаёт или обновляет).
     *
     * @param entity сущность
     * @return сохранённая сущность
     */
    T save(T entity);

    /**
     * Ищет сущность по идентификатору.
     *
     * @param id идентификатор
     * @return сущность или пустой Optional
     */
    Optional<T> findById(ID id);

    /**
     * Возвращает все сущности.
     *
     * @return список сущностей
     */
    List<T> findAll();

    /**
     * Удаляет сущность по идентификатору.
     *
     * @param id идентификатор
     * @return {@code true}, если сущность была удалена
     */
    boolean deleteById(ID id);

    /**
     * Проверяет наличие сущности.
     *
     * @param id идентификатор
     * @return {@code true}, если сущность существует
     */
    boolean existsById(ID id);

    /**
     * Возвращает количество сущностей.
     *
     * @return количество
     */
    long count();
}
