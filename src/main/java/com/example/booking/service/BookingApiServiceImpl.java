package com.example.booking.service;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.model.Booking;
import com.example.booking.model.BookingSearchFilter;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;
import com.example.booking.repository.BookingJpaRepository;
import com.example.booking.repository.UserJpaRepository;
import com.example.booking.repository.WorkspaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса бронирований на Spring Data JPA.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingApiServiceImpl implements BookingApiService {

    private final BookingJpaRepository bookingJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final WorkspaceJpaRepository workspaceJpaRepository;

    @Override
    public List<BookingResponse> findAll(BookingStatus status, String customerEmail,
                                         WorkspaceType workspaceType, LocalDate date,
                                         BigDecimal maxPrice) {
        List<Booking> candidates;
        if (status != null) {
            candidates = bookingJpaRepository.findByStatus(status);
        } else {
            candidates = bookingJpaRepository.findAll();
        }
        BookingSearchFilter filter = new BookingSearchFilter(
                customerEmail, null, status, workspaceType, date, maxPrice);
        return candidates.stream()
                .filter(filter::matches)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BookingResponse findById(Long id) {
        return toResponse(getBooking(id));
    }

    @Override
    @Transactional
    public BookingResponse create(BookingRequest request) {
        User customer = getUser(request.customerId());
        if (!customer.isActive()) {
            throw new IllegalStateException("Пользователь #" + customer.getId() + " деактивирован");
        }
        if (!customer.canCreateBookings()) {
            throw new IllegalStateException("Пользователь #" + customer.getId()
                    + " не имеет права создавать бронирования");
        }
        Workspace workspace = getWorkspace(request.workspaceId());
        if (!workspace.isActive()) {
            throw new IllegalStateException("Пространство #" + workspace.getId() + " недоступно");
        }
        validatePeriod(request.start(), request.end());
        if (hasOverlap(workspace.getId(), request.start(), request.end(), null)) {
            throw new IllegalStateException("Пространство уже занято в выбранный интервал");
        }
        Booking booking = new Booking(null, customer, workspace,
                request.start(), request.end(), LocalDateTime.now());
        return toResponse(bookingJpaRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse update(Long id, BookingRequest request) {
        Booking booking = getBooking(id);
        if (!booking.getCustomer().getId().equals(request.customerId())) {
            throw new IllegalArgumentException("Нельзя изменить клиента бронирования");
        }
        if (!booking.getWorkspace().getId().equals(request.workspaceId())) {
            throw new IllegalArgumentException("Нельзя изменить пространство бронирования");
        }
        validatePeriod(request.start(), request.end());
        if (hasOverlap(booking.getWorkspace().getId(), request.start(), request.end(), id)) {
            throw new IllegalStateException("Пространство уже занято в выбранный интервал");
        }
        booking.reschedule(request.start(), request.end());
        return toResponse(bookingJpaRepository.save(booking));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bookingJpaRepository.delete(getBooking(id));
    }

    @Override
    @Transactional
    public BookingResponse confirm(Long id) {
        Booking booking = getBooking(id);
        booking.confirm();
        return toResponse(bookingJpaRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse cancel(Long id) {
        Booking booking = getBooking(id);
        booking.cancel();
        return toResponse(bookingJpaRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse complete(Long id) {
        Booking booking = getBooking(id);
        booking.complete();
        return toResponse(bookingJpaRepository.save(booking));
    }

    private Booking getBooking(Long id) {
        return bookingJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено: " + id));
    }

    private User getUser(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + id));
    }

    private Workspace getWorkspace(Long id) {
        return workspaceJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пространство не найдено: " + id));
    }

    private void validatePeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Начало и окончание бронирования обязательны");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Нельзя бронировать пространство в прошлом");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Окончание бронирования должно быть позже начала");
        }
    }

    private boolean hasOverlap(Long workspaceId, LocalDateTime start, LocalDateTime end,
                               Long excludedBookingId) {
        List<Booking> candidates = bookingJpaRepository.findByWorkspaceId(workspaceId);
        for (Booking candidate : candidates) {
            if (excludedBookingId != null && candidate.getId().equals(excludedBookingId)) {
                continue;
            }
            if (!candidate.isActive()) {
                continue;
            }
            if (start.isBefore(candidate.getEnd()) && end.isAfter(candidate.getStart())) {
                return true;
            }
        }
        return false;
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getCustomer().getEmail(),
                booking.getWorkspace().getId(),
                booking.getWorkspace().getName(),
                booking.getWorkspace().getType(),
                booking.getStart(),
                booking.getEnd(),
                booking.getCreatedAt(),
                booking.getTotalPrice(),
                booking.getStatus()
        );
    }
}
