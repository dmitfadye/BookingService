package com.example.booking.model;

import com.example.booking.TestFixtures;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingTest {

    @Test
    void calculatesTotalPriceForTwoHours() {
        User customer = TestFixtures.customer(1L, "customer@mail.ru");
        Workspace workspace = TestFixtures.meetingRoom(1L);
        LocalDateTime start = TestFixtures.futureStart(2, 10);

        Booking booking = TestFixtures.booking(1L, customer, workspace, start);

        assertThat(booking.getTotalPrice()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void calculatesTotalPriceForHalfHour() {
        User customer = TestFixtures.customer(1L, "customer@mail.ru");
        Workspace workspace = TestFixtures.meetingRoom(1L);
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        Booking booking = new Booking(1L, customer, workspace, start,
                start.plusMinutes(90), LocalDateTime.now());

        assertThat(booking.getDurationHours()).isEqualByComparingTo(new BigDecimal("1.50"));
        assertThat(booking.getTotalPrice()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    void rejectsPeriodWithEndBeforeStart() {
        User customer = TestFixtures.customer(1L, "customer@mail.ru");
        Workspace workspace = TestFixtures.meetingRoom(1L);
        LocalDateTime start = TestFixtures.futureStart(2, 10);

        assertThatThrownBy(() -> new Booking(1L, customer, workspace,
                start, start.minusHours(1), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("позже начала");
    }

    @Test
    void supportsStatusTransitions() {
        Booking booking = TestFixtures.booking(1L,
                TestFixtures.customer(1L, "customer@mail.ru"),
                TestFixtures.meetingRoom(1L), TestFixtures.futureStart(2, 10));

        booking.confirm();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        booking.complete();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        assertThat(booking.isActive()).isFalse();
    }

    @Test
    void cancelsPendingBooking() {
        Booking booking = TestFixtures.booking(1L,
                TestFixtures.customer(1L, "customer@mail.ru"),
                TestFixtures.meetingRoom(1L), TestFixtures.futureStart(2, 10));

        booking.cancel();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(booking.isActive()).isFalse();
    }

    @Test
    void rejectsRepeatedConfirmation() {
        Booking booking = TestFixtures.booking(1L,
                TestFixtures.customer(1L, "customer@mail.ru"),
                TestFixtures.meetingRoom(1L), TestFixtures.futureStart(2, 10));
        booking.confirm();

        assertThatThrownBy(booking::confirm)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PENDING");
    }

    @Test
    void rejectsCompletionOfPendingBooking() {
        Booking booking = TestFixtures.booking(1L,
                TestFixtures.customer(1L, "customer@mail.ru"),
                TestFixtures.meetingRoom(1L), TestFixtures.futureStart(2, 10));

        assertThatThrownBy(booking::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("подтверждённое");
    }

    @Test
    void rejectsCancellationOfCompletedBooking() {
        Booking booking = TestFixtures.booking(1L,
                TestFixtures.customer(1L, "customer@mail.ru"),
                TestFixtures.meetingRoom(1L), TestFixtures.futureStart(2, 10));
        booking.confirm();
        booking.complete();

        assertThatThrownBy(booking::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("активное");
    }

    @Test
    void detectsOverlappingBookings() {
        User customer = TestFixtures.customer(1L, "customer@mail.ru");
        Workspace workspace = TestFixtures.meetingRoom(1L);
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        Booking first = TestFixtures.booking(1L, customer, workspace, start);
        Booking second = TestFixtures.booking(2L, customer, workspace,
                start.plusHours(1));

        assertThat(first.overlaps(second)).isTrue();
    }

    @Test
    void adjacentBookingsDoNotOverlap() {
        User customer = TestFixtures.customer(1L, "customer@mail.ru");
        Workspace workspace = TestFixtures.meetingRoom(1L);
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        Booking first = TestFixtures.booking(1L, customer, workspace, start);
        Booking second = TestFixtures.booking(2L, customer, workspace,
                start.plusHours(2));

        assertThat(first.overlaps(second)).isFalse();
    }
}
