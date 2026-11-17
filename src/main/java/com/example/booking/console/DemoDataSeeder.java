package com.example.booking.console;

import com.example.booking.model.Booking;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;
import com.example.booking.service.BookingService;
import com.example.booking.service.UserService;
import com.example.booking.service.WorkspaceService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Наполняет in-memory хранилища демонстрационными данными.
 */
@Component
public class DemoDataSeeder {

    private static final String ADMIN_EMAIL = "admin@booking.local";
    private static final String MANAGER_EMAIL = "manager@booking.local";
    private static final String CUSTOMER_ONE_EMAIL = "petrov@mail.ru";
    private static final String CUSTOMER_TWO_EMAIL = "sokolova@mail.ru";
    private static final String CUSTOMER_THREE_EMAIL = "kuznetsov@mail.ru";

    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final BookingService bookingService;

    /**
     * Создаёт наполнитель.
     *
     * @param userService      сервис пользователей
     * @param workspaceService сервис пространств
     * @param bookingService   сервис бронирований
     */
    public DemoDataSeeder(UserService userService, WorkspaceService workspaceService,
                          BookingService bookingService) {
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.bookingService = bookingService;
    }

    /**
     * Создаёт демонстрационные данные, если хранилище пустое.
     *
     * @return набор данных
     */
    public DemoData seed() {
        if (userService.findAll().isEmpty()) {
            createUsers();
        }
        if (workspaceService.findAll().isEmpty()) {
            createWorkspaces();
        }
        if (bookingService.findAll().isEmpty()) {
            createBookings();
        }
        return new DemoData(userService.findAll(), workspaceService.findAll(),
                bookingService.findAll());
    }

    private void createUsers() {
        userService.registerAdmin("  иванов   алексей петрович ",
                ADMIN_EMAIL, "admin123", "Системный администратор");
        userService.registerManager("смирнова ольга викторовна",
                MANAGER_EMAIL, "manager123", "Отдел бронирования");
        userService.registerCustomer("петров иван сергеевич",
                CUSTOMER_ONE_EMAIL, "customer123",
                "+7 (900) 111-22-33", "ООО «Ромашка»");
        userService.registerCustomer("соколова мария андреевна",
                CUSTOMER_TWO_EMAIL, "customer456",
                "+7 (900) 222-33-44", "ИП Соколова");
        userService.registerCustomer("кузнецов дмитрий олегович",
                CUSTOMER_THREE_EMAIL, "customer789",
                "+7 (900) 333-44-55", "АО «Вектор»");
    }

    private void createWorkspaces() {
        workspaceService.addMeetingRoom("Зал «Альфа»", "3 этаж, каб. 301",
                10, new BigDecimal("1500.00"), true, true);
        workspaceService.addMeetingRoom("Зал «Бета»", "3 этаж, каб. 305",
                6, new BigDecimal("1000.00"), true, false);
        workspaceService.addCoworkingDesk("Место A-12", "2 этаж, open space",
                new BigDecimal("350.00"), true, false);
        workspaceService.addCoworkingDesk("Место B-07", "2 этаж, open space",
                new BigDecimal("300.00"), false, true);
    }

    private void createBookings() {
        List<User> users = userService.findAll();
        List<Workspace> workspaces = workspaceService.findAll();
        User customerOne = findUser(users, CUSTOMER_ONE_EMAIL);
        User customerTwo = findUser(users, CUSTOMER_TWO_EMAIL);
        User customerThree = findUser(users, CUSTOMER_THREE_EMAIL);
        Workspace alpha = workspaces.get(0);
        Workspace beta = workspaces.get(1);
        Workspace deskA = workspaces.get(2);
        Workspace deskB = workspaces.get(3);

        bookingService.createBooking(customerOne.getId(), alpha.getId(),
                future(1, 10, 0), future(1, 12, 0));
        bookingService.createBooking(customerTwo.getId(), beta.getId(),
                future(1, 14, 0), future(1, 16, 0));
        bookingService.createBooking(customerThree.getId(), deskA.getId(),
                future(2, 9, 0), future(2, 11, 0));
        bookingService.createBooking(customerOne.getId(), beta.getId(),
                future(2, 11, 30), future(2, 13, 0));
        bookingService.createBooking(customerTwo.getId(), deskB.getId(),
                future(4, 9, 0), future(4, 10, 0));
        bookingService.createBooking(customerThree.getId(), alpha.getId(),
                future(5, 15, 0), future(5, 17, 0));

        Booking confirmedOne = bookingService.createBooking(customerOne.getId(),
                deskB.getId(), future(3, 10, 0), future(3, 12, 0));
        bookingService.confirmBooking(confirmedOne.getId());

        Booking confirmedTwo = bookingService.createBooking(customerTwo.getId(),
                beta.getId(), future(3, 15, 0), future(3, 17, 0));
        bookingService.confirmBooking(confirmedTwo.getId());

        Booking completed = bookingService.createBooking(customerThree.getId(),
                alpha.getId(), future(6, 10, 0), future(6, 12, 0));
        bookingService.confirmBooking(completed.getId());
        bookingService.completeBooking(completed.getId());

        Booking cancelled = bookingService.createBooking(customerOne.getId(),
                deskA.getId(), future(6, 15, 0), future(6, 17, 0));
        bookingService.cancelBooking(cancelled.getId());
    }

    private User findUser(List<User> users, String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        throw new IllegalStateException("Демонстрационный пользователь не найден: " + email);
    }

    private LocalDateTime future(int days, int hour, int minute) {
        return LocalDateTime.now()
                .plusDays(days)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
    }
}
