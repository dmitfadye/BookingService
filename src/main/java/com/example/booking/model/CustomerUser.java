package com.example.booking.model;

import com.example.booking.util.TextUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

/**
 * Клиент: создаёт и отменяет собственные бронирования.
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class CustomerUser extends User {

    @Column(length = 50)
    private String phone;

    @Column(length = 255)
    private String company;

    /**
     * Конструктор для JPA. Не использовать в прикладном коде.
     */
    protected CustomerUser() {
    }

    /**
     * Создаёт клиента.
     *
     * @param id           идентификатор (для JPA допускается null)
     * @param fullName     ФИО
     * @param email        e-mail
     * @param passwordHash хеш пароля
     * @param active       признак активности
     * @param createdAt    дата регистрации
     * @param phone        телефон
     * @param company      компания
     */
    public CustomerUser(Long id, String fullName, String email, String passwordHash,
                        boolean active, LocalDateTime createdAt, String phone, String company) {
        super(id, fullName, email, passwordHash, active, createdAt);
        this.phone = TextUtils.requireText(phone, "phone");
        this.company = TextUtils.requireText(company, "company");
    }

    public String getPhone() {
        return phone;
    }

    public String getCompany() {
        return company;
    }

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER;
    }

    @Override
    public String getPermissionsDescription() {
        return "Создание и отмена собственных бронирований";
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canManageWorkspaces() {
        return false;
    }

    @Override
    public boolean canCreateBookings() {
        return true;
    }
}
