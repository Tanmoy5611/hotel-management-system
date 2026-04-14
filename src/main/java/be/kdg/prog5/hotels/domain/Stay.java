package be.kdg.prog5.hotels.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Entity
@Table(
        name = "stays",
        // Improves performance
        indexes = {
                @Index(name = "idx_stay_room", columnList = "room_id"),
                @Index(name = "idx_stay_guest", columnList = "guest_id")
        }
)

public class Stay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stay belongs to Room aggregate
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;


    // Guest participates but does NOT own lifecycle
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    //  booking period
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;


    protected Stay() {}

    // Constructor - create a Stay link entity that connects one Room with one Guest
    // Constructor used only from aggregate root (Room) - Only Room aggregate should create Stay
   public Stay(Room room, Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {

        if (room == null || guest == null) {
           throw new IllegalArgumentException("Room and Guest cannot be null");
       }

       if (checkInDate == null || checkOutDate == null) {
           throw new IllegalArgumentException("Dates cannot be null");
       }

       if (checkOutDate.isBefore(checkInDate)) {
           throw new IllegalArgumentException("Check-out must be after check-in");
       }

        this.room = room;
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    // Getters
    public Long getId() {
        return id;
    }
    public Room getRoom() { return room; }
    public Guest getGuest() { return guest; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }


    // Package-private setters
    // (prevent external misuse)
    void setRoom(Room room) {
        this.room = room;
    }
    void setGuest(Guest guest) {
        this.guest = guest;
    }

    // Stay duration calculation for guest
    // because Duration belongs to Stay
    public long getNumberOfNights() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return Math.max(nights, 1);
    }

    // calculate total price based on stay duration and room price
    public BigDecimal getTotalPrice() {
        return room.getPricePerNight()
                .multiply(BigDecimal.valueOf(getNumberOfNights()));
    }

    // Calculates discounted price based on guest discount
    public BigDecimal getFinalPrice() {

        BigDecimal total = getTotalPrice();

        // guard against null values to ensure pricing logic is stable
        BigDecimal discountPercent =
                Optional.ofNullable(guest.getDiscountPercentage())
                        .orElse(BigDecimal.ZERO);

        BigDecimal discountAmount =
                total.multiply(discountPercent)
                        .divide(BigDecimal.valueOf(100));

        BigDecimal finalPrice =
                total.subtract(discountAmount);

        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
}