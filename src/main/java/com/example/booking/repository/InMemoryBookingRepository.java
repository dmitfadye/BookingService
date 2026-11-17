package com.example.booking.repository;

import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * In-memory реализация хранилища бронирований.
 *
 * <p>Поиск пересечений выполняется по снимку коллекции и учитывает только
 * активные статусы PENDING и CONFIRMED.</p>
 */
@Repository
public class InMemoryBookingRepository extends AbstractInMemoryRepository<Booking>
        implements BookingRepository {

    @Override
    public List<Booking> findAll() {
        List<Booking> result = snapshot();
        result.sort((first, second) -> {
            int byStart = first.getStart().compareTo(second.getStart());
            if (byStart != 0) {
                return byStart;
            }
            return first.getId().compareTo(second.getId());
        });
        return result;
    }

    @Override
    public List<Booking> findByCustomerId(Long customerId) {
        Objects.requireNonNull(customerId, "Идентификатор клиента не может быть null");
        List<Booking> result = new ArrayList<>();
        List<Booking> bookings = findAll();
        for (Booking booking : bookings) {
            if (booking.getCustomer().getId().equals(customerId)) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        Objects.requireNonNull(status, "Статус не может быть null");
        List<Booking> result = new ArrayList<>();
        List<Booking> bookings = findAll();
        for (Booking booking : bookings) {
            if (booking.getStatus() == status) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public List<Booking> findByWorkspaceId(Long workspaceId) {
        Objects.requireNonNull(workspaceId, "Идентификатор пространства не может быть null");
        List<Booking> result = new ArrayList<>();
        List<Booking> bookings = findAll();
        for (Booking booking : bookings) {
            if (booking.getWorkspace().getId().equals(workspaceId)) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public boolean hasOverlap(Long workspaceId, LocalDateTime start, LocalDateTime end,
                              Long excludedBookingId) {
        Objects.requireNonNull(workspaceId, "Идентификатор пространства не может быть null");
        Objects.requireNonNull(start, "Начало не может быть null");
        Objects.requireNonNull(end, "Окончание не может быть null");
        List<Booking> bookings = snapshot();
        for (Booking booking : bookings) {
            if (!booking.getWorkspace().getId().equals(workspaceId)) {
                continue;
            }
            if (excludedBookingId != null && booking.getId().equals(excludedBookingId)) {
                continue;
            }
            if (!booking.isActive()) {
                continue;
            }
            if (start.isBefore(booking.getEnd()) && end.isAfter(booking.getStart())) {
                return true;
            }
        }
        return false;
    }
}
