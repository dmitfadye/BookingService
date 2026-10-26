package com.example.booking.model;

import com.example.booking.util.TextUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

/**
 * Фильтр для поиска бронирований по нескольким полям.
 *
 * <p>Любое поле может быть {@code null} — тогда оно не учитывается при поиске.
 * Фильтр относится к модели и отвечает только за проверку соответствия
 * бронирования заданным условиям.</p>
 */
public class BookingSearchFilter {

    private final String customerEmail;
    private final String customerNamePart;
    private final BookingStatus status;
    private final WorkspaceType workspaceType;
    private final LocalDate date;
    private final BigDecimal maxPrice;

    /**
     * Создаёт фильтр.
     *
     * @param customerEmail    e-mail клиента или {@code null}
     * @param customerNamePart часть ФИО клиента или {@code null}
     * @param status           статус бронирования или {@code null}
     * @param workspaceType    тип пространства или {@code null}
     * @param date             дата начала бронирования или {@code null}
     * @param maxPrice         максимальная стоимость или {@code null}
     */
    public BookingSearchFilter(String customerEmail, String customerNamePart,
                               BookingStatus status, WorkspaceType workspaceType,
                               LocalDate date, BigDecimal maxPrice) {
        this.customerEmail = customerEmail == null
                ? null
                : TextUtils.normalizeEmail(customerEmail);
        this.customerNamePart = customerNamePart == null
                ? null
                : customerNamePart.trim().toLowerCase(Locale.ROOT);
        if (this.customerNamePart != null && this.customerNamePart.isEmpty()) {
            throw new IllegalArgumentException("Часть ФИО для поиска не может быть пустой");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Максимальная стоимость не может быть отрицательной");
        }
        this.status = status;
        this.workspaceType = workspaceType;
        this.date = date;
        this.maxPrice = maxPrice;
    }

    /**
     * Фильтр по e-mail клиента.
     *
     * @param email e-mail
     * @return фильтр
     */
    public static BookingSearchFilter byCustomerEmail(String email) {
        return new BookingSearchFilter(email, null, null, null, null, null);
    }

    /**
     * Фильтр по статусу.
     *
     * @param status статус
     * @return фильтр
     */
    public static BookingSearchFilter byStatus(BookingStatus status) {
        return new BookingSearchFilter(null, null, Objects.requireNonNull(status), null, null, null);
    }

    /**
     * Фильтр по типу пространства.
     *
     * @param workspaceType тип
     * @return фильтр
     */
    public static BookingSearchFilter byWorkspaceType(WorkspaceType workspaceType) {
        return new BookingSearchFilter(null, null, null,
                Objects.requireNonNull(workspaceType), null, null);
    }

    /**
     * Проверяет, удовлетворяет ли бронирование фильтру.
     *
     * @param booking бронирование
     * @return {@code true}, если все заданные условия выполнены
     */
    public boolean matches(Booking booking) {
        Objects.requireNonNull(booking, "Бронирование не может быть null");
        if (customerEmail != null
                && !booking.getCustomer().getEmail().equals(customerEmail)) {
            return false;
        }
        if (customerNamePart != null
                && !booking.getCustomer().getFullName()
                        .toLowerCase(Locale.ROOT)
                        .contains(customerNamePart)) {
            return false;
        }
        if (status != null && booking.getStatus() != status) {
            return false;
        }
        if (workspaceType != null
                && booking.getWorkspace().getType() != workspaceType) {
            return false;
        }
        if (date != null && !booking.getStart().toLocalDate().equals(date)) {
            return false;
        }
        return maxPrice == null
                || booking.getTotalPrice().compareTo(maxPrice) <= 0;
    }
}
