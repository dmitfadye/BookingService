package com.example.booking;

import com.example.booking.repository.BookingJpaRepository;
import com.example.booking.repository.UserJpaRepository;
import com.example.booking.repository.WorkspaceJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Проверяет подъём Spring-контекста без подключения к PostgreSQL.
 */
@SpringBootTest
@ActiveProfiles("test")
class BookingServiceApplicationTests {

    @MockitoBean
    private UserJpaRepository userJpaRepository;

    @MockitoBean
    private WorkspaceJpaRepository workspaceJpaRepository;

    @MockitoBean
    private BookingJpaRepository bookingJpaRepository;

    @Test
    void contextLoadsWithoutDatabaseConnection() {
    }
}
