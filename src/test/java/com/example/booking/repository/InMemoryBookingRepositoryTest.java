package com.example.booking.repository;

import com.example.booking.TestFixtures;
import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryBookingRepositoryTest {

    private InMemoryBookingRepository repository;
    private User customer;
    private Workspace workspace;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBookingRepository();
        customer = TestFixtures.customer(1L, "customer@mail.ru");
        workspace = TestFixtures.meetingRoom(1L);
    }

    @Test
    void savesAndFindsBooking() {
        Booking booking = TestFixtures.booking(repository.nextId(), customer, workspace,
                TestFixtures.futureStart(2, 10));

        repository.save(booking);

        assertThat(repository.findById(booking.getId())).contains(booking);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void findsByCustomerId() {
        Booking first = TestFixtures.booking(repository.nextId(), customer, workspace,
                TestFixtures.futureStart(2, 10));
        User another = TestFixtures.customer(2L, "another@mail.ru");
        Booking second = TestFixtures.booking(repository.nextId(), another,
                TestFixtures.desk(2L), TestFixtures.futureStart(2, 12));
        repository.save(first);
        repository.save(second);

        List<Booking> result = repository.findByCustomerId(customer.getId());

        assertThat(result).containsExactly(first);
    }

    @Test
    void findsByStatus() {
        Booking pending = TestFixtures.booking(repository.nextId(), customer, workspace,
                TestFixtures.futureStart(2, 10));
        Booking confirmed = TestFixtures.booking(repository.nextId(), customer,
                TestFixtures.desk(2L), TestFixtures.futureStart(2, 12));
        confirmed.confirm();
        repository.save(pending);
        repository.save(confirmed);

        assertThat(repository.findByStatus(BookingStatus.PENDING)).containsExactly(pending);
        assertThat(repository.findByStatus(BookingStatus.CONFIRMED)).containsExactly(confirmed);
    }

    @Test
    void findsByWorkspaceId() {
        Booking first = TestFixtures.booking(repository.nextId(), customer, workspace,
                TestFixtures.futureStart(2, 10));
        Booking second = TestFixtures.booking(repository.nextId(), customer,
                TestFixtures.desk(2L), TestFixtures.futureStart(2, 12));
        repository.save(first);
        repository.save(second);

        assertThat(repository.findByWorkspaceId(workspace.getId())).containsExactly(first);
    }

    @Test
    void detectsOverlapForActiveBookings() {
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        Booking existing = TestFixtures.booking(repository.nextId(), customer, workspace, start);
        repository.save(existing);

        boolean overlap = repository.hasOverlap(workspace.getId(),
                start.plusHours(1), start.plusHours(3), null);

        assertThat(overlap).isTrue();
    }

    @Test
    void ignoresCancelledBookingsWhenCheckingOverlap() {
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        Booking existing = TestFixtures.booking(repository.nextId(), customer, workspace, start);
        existing.cancel();
        repository.save(existing);

        boolean overlap = repository.hasOverlap(workspace.getId(),
                start.plusHours(1), start.plusHours(3), null);

        assertThat(overlap).isFalse();
    }

    @Test
    void ignoresAdjacentIntervals() {
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        Booking existing = TestFixtures.booking(repository.nextId(), customer, workspace, start);
        repository.save(existing);

        boolean overlap = repository.hasOverlap(workspace.getId(),
                start.plusHours(2), start.plusHours(4), null);

        assertThat(overlap).isFalse();
    }

    @Test
    void ignoresExcludedBooking() {
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        Booking existing = TestFixtures.booking(repository.nextId(), customer, workspace, start);
        repository.save(existing);

        boolean overlap = repository.hasOverlap(workspace.getId(), start,
                start.plusHours(2), existing.getId());

        assertThat(overlap).isFalse();
    }

    @Test
    void doesNotDetectOverlapInAnotherWorkspace() {
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        Booking existing = TestFixtures.booking(repository.nextId(), customer, workspace, start);
        repository.save(existing);

        boolean overlap = repository.hasOverlap(99L, start,
                start.plusHours(2), null);

        assertThat(overlap).isFalse();
    }

    @Test
    void returnsBookingsSortedByStart() {
        Booking later = TestFixtures.booking(repository.nextId(), customer, workspace,
                TestFixtures.futureStart(2, 15));
        Booking earlier = TestFixtures.booking(repository.nextId(), customer,
                TestFixtures.desk(2L), TestFixtures.futureStart(2, 9));
        repository.save(later);
        repository.save(earlier);

        List<Booking> result = repository.findAll();

        assertThat(result).containsExactly(earlier, later);
    }
}
