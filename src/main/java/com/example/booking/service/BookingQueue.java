package com.example.booking.service;

import com.example.booking.model.Booking;

import java.util.List;

/**
 * Очередь бронирований, ожидающих подтверждения.
 */
public interface BookingQueue {

    /**
     * Добавляет бронирование в очередь.
     *
     * @param booking бронирование
     */
    void add(Booking booking);

    /**
     * Возвращает снимок очереди.
     *
     * @return список бронирований
     */
    List<Booking> snapshot();

    /**
     * Удаляет бронирование из очереди по идентификатору.
     *
     * @param bookingId идентификатор
     * @return {@code true}, если бронирование было в очереди
     */
    boolean remove(Long bookingId);

    /**
     * Возвращает размер очереди.
     *
     * @return количество элементов
     */
    int size();

    /**
     * Очищает очередь.
     */
    void clear();
}
