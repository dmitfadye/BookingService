package com.example.booking.concurrency;

import com.example.booking.TestFixtures;
import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.User;
import com.example.booking.model.Workspace;
import com.example.booking.repository.InMemoryBookingRepository;
import com.example.booking.repository.InMemoryUserRepository;
import com.example.booking.repository.InMemoryWorkspaceRepository;
import com.example.booking.service.BookingService;
import com.example.booking.service.BookingServiceImpl;
import com.example.booking.service.ConcurrentBookingQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParallelBookingProcessorTest {

    private InMemoryUserRepository userRepository;
    private InMemoryWorkspaceRepository workspaceRepository;
    private InMemoryBookingRepository bookingRepository;
    private BookingService bookingService;
    private ParallelBookingProcessor processor;
    private User customer;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        workspaceRepository = new InMemoryWorkspaceRepository();
        bookingRepository = new InMemoryBookingRepository();
        bookingService = new BookingServiceImpl(bookingRepository, userRepository,
                workspaceRepository, new ConcurrentBookingQueue());
        processor = new ParallelBookingProcessor(bookingService);
        customer = TestFixtures.customer(userRepository.nextId(), "customer@mail.ru");
        userRepository.save(customer);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void processesQueueInSeveralNamedThreads() {
        createPendingBookings(6);

        ProcessingReport report = processor.processPendingBookings(3);

        assertThat(report.getProcessedBookings()).hasSize(6);
        assertThat(report.getSuccessfulCount()).isEqualTo(6);
        assertThat(report.getFailedCount()).isZero();
        assertThat(report.getThreadNames()).hasSize(3);
        assertThat(report.getThreadNames())
                .allMatch(name -> name.startsWith("booking-worker-"));
        assertThat(report.isParallel()).isTrue();
        assertThat(bookingService.getPendingBookings()).isEmpty();
        assertThat(bookingService.findAll())
                .allMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED);
    }

    @Test
    void usesOneWorkerWhenOnlyOneBookingExists() {
        createPendingBookings(1);

        ProcessingReport report = processor.processPendingBookings(3);

        assertThat(report.getProcessedBookings()).hasSize(1);
        assertThat(report.getThreadNames()).hasSize(1);
        assertThat(report.isParallel()).isFalse();
    }

    @Test
    void returnsEmptyReportForEmptyQueue() {
        ProcessingReport report = processor.processPendingBookings(3);

        assertThat(report.getProcessedBookings()).isEmpty();
        assertThat(report.getThreadNames()).isEmpty();
        assertThat(report.getSuccessfulCount()).isZero();
    }

    @Test
    void rejectsNonPositiveThreadCount() {
        assertThatThrownBy(() -> processor.processPendingBookings(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void createPendingBookings(int count) {
        LocalDateTime start = TestFixtures.futureStart(2, 10);
        for (int index = 1; index <= count; index++) {
            Workspace workspace = TestFixtures.meetingRoom(workspaceRepository.nextId());
            workspaceRepository.save(workspace);
            Booking booking = bookingService.createBooking(customer.getId(),
                    workspace.getId(), start, start.plusHours(2));
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        }
    }
}
