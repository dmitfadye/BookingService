package com.example.booking.concurrency;

import com.example.booking.model.Booking;
import com.example.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Многопоточный обработчик очереди бронирований.
 *
 * <p>Список ожидающих бронирований разбивается на части — по одной на поток.
 * Все потоки одновременно стартуют через {@link CountDownLatch}, что позволяет
 * наглядно показать параллельную работу в логе. Каждый поток имеет имя
 * {@code booking-worker-N}.</p>
 */
@Service
public class ParallelBookingProcessor implements BookingProcessor {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ParallelBookingProcessor.class);

    private final BookingService bookingService;

    /**
     * Создаёт обработчик.
     *
     * @param bookingService сервис бронирований
     */
    public ParallelBookingProcessor(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public ProcessingReport processPendingBookings(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Количество потоков должно быть больше нуля");
        }
        List<Booking> bookings = bookingService.getPendingBookings();
        if (bookings.isEmpty()) {
            LOGGER.info("Очередь пуста: обрабатывать нечего");
            return new ProcessingReport(Collections.emptyList(),
                    Collections.emptySet(), 0L);
        }

        int workers = Math.min(threadCount, bookings.size());
        int chunkSize = (bookings.size() + workers - 1) / workers;
        ExecutorService executor = Executors.newFixedThreadPool(
                workers, new NamedThreadFactory("booking-worker"));
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<List<ProcessedBooking>>> futures = new ArrayList<>();
        long startNanos = System.nanoTime();

        try {
            for (int fromIndex = 0; fromIndex < bookings.size(); fromIndex += chunkSize) {
                int toIndex = Math.min(fromIndex + chunkSize, bookings.size());
                List<Booking> chunk = new ArrayList<>(
                        bookings.subList(fromIndex, toIndex));
                Future<List<ProcessedBooking>> future = executor.submit(() -> {
                    startGate.await();
                    return processChunk(chunk);
                });
                futures.add(future);
            }
            startGate.countDown();

            List<ProcessedBooking> processed = new ArrayList<>();
            Set<String> threadNames = new TreeSet<>();
            for (Future<List<ProcessedBooking>> future : futures) {
                List<ProcessedBooking> chunkResult = future.get();
                processed.addAll(chunkResult);
                for (ProcessedBooking item : chunkResult) {
                    threadNames.add(item.getThreadName());
                }
            }
            long durationMillis = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - startNanos);
            ProcessingReport report = new ProcessingReport(
                    processed, threadNames, durationMillis);
            LOGGER.info("Обработка завершена: успешно {}, ошибок {}, потоки {}, время {} мс",
                    report.getSuccessfulCount(), report.getFailedCount(),
                    report.getThreadNames(), report.getDurationMillis());
            return report;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Ожидание обработки было прервано", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Ошибка при многопоточной обработке",
                    exception.getCause());
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException exception) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private List<ProcessedBooking> processChunk(List<Booking> chunk) {
        List<ProcessedBooking> result = new ArrayList<>();
        String threadName = Thread.currentThread().getName();
        for (Booking booking : chunk) {
            LOGGER.info("[{}] Взят в обработку заказ #{} ({})",
                    threadName, booking.getId(), booking.getWorkspace().getName());
            try {
                bookingService.confirmBooking(booking.getId());
                result.add(new ProcessedBooking(booking.getId(),
                        booking.getWorkspace().getName(), threadName,
                        true, "Бронирование подтверждено"));
                LOGGER.info("[{}] Заказ #{} успешно подтверждён",
                        threadName, booking.getId());
            } catch (RuntimeException exception) {
                result.add(new ProcessedBooking(booking.getId(),
                        booking.getWorkspace().getName(), threadName,
                        false, exception.getMessage()));
                LOGGER.error("[{}] Ошибка обработки заказа #{}: {}",
                        threadName, booking.getId(), exception.getMessage());
            }
        }
        return result;
    }
}
