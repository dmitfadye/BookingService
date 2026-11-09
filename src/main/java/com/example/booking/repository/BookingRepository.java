package com.example.booking.repository;

import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Хранилище бронирований.
 */
public interface BookingRepository extends CrudRepository<Booking, Long> {

    /**
     * Ищет бронирования клиента.
     *
     * @param customerId идентификатор клиента
     * @return список бронирований
     */
    List<Booking> findByCustomerId(Long customerId);

    /**
     * Ищет бронирования по статусу.
     *
     * @param status статус
     * @return список бронирований
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Ищет бронирования пространства.
     *
     * @param workspaceId идентификатор пространства
     * @return список бронирований
     */
    List<Booking> findByWorkspaceId(Long workspaceId);

    /**
     * Проверяет пересечение с существующими активными бронированиями.
     *
     * @param workspaceId      идентификатор пространства
     * @param start            начало
     * @param end              окончание
     * @param excludedBookingId идентификатор бронирования, которое нужно исключить
     * @return {@code true}, если есть пересечение
     */
    boolean hasOverlap(Long workspaceId, LocalDateTime start, LocalDateTime end,
                       Long excludedBookingId);

    /**
     * Генерирует следующий идентификатор.
     *
     * @return идентификатор
     */
    Long nextId();
}
