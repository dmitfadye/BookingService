package com.example.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Бронирование рабочего пространства клиентом.
 *
 * <p>В лабораторной работе №2 класс является JPA-сущностью: хранится
 * в таблице {@code bookings}, клиент и пространство связаны через
 * внешние ключи {@code customer_id} и {@code workspace_id}.</p>
 */
@Entity
@Table(name = "bookings")
public class Booking implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    /**
     * Конструктор для JPA. Не использовать в прикладном коде.
     */
    protected Booking() {
    }

    /**
     * Создаёт новое бронирование в статусе {@link BookingStatus#PENDING}.
     *
     * @param id        идентификатор (для JPA допускается null)
     * @param customer  пользователь, создающий бронирование
     * @param workspace рабочее пространство
     * @param start     начало
     * @param end       окончание
     * @param createdAt дата создания
     */
    public Booking(Long id, User customer, Workspace workspace,
                   LocalDateTime start, LocalDateTime end, LocalDateTime createdAt) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Идентификатор бронирования должен быть положительным");
        }
        this.id = id;
        this.customer = Objects.requireNonNull(customer, "Клиент не может быть null");
        this.workspace = Objects.requireNonNull(workspace, "Пространство не может быть null");
        validatePeriod(start, end);
        this.start = start;
        this.end = end;
        this.createdAt = Objects.requireNonNull(createdAt, "Дата создания не может быть null");
        this.status = BookingStatus.PENDING;
        this.totalPrice = calculateTotalPrice(start, end, workspace.getPricePerHour());
    }

    @Override
    public Long getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    /**
     * Обновляет период бронирования с пересчётом стоимости.
     *
     * @param newStart новое начало
     * @param newEnd   новое окончание
     */
    public void reschedule(LocalDateTime newStart, LocalDateTime newEnd) {
        validatePeriod(newStart, newEnd);
        this.start = newStart;
        this.end = newEnd;
        this.totalPrice = calculateTotalPrice(newStart, newEnd, workspace.getPricePerHour());
    }

    /**
     * Возвращает длительность бронирования в часах.
     *
     * @return длительность с точностью до двух знаков
     */
    public BigDecimal getDurationHours() {
        long minutes = ChronoUnit.MINUTES.between(start, end);
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    /**
     * Проверяет, занимает ли бронирование время в расписании.
     *
     * @return {@code true}, если статус активный
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * Подтверждает бронирование. Допустимо только из статуса PENDING.
     */
    public void confirm() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Подтвердить можно только бронирование в статусе PENDING, текущий статус: "
                            + status.getDisplayName());
        }
        this.status = BookingStatus.CONFIRMED;
    }

    /**
     * Отменяет бронирование. Допустимо для PENDING и CONFIRMED.
     */
    public void cancel() {
        if (!status.isActive()) {
            throw new IllegalStateException(
                    "Отменить можно только активное бронирование, текущий статус: "
                            + status.getDisplayName());
        }
        this.status = BookingStatus.CANCELLED;
    }

    /**
     * Завершает бронирование. Допустимо только из статуса CONFIRMED.
     */
    public void complete() {
        if (status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Завершить можно только подтверждённое бронирование, текущий статус: "
                            + status.getDisplayName());
        }
        this.status = BookingStatus.COMPLETED;
    }

    /**
     * Проверяет пересечение интервалов двух бронирований.
     *
     * @param other другое бронирование
     * @return {@code true}, если интервалы пересекаются
     */
    public boolean overlaps(Booking other) {
        Objects.requireNonNull(other, "Бронирование для сравнения не может быть null");
        return workspace.getId().equals(other.workspace.getId())
                && start.isBefore(other.end)
                && end.isAfter(other.start);
    }

    private static void validatePeriod(LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(start, "Начало бронирования не может быть null");
        Objects.requireNonNull(end, "Окончание бронирования не может быть null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Окончание бронирования должно быть позже начала");
        }
    }

    private static BigDecimal calculateTotalPrice(LocalDateTime start, LocalDateTime end,
                                                  BigDecimal pricePerHour) {
        long minutes = ChronoUnit.MINUTES.between(start, end);
        BigDecimal hours = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return pricePerHour.multiply(hours).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Booking booking = (Booking) other;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Booking{id=" + id
                + ", customer='" + customer.getFullName() + '\''
                + ", workspace='" + workspace.getName() + '\''
                + ", start=" + start
                + ", end=" + end
                + ", status=" + status
                + ", totalPrice=" + totalPrice
                + '}';
    }
}
