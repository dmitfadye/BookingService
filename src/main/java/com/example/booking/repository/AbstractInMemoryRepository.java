package com.example.booking.repository;

import com.example.booking.model.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Базовое потокобезопасное in-memory хранилище на основе {@link Map}.
 *
 * <p>Класс отвечает только за хранение сущностей и генерацию идентификаторов.
 * Специфичные для предметной области запросы реализуются в наследниках.</p>
 *
 * @param <T> тип сущности
 */
public abstract class AbstractInMemoryRepository<T extends Identifiable<Long>>
        implements CrudRepository<T, Long> {

    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Сущность не может быть null");
        Objects.requireNonNull(entity.getId(), "Идентификатор сущности не может быть null");
        beforeSave(entity);
        storage.put(entity.getId(), entity);
        idGenerator.accumulateAndGet(entity.getId(), Math::max);
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>(storage.values());
        result.sort((first, second) -> first.getId().compareTo(second.getId()));
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        T removed = storage.remove(id);
        if (removed == null) {
            return false;
        }
        afterDelete(removed);
        return true;
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    /**
     * Генерирует следующий свободный идентификатор.
     *
     * @return новый идентификатор
     */
    public Long nextId() {
        return idGenerator.incrementAndGet();
    }

    /**
     * Хук, вызываемый перед сохранением.
     *
     * @param entity сохраняемая сущность
     */
    protected void beforeSave(T entity) {
    }

    /**
     * Хук, вызываемый после удаления.
     *
     * @param entity удалённая сущность
     */
    protected void afterDelete(T entity) {
    }

    /**
     * Возвращает снимок всех значений для специфичных запросов.
     *
     * @return список значений
     */
    protected List<T> snapshot() {
        return new ArrayList<>(storage.values());
    }
}
