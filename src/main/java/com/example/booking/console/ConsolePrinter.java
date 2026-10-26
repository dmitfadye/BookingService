package com.example.booking.console;

import com.example.booking.concurrency.ProcessedBooking;
import com.example.booking.concurrency.ProcessingReport;
import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Формирует консольный вывод демонстрационного приложения.
 */
@Component
public class ConsolePrinter {

    private static final int WIDTH = 126;
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter SHORT_DATE_TIME =
            DateTimeFormatter.ofPattern("dd.MM HH:mm");

    /**
     * Печатает заголовок раздела.
     *
     * @param title заголовок
     */
    public void printHeader(String title) {
        System.out.println();
        System.out.println("=".repeat(WIDTH));
        System.out.println(center(title, WIDTH));
        System.out.println("=".repeat(WIDTH));
    }

    /**
     * Печатает подзаголовок.
     *
     * @param title подзаголовок
     */
    public void printSubHeader(String title) {
        System.out.println();
        System.out.println("--- " + title + " " + "-".repeat(Math.max(2,
                WIDTH - title.length() - 5)));
    }

    /**
     * Печатает список пользователей.
     *
     * @param users пользователи
     */
    public void printUsers(List<User> users) {
        for (User user : users) {
            System.out.printf("#%-3d %s — %s%n", user.getId(), user.getFullName(),
                    user.getRole().getDisplayName());
            System.out.printf("     e-mail: %-28s активен: %s%n",
                    user.getEmail(), user.isActive() ? "да" : "нет");
            System.out.println("     права: " + user.getPermissionsDescription());
        }
    }

    /**
     * Печатает таблицу рабочих пространств.
     *
     * @param workspaces пространства
     */
    public void printWorkspaces(List<Workspace> workspaces) {
        System.out.printf("%-3s | %-20s | %-16s | %-5s | %-10s | %s%n",
                "ID", "Пространство", "Тип", "Мест", "Цена/час", "Оснащение");
        System.out.println("-".repeat(WIDTH));
        for (Workspace workspace : workspaces) {
            System.out.printf("%-3d | %-20s | %-16s | %-5d | %-10s | %s%n",
                    workspace.getId(), workspace.getName(),
                    workspace.getType().getDisplayName(), workspace.getCapacity(),
                    formatMoney(workspace.getPricePerHour()),
                    workspace.getEquipmentDescription());
        }
    }

    /**
     * Печатает таблицу бронирований.
     *
     * @param bookings бронирования
     */
    public void printBookings(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("Бронирования не найдены.");
            return;
        }
        System.out.printf("%-3s | %-25s | %-20s | %-11s | %-11s | %-20s | %12s%n",
                "ID", "Клиент", "Пространство", "Начало", "Окончание",
                "Статус", "Сумма");
        System.out.println("-".repeat(WIDTH));
        for (Booking booking : bookings) {
            System.out.printf("%-3d | %-25s | %-20s | %-11s | %-11s | %-20s | %12s%n",
                    booking.getId(), booking.getCustomer().getFullName(),
                    booking.getWorkspace().getName(),
                    booking.getStart().format(SHORT_DATE_TIME),
                    booking.getEnd().format(SHORT_DATE_TIME),
                    booking.getStatus().getDisplayName(),
                    formatMoney(booking.getTotalPrice()));
        }
    }

    /**
     * Печатает отчёт многопоточной обработки.
     *
     * @param report отчёт
     */
    public void printProcessingReport(ProcessingReport report) {
        System.out.println();
        System.out.println("Использованные потоки: " + report.getThreadNames());
        System.out.printf("Успешно: %d, ошибок: %d, время: %d мс, параллельно: %s%n",
                report.getSuccessfulCount(), report.getFailedCount(),
                report.getDurationMillis(), report.isParallel() ? "да" : "нет");
        System.out.printf("%-3s | %-20s | %-20s | %s%n",
                "ID", "Пространство", "Поток", "Результат");
        System.out.println("-".repeat(WIDTH));
        for (ProcessedBooking item : report.getProcessedBookings()) {
            System.out.printf("%-3d | %-20s | %-20s | %s%n",
                    item.getBookingId(), item.getWorkspaceName(),
                    item.getThreadName(), item.getMessage());
        }
    }

    /**
     * Печатает статистику по бронированиям.
     *
     * @param counts  количество по статусам
     * @param revenue выручка
     */
    public void printStatistics(Map<BookingStatus, Long> counts, BigDecimal revenue) {
        for (BookingStatus status : BookingStatus.values()) {
            System.out.printf("  %-22s %d%n", status.getDisplayName(), counts.get(status));
        }
        System.out.println("  Выручка (CONFIRMED + COMPLETED): " + formatMoney(revenue));
    }

    /**
     * Печатает строку с датой и временем.
     *
     * @param booking бронирование
     */
    public void printBookingPeriod(Booking booking) {
        System.out.println("  #" + booking.getId() + ": "
                + booking.getStart().format(DATE_TIME) + " — "
                + booking.getEnd().format(DATE_TIME));
    }

    /**
     * Печатает поясняющее сообщение.
     *
     * @param message сообщение
     */
    public void printMessage(String message) {
        System.out.println(message);
    }

    private String formatMoney(BigDecimal amount) {
        return amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() + " руб.";
    }

    private String center(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int leftPadding = (width - text.length()) / 2;
        return " ".repeat(leftPadding) + text;
    }
}
