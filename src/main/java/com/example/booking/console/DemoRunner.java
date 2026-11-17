package com.example.booking.console;

import com.example.booking.concurrency.BookingProcessor;
import com.example.booking.concurrency.ProcessingReport;
import com.example.booking.model.Booking;
import com.example.booking.model.BookingSearchFilter;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.WorkspaceType;
import com.example.booking.service.BookingService;
import com.example.booking.service.UserService;
import com.example.booking.service.WorkspaceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Демонстрационный сценарий работы приложения для лабораторной работы №1.
 */
@Component
@Profile("!test")
public class DemoRunner implements CommandLineRunner {

    private final DemoDataSeeder dataSeeder;
    private final ConsolePrinter printer;
    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final BookingService bookingService;
    private final BookingProcessor bookingProcessor;

    /**
     * Создаёт демонстрационный сценарий.
     *
     * @param dataSeeder       наполнитель данных
     * @param printer          вывод в консоль
     * @param userService      сервис пользователей
     * @param workspaceService сервис пространств
     * @param bookingService   сервис бронирований
     * @param bookingProcessor многопоточный обработчик
     */
    public DemoRunner(DemoDataSeeder dataSeeder, ConsolePrinter printer,
                      UserService userService, WorkspaceService workspaceService,
                      BookingService bookingService, BookingProcessor bookingProcessor) {
        this.dataSeeder = dataSeeder;
        this.printer = printer;
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.bookingService = bookingService;
        this.bookingProcessor = bookingProcessor;
    }

    @Override
    public void run(String... args) {
        DemoData data = dataSeeder.seed();

        printer.printHeader("ЛАБОРАТОРНАЯ РАБОТА №1 — СЕРВИС БРОНИРОВАНИЯ");
        printer.printMessage("Тема проекта: сервис бронирования рабочих пространств.");
        printer.printMessage("Модель: пользователи с ролями, пространства, бронирования.");

        printer.printSubHeader("1. Пользователи и роли (наследование User)");
        printer.printUsers(data.getUsers());

        printer.printSubHeader("2. Рабочие пространства (наследование Workspace)");
        printer.printWorkspaces(data.getWorkspaces());

        printer.printSubHeader("3. Все бронирования (List/Map)");
        printer.printBookings(data.getBookings());

        printer.printSubHeader("4. Поиск по полям");
        printer.printMessage("4.1. По e-mail клиента (регистр e-mail нормализуется):");
        List<Booking> byEmail = bookingService.findByCustomerEmail("PETROV@MAIL.RU");
        printer.printBookings(byEmail);

        printer.printMessage("4.2. По статусу PENDING (очередь на подтверждение):");
        List<Booking> pending = bookingService.findByStatus(BookingStatus.PENDING);
        printer.printBookings(pending);

        printer.printMessage("4.3. По типу пространства MEETING_ROOM:");
        List<Booking> meetingRooms = bookingService.findByWorkspaceType(
                WorkspaceType.MEETING_ROOM);
        printer.printBookings(meetingRooms);

        printer.printMessage("4.4. Комбинированный фильтр: ФИО содержит «иван» + CANCELLED:");
        BookingSearchFilter filter = new BookingSearchFilter(
                null, "иван", BookingStatus.CANCELLED, null, null, null);
        List<Booking> filtered = bookingService.search(filter);
        printer.printBookings(filtered);

        printer.printSubHeader("5. Удаление объекта");
        List<Booking> cancelled = bookingService.findByStatus(BookingStatus.CANCELLED);
        if (!cancelled.isEmpty()) {
            Booking booking = cancelled.get(0);
            boolean deleted = bookingService.deleteBooking(booking.getId());
            printer.printMessage("Удалено бронирование #" + booking.getId()
                    + ": " + deleted);
        }
        printer.printMessage("Всего бронирований после удаления: "
                + bookingService.findAll().size());

        printer.printSubHeader("6. Многопоточная обработка очереди");
        printer.printMessage("Ожидают подтверждения: "
                + bookingService.getPendingBookings().size());
        ProcessingReport report = bookingProcessor.processPendingBookings(3);
        printer.printProcessingReport(report);
        printer.printMessage("Ожидают подтверждения после обработки: "
                + bookingService.getPendingBookings().size());

        printer.printSubHeader("7. Статистика");
        printer.printStatistics(bookingService.countByStatus(),
                bookingService.calculateRevenue());

        printer.printMessage("");
        printer.printMessage("Пользователей в системе: " + userService.findAll().size()
                + ", пространств: " + workspaceService.findAll().size()
                + ", бронирований: " + bookingService.findAll().size());
        printer.printMessage("Демонстрация завершена.");
    }
}
