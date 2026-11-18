package com.example.booking.model;

import com.example.booking.util.TextUtils;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Абстрактный пользователь сервиса бронирования.
 *
 * <p>Общие поля и поведение вынесены в базовый класс, а роль и набор
 * разрешений определяются наследниками: {@link AdminUser},
 * {@link ManagerUser}, {@link CustomerUser}.</p>
 *
 * <p>В лабораторной работе №2 класс является JPA-сущностью с иерархией
 * SINGLE_TABLE: все пользователи хранятся в таблице {@code app_users},
 * тип наследника фиксируется дискриминатором {@code user_type}.</p>
 */
@Entity
@Table(name = "app_users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Конструктор для JPA. Не использовать в прикладном коде.
     */
    protected User() {
    }

    /**
     * Создаёт пользователя.
     *
     * @param id           уникальный идентификатор (для JPA допускается null)
     * @param fullName     ФИО
     * @param email        e-mail
     * @param passwordHash хеш пароля
     * @param active       признак активности
     * @param createdAt    дата регистрации
     */
    protected User(Long id, String fullName, String email, String passwordHash,
                   boolean active, LocalDateTime createdAt) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Идентификатор пользователя должен быть положительным");
        }
        this.id = id;
        this.fullName = TextUtils.normalizePersonName(fullName);
        this.email = TextUtils.normalizeEmail(email);
        this.passwordHash = TextUtils.requireText(passwordHash, "passwordHash");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "Дата регистрации не может быть null");
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Возвращает роль пользователя.
     *
     * @return роль
     */
    public abstract UserRole getRole();

    /**
     * Возвращает описание прав пользователя.
     *
     * @return текстовое описание разрешений
     */
    public abstract String getPermissionsDescription();

    /**
     * Может ли пользователь управлять пользователями.
     *
     * @return {@code true}, если может
     */
    public abstract boolean canManageUsers();

    /**
     * Может ли пользователь управлять рабочими пространствами.
     *
     * @return {@code true}, если может
     */
    public abstract boolean canManageWorkspaces();

    /**
     * Может ли пользователь создавать бронирования.
     *
     * @return {@code true}, если может
     */
    public abstract boolean canCreateBookings();

    /**
     * Изменяет ФИО с нормализацией строки.
     *
     * @param newFullName новое ФИО
     */
    public void changeFullName(String newFullName) {
        this.fullName = TextUtils.normalizePersonName(newFullName);
    }

    /**
     * Деактивирует учётную запись.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Активирует учётную запись.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Возвращает подпись пользователя для консольного вывода.
     *
     * @return ФИО и роль
     */
    public String getDisplayName() {
        return fullName + " (" + getRole().getDisplayName() + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        User user = (User) other;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{id=" + id
                + ", fullName='" + fullName + '\''
                + ", email='" + email + '\''
                + ", role=" + getRole()
                + ", active=" + active
                + '}';
    }
}
