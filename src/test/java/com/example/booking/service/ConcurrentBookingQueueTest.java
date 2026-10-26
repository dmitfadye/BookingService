package com.example.booking.service;

import com.example.booking.TestFixtures;
import com.example.booking.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentBookingQueueTest {

    private ConcurrentBookingQueue queue;
    private Booking first;
    private Booking second;

    @BeforeEach
    void setUp() {
        queue = new ConcurrentBookingQueue();
        first = TestFixtures.booking(1L,
                TestFixtures.customer(1L, "first@mail.ru"),
                TestFixtures.meetingRoom(1L), TestFixtures.futureStart(2, 10));
        second = TestFixtures.booking(2L,
                TestFixtures.customer(2L, "second@mail.ru"),
                TestFixtures.desk(2L), TestFixtures.futureStart(2, 12));
    }

    @Test
    void addsAndReturnsSnapshot() {
        queue.add(first);
        queue.add(second);

        List<Booking> snapshot = queue.snapshot();

        assertThat(snapshot).containsExactly(first, second);
        assertThat(queue.size()).isEqualTo(2);
    }

    @Test
    void removesBookingById() {
        queue.add(first);
        queue.add(second);

        boolean removed = queue.remove(first.getId());

        assertThat(removed).isTrue();
        assertThat(queue.snapshot()).containsExactly(second);
        assertThat(queue.size()).isEqualTo(1);
    }

    @Test
    void returnsFalseWhenBookingIsNotFound() {
        queue.add(first);

        assertThat(queue.remove(999L)).isFalse();
    }

    @Test
    void clearsQueue() {
        queue.add(first);
        queue.add(second);

        queue.clear();

        assertThat(queue.size()).isZero();
        assertThat(queue.snapshot()).isEmpty();
    }
}
