package com.theatre.Booking_System.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    private LocalDateTime bookingTime;

    private LocalDateTime expiresAt;

    private BigDecimal baseAmount;

    private BigDecimal surgeAmount;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private String promoCode;

    private String paymentId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<BookingSeat> bookingSeats;

    public enum BookingStatus {
        PENDING_PAYMENT, CONFIRMED, CANCELLED, EXPIRED
    }
}