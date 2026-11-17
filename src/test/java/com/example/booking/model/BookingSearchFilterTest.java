package com.example.booking.model;

import com.example.booking.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingSearchFilterTest {

    private Booking booking;
    private LocalDateTime start;

    @BeforeEach
    void setUp() {
        User customer = TestFixtures.customer(1L, "petrov@mail.ru");
        Workspace workspace = TestFixtures.meetingRoom(1L);
        start = TestFixtures.futureStart(2, 10);
        booking = TestFixtures.booking(1L, customer, workspace, start);
    }

    @Test
    void matchesByNormalizedEmail() {
        BookingSearchFilter filter = BookingSearchFilter.byCustomerEmail(" PETROV@MAIL.RU ");

        assertThat(filter.matches(booking)).isTrue();
    }

    @Test
    void matchesByCustomerNamePartIgnoringCase() {
        BookingSearchFilter filter = new BookingSearchFilter(
                null, "ИВАН", null, null, null, null);

        assertThat(filter.matches(booking)).isTrue();
    }

    @Test
    void matchesByStatus() {
        booking.confirm();
        BookingSearchFilter filter = BookingSearchFilter.byStatus(BookingStatus.CONFIRMED);

        assertThat(filter.matches(booking)).isTrue();
    }

    @Test
    void matchesByWorkspaceType() {
        BookingSearchFilter filter = BookingSearchFilter.byWorkspaceType(
                WorkspaceType.MEETING_ROOM);

        assertThat(filter.matches(booking)).isTrue();
    }

    @Test
    void matchesByDateAndMaxPrice() {
        BookingSearchFilter filter = new BookingSearchFilter(
                null, null, null, null, start.toLocalDate(), new BigDecimal("2000.00"));

        assertThat(filter.matches(booking)).isTrue();
    }

    @Test
    void rejectsByDateWhenDateDiffers() {
        BookingSearchFilter filter = new BookingSearchFilter(
                null, null, null, null, LocalDate.of(2020, 1, 1), null);

        assertThat(filter.matches(booking)).isFalse();
    }

    @Test
    void rejectsWhenPriceExceedsLimit() {
        BookingSearchFilter filter = new BookingSearchFilter(
                null, null, null, null, null, new BigDecimal("1000.00"));

        assertThat(filter.matches(booking)).isFalse();
    }

    @Test
    void combinesAllConditions() {
        booking.confirm();
        BookingSearchFilter filter = new BookingSearchFilter(
                "petrov@mail.ru", "петров", BookingStatus.CONFIRMED,
                WorkspaceType.MEETING_ROOM, start.toLocalDate(),
                new BigDecimal("2500.00"));

        assertThat(filter.matches(booking)).isTrue();
    }

    @Test
    void rejectsBlankNamePart() {
        assertThatThrownBy(() -> new BookingSearchFilter(
                null, "   ", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNegativeMaxPrice() {
        assertThatThrownBy(() -> new BookingSearchFilter(
                null, null, null, null, null, new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
