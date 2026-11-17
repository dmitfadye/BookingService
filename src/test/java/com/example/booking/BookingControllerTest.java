package com.example.booking;

import com.example.booking.dto.BookingResponse;
import com.example.booking.model.BookingStatus;
import com.example.booking.model.WorkspaceType;
import com.example.booking.repository.BookingJpaRepository;
import com.example.booking.repository.UserJpaRepository;
import com.example.booking.repository.WorkspaceJpaRepository;
import com.example.booking.service.BookingApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверяет REST API бронирований через MockMvc без обращения к базе данных.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingApiService bookingApiService;

    @MockitoBean
    private UserJpaRepository userJpaRepository;

    @MockitoBean
    private WorkspaceJpaRepository workspaceJpaRepository;

    @MockitoBean
    private BookingJpaRepository bookingJpaRepository;

    @Test
    void shouldReturnBookingsFilteredByStatusWithoutDatabase() throws Exception {
        BookingResponse response = new BookingResponse(
                1L,
                1L,
                "customer@mail.ru",
                1L,
                "Зал «Альфа»",
                WorkspaceType.MEETING_ROOM,
                LocalDateTime.of(2026, 2, 10, 10, 0),
                LocalDateTime.of(2026, 2, 10, 12, 0),
                LocalDateTime.of(2026, 1, 15, 12, 0),
                new BigDecimal("2000.00"),
                BookingStatus.PENDING
        );
        when(bookingApiService.findAll(
                BookingStatus.PENDING, null, null, null, null))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/bookings").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].customerEmail").value("customer@mail.ru"));
    }
}
