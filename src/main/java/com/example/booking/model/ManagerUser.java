package com.example.booking.model;

import com.example.booking.util.TextUtils;

import java.time.LocalDateTime;

/**
 * Менеджер: управляет рабочими пространствами и подтверждает бронирования.
 */
public class ManagerUser extends User {

    private final String department;

    /**
     * Создаёт менеджера.
     *
     * @param id           идентификатор
     * @param fullName     ФИО
     * @param email        e-mail
     * @param passwordHash хеш пароля
     * @param active       признак активности
     * @param createdAt    дата регистрации
     * @param department   отдел
     */
    public ManagerUser(Long id, String fullName, String email, String passwordHash,
                       boolean active, LocalDateTime createdAt, String department) {
        super(id, fullName, email, passwordHash, active, createdAt);
        this.department = TextUtils.requireText(department, "department");
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public UserRole getRole() {
        return UserRole.MANAGER;
    }

    @Override
    public String getPermissionsDescription() {
        return "Управление пространствами и подтверждение бронирований";
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canManageWorkspaces() {
        return true;
    }

    @Override
    public boolean canCreateBookings() {
        return true;
    }
}
