package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.SeatHold;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SeatHoldServiceTest {

    private SeatHoldService seatHoldService;

    @BeforeEach
    void setUp() {
        seatHoldService = new SeatHoldService();
    }

    @Test
    @DisplayName("Successfully hold seats for a user")
    void testHoldSeatsSuccess() {
        Long showId = 1L;
        List<Long> seatIds = List.of(10L, 11L);
        Long userId = 100L;

        SeatHold hold = seatHoldService.holdSeats(showId, seatIds, userId);

        assertNotNull(hold);
        assertNotNull(hold.getHoldId());
        assertEquals(showId, hold.getShowId());
        assertEquals(seatIds, hold.getSeatIds());
        assertEquals(userId, hold.getUserId());
        assertFalse(hold.isExpired());
    }

    @Test
    @DisplayName("Prevent another user from holding already held active seats")
    void testHoldSeatsConflict() {
        Long showId = 1L;
        List<Long> seatIds = List.of(10L, 11L);
        Long user1 = 100L;
        Long user2 = 200L;

        // User 1 holds seats 10, 11
        seatHoldService.holdSeats(showId, seatIds, user1);

        // User 2 tries to hold seat 10
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                seatHoldService.holdSeats(showId, List.of(10L), user2)
        );

        assertTrue(exception.getMessage().contains("currently on hold by another user"));
    }

    @Test
    @DisplayName("Allow same user to update or re-hold their own seats")
    void testReHoldSeatsSameUser() {
        Long showId = 1L;
        List<Long> seatIds = List.of(10L);
        Long userId = 100L;

        seatHoldService.holdSeats(showId, seatIds, userId);

        assertDoesNotThrow(() -> seatHoldService.holdSeats(showId, seatIds, userId));
    }

    @Test
    @DisplayName("Verify getHeldSeatIdsForShow returns active held seats")
    void testGetHeldSeatIdsForShow() {
        Long showId = 1L;
        seatHoldService.holdSeats(showId, List.of(10L, 11L), 100L);

        Set<Long> heldSeats = seatHoldService.getHeldSeatIdsForShow(showId);

        assertEquals(2, heldSeats.size());
        assertTrue(heldSeats.contains(10L));
        assertTrue(heldSeats.contains(11L));
    }

    @Test
    @DisplayName("Release hold removes seats from active holds")
    void testReleaseHold() {
        Long showId = 1L;
        List<Long> seatIds = List.of(10L, 11L);
        seatHoldService.holdSeats(showId, seatIds, 100L);

        seatHoldService.releaseHold(showId, seatIds);

        Set<Long> heldSeats = seatHoldService.getHeldSeatIdsForShow(showId);
        assertTrue(heldSeats.isEmpty());
    }
}
