package com.example.booking.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Бронирование рабочего пространства клиентом.
 */
public class Booking implements Identifiable<Long> {

    private final Long id;
    private final User customer;
    private final Workspace workspace;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final LocalDateTime createdAt;
    private final BigDecimal totalPrice;
    private BookingStatus status;

    /**
     * Создаёт новое бронирование в статусе {@link BookingStatus#PENDING}.
     *
     * @param id        идентификатор
     * @param customer  пользователь, создающий бронирование
     * @param workspace рабочее пространство
     * @param start     начало
     * @param end       окончание
     * @param createdAt дата создания
     */
    public Booking(Long id, User customer, Workspace workspace,
                   LocalDateTime start, LocalDateTime end, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "Идентификатор бронирования не может быть null");
        if (id <= 0) {
            throw new IllegalArgumentException("Идентификатор бронирования должен быть положительным");
        }
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
        return id.equals(booking.id);
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
