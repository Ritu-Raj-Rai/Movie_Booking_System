package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.SeatHold;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SeatHoldService {

    public static final int HOLD_DURATION_MINUTES = 5;

    // Key: "showId:seatId", Value: SeatHold
    private final Map<String, SeatHold> activeSeatHolds = new ConcurrentHashMap<>();

    public synchronized SeatHold holdSeats(Long showId, List<Long> seatIds, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Verify if any requested seat is held by another active user
        for (Long seatId : seatIds) {
            String key = buildKey(showId, seatId);
            SeatHold existingHold = activeSeatHolds.get(key);
            if (existingHold != null) {
                if (!existingHold.isExpired() && !existingHold.getUserId().equals(userId)) {
                    throw new RuntimeException("Seat ID " + seatId + " is currently on hold by another user");
                }
            }
        }

        // 2. Create seat hold
        String holdId = UUID.randomUUID().toString();
        LocalDateTime expiresAt = now.plusMinutes(HOLD_DURATION_MINUTES);

        SeatHold hold = SeatHold.builder()
                .holdId(holdId)
                .showId(showId)
                .seatIds(seatIds)
                .userId(userId)
                .heldAt(now)
                .expiresAt(expiresAt)
                .build();

        for (Long seatId : seatIds) {
            activeSeatHolds.put(buildKey(showId, seatId), hold);
        }

        return hold;
    }

    public boolean isSeatHeldByAnotherUser(Long showId, Long seatId, Long currentUserId) {
        String key = buildKey(showId, seatId);
        SeatHold hold = activeSeatHolds.get(key);
        if (hold == null || hold.isExpired()) {
            return false;
        }
        return !hold.getUserId().equals(currentUserId);
    }

    public Set<Long> getHeldSeatIdsForShow(Long showId) {
        Set<Long> heldSeatIds = new HashSet<>();
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<String, SeatHold> entry : activeSeatHolds.entrySet()) {
            SeatHold hold = entry.getValue();
            if (hold.getShowId().equals(showId) && !hold.isExpired()) {
                heldSeatIds.addAll(hold.getSeatIds());
            }
        }
        return heldSeatIds;
    }

    public void releaseHold(Long showId, List<Long> seatIds) {
        for (Long seatId : seatIds) {
            activeSeatHolds.remove(buildKey(showId, seatId));
        }
    }

    public void cleanExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        activeSeatHolds.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private String buildKey(Long showId, Long seatId) {
        return showId + ":" + seatId;
    }
}
