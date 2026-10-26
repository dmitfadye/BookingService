package com.example.booking.concurrency;

import java.util.Objects;

/**
 * Результат обработки одного бронирования в отдельном потоке.
 */
public class ProcessedBooking {

    private final Long bookingId;
    private final String workspaceName;
    private final String threadName;
    private final boolean successful;
    private final String message;

    /**
     * Создаёт результат обработки.
     *
     * @param bookingId     идентификатор бронирования
     * @param workspaceName название пространства
     * @param threadName    имя потока
     * @param successful    признак успеха
     * @param message       сообщение
     */
    public ProcessedBooking(Long bookingId, String workspaceName, String threadName,
                            boolean successful, String message) {
        this.bookingId = Objects.requireNonNull(bookingId, "Идентификатор не может быть null");
        this.workspaceName = Objects.requireNonNull(workspaceName, "Название не может быть null");
        this.threadName = Objects.requireNonNull(threadName, "Имя потока не может быть null");
        this.successful = successful;
        this.message = Objects.requireNonNull(message, "Сообщение не может быть null");
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public String getThreadName() {
        return threadName;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ProcessedBooking{bookingId=" + bookingId
                + ", workspaceName='" + workspaceName + '\''
                + ", threadName='" + threadName + '\''
                + ", successful=" + successful
                + ", message='" + message + '\''
                + '}';
    }
}
