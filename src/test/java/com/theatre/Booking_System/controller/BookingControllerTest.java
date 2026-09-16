package com.theatre.Booking_System.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theatre.Booking_System.dto.BookingRequestDTO;
import com.theatre.Booking_System.dto.BookingResponseDTO;
import com.theatre.Booking_System.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @Test
    @DisplayName("Reject unauthenticated request to /api/bookings with 403 Forbidden")
    void testCreateBookingUnauthenticated() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO(1L, 1L, List.of(10L), null);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    @DisplayName("Create booking successfully when authenticated with 201 Created status")
    void testCreateBookingAuthenticated() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO(1L, 1L, List.of(10L), "FIRST50");

        BookingResponseDTO mockResponse = BookingResponseDTO.builder()
                .bookingId(100L)
                .movieTitle("Interstellar")
                .theatreName("PVR IMAX")
                .showTime(LocalDateTime.now().plusDays(1))
                .seatNumbers(List.of("A1"))
                .baseAmount(BigDecimal.valueOf(300))
                .surgeAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.valueOf(100))
                .totalAmount(BigDecimal.valueOf(200))
                .promoCode("FIRST50")
                .status("PENDING_PAYMENT")
                .build();

        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(100L))
                .andExpect(jsonPath("$.movieTitle").value("Interstellar"))
                .andExpect(jsonPath("$.theatreName").value("PVR IMAX"))
                .andExpect(jsonPath("$.totalAmount").value(200))
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }
}
