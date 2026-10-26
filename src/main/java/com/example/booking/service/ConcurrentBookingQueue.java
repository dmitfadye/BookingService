package com.example.booking.service;

import com.example.booking.model.Booking;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Потокобезопасная очередь ожидающих бронирований.
 */
@Component
public class ConcurrentBookingQueue implements BookingQueue {

    private final Queue<Booking> pendingBookings = new ConcurrentLinkedQueue<>();

    @Override
    public void add(Booking booking) {
        pendingBookings.add(Objects.requireNonNull(booking, "Бронирование не может быть null"));
    }

    @Override
    public List<Booking> snapshot() {
        return new ArrayList<>(pendingBookings);
    }

    @Override
    public boolean remove(Long bookingId) {
        if (bookingId == null) {
            return false;
        }
        return pendingBookings.removeIf(booking -> booking.getId().equals(bookingId));
    }

    @Override
    public int size() {
        return pendingBookings.size();
    }

    @Override
    public void clear() {
        pendingBookings.clear();
    }
}
