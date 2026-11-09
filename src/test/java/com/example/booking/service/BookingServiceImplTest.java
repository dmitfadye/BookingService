package com.example.booking.service;

import com.example.booking.TestFixtures;
import com.example.booking.model.Booking;
import com.example.booking.model.BookingSearchFilter;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;
import com.example.booking.repository.InMemoryBookingRepository;
import com.example.booking.repository.InMemoryUserRepository;
import com.example.booking.repository.InMemoryWorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingServiceImplTest {

    private InMemoryUserRepository userRepository;
    private InMemoryWorkspaceRepository workspaceRepository;
    private InMemoryBookingRepository bookingRepository;
    private ConcurrentBookingQueue queue;
    private BookingService service;
    private User customer;
    private Workspace workspace;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        workspaceRepository = new InMemoryWorkspaceRepository();
        bookingRepository = new InMemoryBookingRepository();
        queue = new ConcurrentBookingQueue();
        service = new BookingServiceImpl(bookingRepository, userRepository,
                workspaceRepository, queue);

        customer = TestFixtures.customer(userRepository.nextId(), "customer@mail.ru");
        userRepository.save(customer);
        workspace = TestFixtures.meetingRoom(workspaceRepository.nextId());
        workspaceRepository.save(workspace);
    }

    @Test
    void createsBookingAndAddsItToQueue() {
        LocalDateTime start = TestFixtures.futureStart(2, 10);

        Booking booking = service.createBooking(customer.getId(), workspace.getId(),
                start, start.plusHours(2));

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getTotalPrice()).isEqualByComparingTo("2000.00");
        assertThat(service.findAll()).containsExactly(booking);
        assertThat(service.getPendingBookings()).containsExactly(booking);
        assertThat(queue.size()).isEqualTo(1);
    }

    @Test
    void rejectsOverlappingBooking() {
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        service.createBooking(customer.getId(), workspace.getId(), start, start.plusHours(2));

        assertThatThrownBy(() -> service.createBooking(customer.getId(), workspace.getId(),
                start.plusHours(1), start.plusHours(3)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("занято");
    }

    @Test
    void allowsAdjacentBooking() {
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        service.createBooking(customer.getId(), workspace.getId(), start, start.plusHours(2));

        Booking second = service.createBooking(customer.getId(), workspace.getId(),
                start.plusHours(2), start.plusHours(4));

        assertThat(service.findAll()).hasSize(2);
        assertThat(second.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void rejectsBookingInPast() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);

        assertThatThrownBy(() -> service.createBooking(customer.getId(), workspace.getId(),
                past, past.plusHours(2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("прошлом");
    }

    @Test
    void rejectsInactiveWorkspace() {
        workspace.deactivate();
        workspaceRepository.save(workspace);
        LocalDateTime start = TestFixtures.futureStart(2, 10);

        assertThatThrownBy(() -> service.createBooking(customer.getId(), workspace.getId(),
                start, start.plusHours(2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("недоступно");
    }

    @Test
    void rejectsInactiveUser() {
        customer.deactivate();
        userRepository.save(customer);
        LocalDateTime start = TestFixtures.futureStart(2, 10);

        assertThatThrownBy(() -> service.createBooking(customer.getId(), workspace.getId(),
                start, start.plusHours(2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("деактивирован");
    }

    @Test
    void findsBookingsByCustomerEmailIgnoringCase() {
        createBooking(2, 10, workspace);

        List<Booking> result = service.findByCustomerEmail("CUSTOMER@MAIL.RU");

        assertThat(result).hasSize(1);
    }

    @Test
    void returnsEmptyListForUnknownEmail() {
        assertThat(service.findByCustomerEmail("unknown@mail.ru")).isEmpty();
    }

    @Test
    void findsBookingsByWorkspaceType() {
        createBooking(2, 10, workspace);
        Workspace desk = TestFixtures.desk(workspaceRepository.nextId());
        workspaceRepository.save(desk);
        createBooking(2, 12, desk);

        List<Booking> result = service.findByWorkspaceType(WorkspaceType.COWORKING_DESK);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWorkspace().getType())
                .isEqualTo(WorkspaceType.COWORKING_DESK);
    }

    @Test
    void searchesByCombinedFilter() {
        createBooking(2, 10, workspace);

        BookingSearchFilter filter = new BookingSearchFilter(
                "customer@mail.ru", "петров", BookingStatus.PENDING,
                WorkspaceType.MEETING_ROOM, null, new BigDecimal("3000.00"));

        assertThat(service.search(filter)).hasSize(1);
    }

    @Test
    void confirmsBookingAndRemovesItFromQueue() {
        Booking booking = createBooking(2, 10, workspace);

        service.confirmBooking(booking.getId());

        assertThat(service.getById(booking.getId()).getStatus())
                .isEqualTo(BookingStatus.CONFIRMED);
        assertThat(queue.size()).isZero();
    }

    @Test
    void cancelsBooking() {
        Booking booking = createBooking(2, 10, workspace);

        service.cancelBooking(booking.getId());

        assertThat(service.getById(booking.getId()).getStatus())
                .isEqualTo(BookingStatus.CANCELLED);
        assertThat(queue.size()).isZero();
    }

    @Test
    void completesConfirmedBooking() {
        Booking booking = createBooking(2, 10, workspace);
        service.confirmBooking(booking.getId());

        service.completeBooking(booking.getId());

        assertThat(service.getById(booking.getId()).getStatus())
                .isEqualTo(BookingStatus.COMPLETED);
    }

    @Test
    void deletesBookingAndRemovesItFromQueue() {
        Booking booking = createBooking(2, 10, workspace);

        boolean deleted = service.deleteBooking(booking.getId());

        assertThat(deleted).isTrue();
        assertThat(service.findAll()).isEmpty();
        assertThat(queue.size()).isZero();
    }

    @Test
    void countsBookingsByStatus() {
        Booking pending = createBooking(2, 10, workspace);
        Booking confirmed = createBooking(2, 12, workspace);
        service.confirmBooking(confirmed.getId());

        Map<BookingStatus, Long> counts = service.countByStatus();

        assertThat(counts.get(BookingStatus.PENDING)).isEqualTo(1L);
        assertThat(counts.get(BookingStatus.CONFIRMED)).isEqualTo(1L);
        assertThat(counts.get(BookingStatus.CANCELLED)).isZero();
        assertThat(pending.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void calculatesRevenueOnlyForConfirmedAndCompletedBookings() {
        Booking pending = createBooking(2, 10, workspace);
        Booking confirmed = createBooking(2, 12, workspace);
        service.confirmBooking(confirmed.getId());

        BigDecimal revenue = service.calculateRevenue();

        assertThat(revenue).isEqualByComparingTo("2000.00");
        assertThat(pending.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void throwsWhenBookingNotFound() {
        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не найдено");
    }

    private Booking createBooking(int day, int hour, Workspace targetWorkspace) {
        LocalDateTime start = TestFixtures.futureStart(day, hour);
        return service.createBooking(customer.getId(), targetWorkspace.getId(),
                start, start.plusHours(2));
    }
}
