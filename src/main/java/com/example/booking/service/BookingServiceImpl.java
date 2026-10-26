package com.example.booking.service;

import com.example.booking.model.Booking;
import com.example.booking.model.BookingSearchFilter;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.UserRepository;
import com.example.booking.repository.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Реализация сервиса управления бронированиями.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BookingQueue pendingQueue;

    /**
     * Создаёт сервис.
     *
     * @param bookingRepository   хранилище бронирований
     * @param userRepository      хранилище пользователей
     * @param workspaceRepository хранилище пространств
     * @param pendingQueue        очередь ожидающих подтверждения
     */
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              WorkspaceRepository workspaceRepository,
                              BookingQueue pendingQueue) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.pendingQueue = pendingQueue;
    }

    @Override
    public Booking createBooking(Long customerId, Long workspaceId,
                                 LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(customerId, "Идентификатор пользователя не может быть null");
        Objects.requireNonNull(workspaceId, "Идентификатор пространства не может быть null");
        Objects.requireNonNull(start, "Начало бронирования не может быть null");
        Objects.requireNonNull(end, "Окончание бронирования не может быть null");

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с id " + customerId + " не найден"));
        if (!customer.isActive()) {
            throw new IllegalStateException("Пользователь #" + customerId + " деактивирован");
        }
        if (!customer.canCreateBookings()) {
            throw new IllegalStateException("Пользователь #" + customerId
                    + " не имеет права создавать бронирования");
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пространство с id " + workspaceId + " не найдено"));
        if (!workspace.isActive()) {
            throw new IllegalStateException("Пространство #" + workspaceId + " недоступно");
        }
        LocalDateTime now = LocalDateTime.now();
        if (start.isBefore(now)) {
            throw new IllegalArgumentException("Нельзя бронировать пространство в прошлом");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Окончание бронирования должно быть позже начала");
        }
        if (bookingRepository.hasOverlap(workspaceId, start, end, null)) {
            throw new IllegalStateException("Пространство уже занято в выбранный интервал");
        }

        Long id = bookingRepository.nextId();
        Booking booking = new Booking(id, customer, workspace, start, end, now);
        bookingRepository.save(booking);
        pendingQueue.add(booking);
        LOGGER.info("Создано бронирование #{}: {} — {} ({}, {} — {})",
                booking.getId(), customer.getFullName(), workspace.getName(),
                booking.getTotalPrice(), start, end);
        return booking;
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Бронирование с id " + id + " не найдено"));
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> findByCustomerEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> bookingRepository.findByCustomerId(user.getId()))
                .orElseGet(Collections::emptyList);
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    public List<Booking> findByWorkspaceType(WorkspaceType type) {
        Objects.requireNonNull(type, "Тип пространства не может быть null");
        List<Booking> result = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if (booking.getWorkspace().getType() == type) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public List<Booking> search(BookingSearchFilter filter) {
        Objects.requireNonNull(filter, "Фильтр не может быть null");
        List<Booking> result = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if (filter.matches(booking)) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public List<Booking> getPendingBookings() {
        List<Booking> result = new ArrayList<>();
        List<Booking> bookings = pendingQueue.snapshot();
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.PENDING) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public void confirmBooking(Long id) {
        Booking booking = getById(id);
        booking.confirm();
        bookingRepository.save(booking);
        pendingQueue.remove(id);
        LOGGER.info("Бронирование #{} подтверждено", id);
    }

    @Override
    public void cancelBooking(Long id) {
        Booking booking = getById(id);
        booking.cancel();
        bookingRepository.save(booking);
        pendingQueue.remove(id);
        LOGGER.info("Бронирование #{} отменено", id);
    }

    @Override
    public void completeBooking(Long id) {
        Booking booking = getById(id);
        booking.complete();
        bookingRepository.save(booking);
        pendingQueue.remove(id);
        LOGGER.info("Бронирование #{} завершено", id);
    }

    @Override
    public boolean deleteBooking(Long id) {
        boolean deleted = bookingRepository.deleteById(id);
        pendingQueue.remove(id);
        if (deleted) {
            LOGGER.info("Бронирование #{} удалено", id);
        }
        return deleted;
    }

    @Override
    public Map<BookingStatus, Long> countByStatus() {
        Map<BookingStatus, Long> counts = new EnumMap<>(BookingStatus.class);
        for (BookingStatus status : BookingStatus.values()) {
            counts.put(status, 0L);
        }
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            Long current = counts.get(booking.getStatus());
            counts.put(booking.getStatus(), current + 1L);
        }
        return Collections.unmodifiableMap(counts);
    }

    @Override
    public BigDecimal calculateRevenue() {
        BigDecimal total = BigDecimal.ZERO;
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.CONFIRMED
                    || booking.getStatus() == BookingStatus.COMPLETED) {
                total = total.add(booking.getTotalPrice());
            }
        }
        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
