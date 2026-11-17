package com.example.booking;

import com.example.booking.model.AdminUser;
import com.example.booking.model.Booking;
import com.example.booking.model.CoworkingDesk;
import com.example.booking.model.CustomerUser;
import com.example.booking.model.ManagerUser;
import com.example.booking.model.MeetingRoom;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Фабрика тестовых объектов.
 */
public final class TestFixtures {

    private static final LocalDateTime CREATED_AT =
            LocalDateTime.of(2026, 1, 15, 12, 0);

    private TestFixtures() {
    }

    /**
     * Создаёт клиента.
     *
     * @param id    идентификатор
     * @param email e-mail
     * @return клиент
     */
    public static CustomerUser customer(Long id, String email) {
        return new CustomerUser(id, "петров иван сергеевич", email, "hash",
                true, CREATED_AT, "+7 (900) 111-22-33", "ООО «Ромашка»");
    }

    /**
     * Создаёт администратора.
     *
     * @param id    идентификатор
     * @param email e-mail
     * @return администратор
     */
    public static AdminUser admin(Long id, String email) {
        return new AdminUser(id, "иванов алексей петрович", email, "hash",
                true, CREATED_AT, "Системный администратор");
    }

    /**
     * Создаёт менеджера.
     *
     * @param id    идентификатор
     * @param email e-mail
     * @return менеджер
     */
    public static ManagerUser manager(Long id, String email) {
        return new ManagerUser(id, "смирнова ольга викторовна", email, "hash",
                true, CREATED_AT, "Отдел бронирования");
    }

    /**
     * Создаёт переговорную.
     *
     * @param id идентификатор
     * @return переговорная
     */
    public static MeetingRoom meetingRoom(Long id) {
        return new MeetingRoom(id, "Зал «Альфа»", "3 этаж", 10,
                new BigDecimal("1000.00"), true, true, true);
    }

    /**
     * Создаёт коворкинг-место.
     *
     * @param id идентификатор
     * @return коворкинг-место
     */
    public static CoworkingDesk desk(Long id) {
        return new CoworkingDesk(id, "Место A-12", "2 этаж", 1,
                new BigDecimal("300.00"), true, true, false);
    }

    /**
     * Создаёт бронирование на два часа.
     *
     * @param id        идентификатор
     * @param customer  пользователь
     * @param workspace пространство
     * @param start     начало
     * @return бронирование
     */
    public static Booking booking(Long id, User customer, Workspace workspace,
                                  LocalDateTime start) {
        return new Booking(id, customer, workspace, start, start.plusHours(2),
                CREATED_AT);
    }

    /**
     * Возвращает будущую дату с указанным часом.
     *
     * @param days количество дней от текущей даты
     * @param hour час
     * @return дата и время
     */
    public static LocalDateTime futureStart(int days, int hour) {
        return LocalDateTime.now()
                .plusDays(days)
                .withHour(hour)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }
}
