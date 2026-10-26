package com.example.booking.concurrency;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Фабрика потоков с понятными именами вида {@code booking-worker-1}.
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String prefix;
    private final AtomicInteger counter = new AtomicInteger(1);

    /**
     * Создаёт фабрику.
     *
     * @param prefix префикс имени потока
     */
    public NamedThreadFactory(String prefix) {
        this.prefix = Objects.requireNonNull(prefix, "Префикс не может быть null");
        if (prefix.isBlank()) {
            throw new IllegalArgumentException("Префикс не может быть пустым");
        }
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, prefix + "-" + counter.getAndIncrement());
        thread.setDaemon(false);
        return thread;
    }
}
