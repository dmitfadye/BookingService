package com.example.booking.repository;

import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA-репозиторий бронирований (лабораторная работа №2).
 */
public interface BookingJpaRepository extends JpaRepository<Booking, Long> {

    /**
     * Ищет бронирования по статусу.
     *
     * @param status статус
     * @return список бронирований
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Ищет бронирования клиента.
     *
     * @param customerId идентификатор клиента
     * @return список бронирований
     */
    List<Booking> findByCustomerId(Long customerId);

    /**
     * Ищет бронирования пространства.
     *
     * @param workspaceId идентификатор пространства
     * @return список бронирований
     */
    List<Booking> findByWorkspaceId(Long workspaceId);
}
