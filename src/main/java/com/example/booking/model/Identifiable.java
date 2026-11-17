package com.example.booking.model;

/**
 * Базовый контракт сущности, у которой есть уникальный идентификатор.
 *
 * @param <ID> тип идентификатора
 */
public interface Identifiable<ID> {

    /**
     * Возвращает идентификатор сущности.
     *
     * @return идентификатор
     */
    ID getId();
}
