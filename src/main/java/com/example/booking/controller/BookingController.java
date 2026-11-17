package com.example.booking.controller;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.WorkspaceType;
import com.example.booking.service.BookingApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST-контроллер бронирований.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingApiService bookingApiService;

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
    @GetMapping
    public List<BookingResponse> findAll(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) WorkspaceType workspaceType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return bookingApiService.findAll(status, customerEmail, workspaceType, date, maxPrice);
    }

    /**
     * Возвращает бронирование по идентификатору.
     *
     * @param id идентификатор
     * @return бронирование
     */
    @GetMapping("/{id}")
    public BookingResponse findById(@PathVariable Long id) {
        return bookingApiService.findById(id);
    }

    /**
     * Создаёт бронирование.
     *
     * @param request запрос
     * @return созданное бронирование
     */
    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingApiService.create(request));
    }

    /**
     * Обновляет период бронирования.
     *
     * @param id      идентификатор
     * @param request запрос
     * @return обновлённое бронирование
     */
    @PutMapping("/{id}")
    public BookingResponse update(@PathVariable Long id,
                                  @Valid @RequestBody BookingRequest request) {
        return bookingApiService.update(id, request);
    }

    /**
     * Удаляет бронирование.
     *
     * @param id идентификатор
     * @return пустой ответ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingApiService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Подтверждает бронирование.
     *
     * @param id идентификатор
     * @return обновлённое бронирование
     */
    @PostMapping("/{id}/confirm")
    public BookingResponse confirm(@PathVariable Long id) {
        return bookingApiService.confirm(id);
    }

    /**
     * Отменяет бронирование.
     *
     * @param id идентификатор
     * @return обновлённое бронирование
     */
    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        return bookingApiService.cancel(id);
    }

    /**
     * Завершает бронирование.
     *
     * @param id идентификатор
     * @return обновлённое бронирование
     */
    @PostMapping("/{id}/complete")
    public BookingResponse complete(@PathVariable Long id) {
        return bookingApiService.complete(id);
    }
}
