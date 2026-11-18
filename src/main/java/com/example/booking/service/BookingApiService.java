package com.example.booking.service;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.WorkspaceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Сервис бронирований на Spring Data JPA (лабораторная работа №2).
 */
public interface BookingApiService {

    /**
     * Возвращает бронирования с фильтрами.
     *
     * @param status        статус или null
     * @param customerEmail e-mail клиента или null
     * @param workspaceType тип пространства или null
     * @param date          дата начала или null
     * @param maxPrice      максимальная стоимость или null
     * @return список бронирований
     */
    List<BookingResponse> findAll(BookingStatus status, String customerEmail,
                                  WorkspaceType workspaceType, LocalDate date,
                                  BigDecimal maxPrice);

    /**
     * Возвращает бронирование по идентификатору.
     *
     * @param id идентификатор
     * @return бронирование
     */
    BookingResponse findById(Long id);

    /**
     * Создаёт бронирование.
     *
     * @param request запрос
     * @return созданное бронирование
     */
    BookingResponse create(BookingRequest request);

    /**
     * Обновляет период бронирования.
     *
     * @param id      идентификатор
     * @param request запрос
     * @return обновлённое бронирование
     */
    BookingResponse update(Long id, BookingRequest request);

    /**
     * Удаляет бронирование.
     *
     * @param id идентификатор
     */
    void delete(Long id);

    /**
     * Подтверждает бронирование.
     *
     * @param id идентификатор
     * @return обновлённое бронирование
     */
    BookingResponse confirm(Long id);

    /**
     * Отменяет бронирование.
     *
     * @param id идентификатор
     * @return обновлённое бронирование
     */
    BookingResponse cancel(Long id);

    /**
     * Завершает бронирование.
     *
     * @param id идентификатор
     * @return обновлённое бронирование
     */
    BookingResponse complete(Long id);
}
