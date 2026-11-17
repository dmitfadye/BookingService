package com.example.booking.concurrency;

/**
 * Обработчик очереди бронирований.
 */
public interface BookingProcessor {

    /**
     * Подтверждает все бронирования из очереди в нескольких потоках.
     *
     * @param threadCount количество потоков
     * @return отчёт об обработке
     */
    ProcessingReport processPendingBookings(int threadCount);
}
