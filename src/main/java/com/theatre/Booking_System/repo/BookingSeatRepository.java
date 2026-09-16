package com.theatre.Booking_System.repo;

import com.theatre.Booking_System.model.Booking.BookingStatus;
import com.theatre.Booking_System.model.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("SELECT bs.seat.id FROM BookingSeat bs " +
            "WHERE bs.booking.show.id = :showId " +
            "AND bs.booking.status = :status")
    Set<Long> findBookedSeatIdsByShowAndStatus(
            @Param("showId") Long showId,
            @Param("status") BookingStatus status
    );

    @Query("SELECT COUNT(bs) > 0 FROM BookingSeat bs " +
            "WHERE bs.seat.id = :seatId " +
            "AND bs.booking.show.id = :showId " +
            "AND bs.booking.status = :status")
    boolean isSeatAlreadyBooked(
            @Param("seatId") Long seatId,
            @Param("showId") Long showId,
            @Param("status") BookingStatus status
    );
}