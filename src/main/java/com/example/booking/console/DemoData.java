package com.example.booking.console;

import com.example.booking.model.Booking;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;

import java.util.Collections;
import java.util.List;

/**
 * Набор демонстрационных данных.
 */
public class DemoData {

    private final List<User> users;
    private final List<Workspace> workspaces;
    private final List<Booking> bookings;

    /**
     * Создаёт набор данных.
     *
     * @param users      пользователи
     * @param workspaces пространства
     * @param bookings   бронирования
     */
    public DemoData(List<User> users, List<Workspace> workspaces, List<Booking> bookings) {
        this.users = Collections.unmodifiableList(users);
        this.workspaces = Collections.unmodifiableList(workspaces);
        this.bookings = Collections.unmodifiableList(bookings);
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Workspace> getWorkspaces() {
        return workspaces;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}
