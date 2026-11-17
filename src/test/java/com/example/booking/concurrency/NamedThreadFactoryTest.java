package com.example.booking.concurrency;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NamedThreadFactoryTest {

    @Test
    void createsThreadsWithSequentialNames() {
        NamedThreadFactory factory = new NamedThreadFactory("worker");

        Thread first = factory.newThread(() -> {
        });
        Thread second = factory.newThread(() -> {
        });

        assertThat(first.getName()).isEqualTo("worker-1");
        assertThat(second.getName()).isEqualTo("worker-2");
        assertThat(first.isDaemon()).isFalse();
    }
}
