package com.example.booking.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Итоговый отчёт многопоточной обработки бронирований.
 */
public class ProcessingReport {

    private final List<ProcessedBooking> processedBookings;
    private final Set<String> threadNames;
    private final long durationMillis;

    /**
     * Создаёт отчёт.
     *
     * @param processedBookings результаты обработки
     * @param threadNames       имена использованных потоков
     * @param durationMillis    длительность обработки в миллисекундах
     */
    public ProcessingReport(List<ProcessedBooking> processedBookings,
                            Set<String> threadNames, long durationMillis) {
        this.processedBookings = Collections.unmodifiableList(
                new ArrayList<>(processedBookings));
        this.threadNames = Collections.unmodifiableSet(new TreeSet<>(threadNames));
        this.durationMillis = durationMillis;
    }

    public List<ProcessedBooking> getProcessedBookings() {
        return processedBookings;
    }

    public Set<String> getThreadNames() {
        return threadNames;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * Возвращает количество успешно обработанных бронирований.
     *
     * @return количество
     */
    public int getSuccessfulCount() {
        int count = 0;
        for (ProcessedBooking booking : processedBookings) {
            if (booking.isSuccessful()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Возвращает количество ошибок.
     *
     * @return количество
     */
    public int getFailedCount() {
        return processedBookings.size() - getSuccessfulCount();
    }

    /**
     * Проверяет, использовалось ли больше одного потока.
     *
     * @return {@code true}, если обработка действительно многопоточная
     */
    public boolean isParallel() {
        return threadNames.size() > 1;
    }
}
