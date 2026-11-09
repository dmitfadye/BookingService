package com.example.booking.service;

import com.example.booking.model.Booking;
import com.example.booking.model.BookingSearchFilter;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.WorkspaceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Сервис управления бронированиями.
 */
public interface BookingService {

    /**
     * Создаёт бронирование.
     *
     * @param customerId  идентификатор пользователя
     * @param workspaceId идентификатор пространства
     * @param start       начало
     * @param end         окончание
     * @return созданное бронирование
     */
    Booking createBooking(Long customerId, Long workspaceId,
                          LocalDateTime start, LocalDateTime end);

    /**
     * Возвращает бронирование по идентификатору.
     *
     * @param id идентификатор
     * @return бронирование
     */
    Booking getById(Long id);

    /**
     * Возвращает все бронирования.
     *
     * @return список бронирований
     */
    List<Booking> findAll();

    /**
     * Ищет бронирования по e-mail пользователя.
     *
     * @param email e-mail
     * @return список бронирований
     */
    List<Booking> findByCustomerEmail(String email);

    /**
     * Ищет бронирования по статусу.
     *
     * @param status статус
     * @return список бронирований
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Ищет бронирования по типу пространства.
     *
     * @param type тип пространства
     * @return список бронирований
     */
    List<Booking> findByWorkspaceType(WorkspaceType type);

    /**
     * Ищет бронирования по комбинированному фильтру.
     *
     * @param filter фильтр
     * @return список бронирований
     */
    List<Booking> search(BookingSearchFilter filter);

    /**
     * Возвращает очередь бронирований, ожидающих подтверждения.
     *
     * @return список ожидающих бронирований
     */
    List<Booking> getPendingBookings();

    /**
     * Подтверждает бронирование.
     *
     * @param id идентификатор
     */
    void confirmBooking(Long id);

    /**
     * Отменяет бронирование.
     *
     * @param id идентификатор
     */
    void cancelBooking(Long id);

    /**
     * Завершает бронирование.
     *
     * @param id идентификатор
     */
    void completeBooking(Long id);

    /**
     * Удаляет бронирование.
     *
     * @param id идентификатор
     * @return {@code true}, если бронирование удалено
     */
    boolean deleteBooking(Long id);

    /**
     * Считает количество бронирований по статусам.
     *
     * @return карта «статус — количество»
     */
    Map<BookingStatus, Long> countByStatus();

    /**
     * Считает выручку по подтверждённым и завершённым бронированиям.
     *
     * @return сумма
     */
    BigDecimal calculateRevenue();
}
