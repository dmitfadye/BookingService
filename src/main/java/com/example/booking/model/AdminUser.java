package com.example.booking.model;

import com.example.booking.util.TextUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

/**
 * Администратор сервиса: управляет пользователями, пространствами и бронированиями.
 */
@Entity
@DiscriminatorValue("ADMIN")
public class AdminUser extends User {

    @Column(length = 255)
    private String position;

    /**
     * Конструктор для JPA. Не использовать в прикладном коде.
     */
    protected AdminUser() {
    }

    /**
     * Создаёт администратора.
     *
     * @param id           идентификатор (для JPA допускается null)
     * @param fullName     ФИО
     * @param email        e-mail
     * @param passwordHash хеш пароля
     * @param active       признак активности
     * @param createdAt    дата регистрации
     * @param position     должность
     */
    public AdminUser(Long id, String fullName, String email, String passwordHash,
                     boolean active, LocalDateTime createdAt, String position) {
        super(id, fullName, email, passwordHash, active, createdAt);
        this.position = TextUtils.requireText(position, "position");
    }

    public String getPosition() {
        return position;
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }

    @Override
    public String getPermissionsDescription() {
        return "Полный доступ: пользователи, пространства, бронирования";
    }

    @Override
    public boolean canManageUsers() {
        return true;
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
