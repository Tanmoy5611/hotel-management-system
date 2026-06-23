package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Combines the guest and booking rows needed by the MVC detail page
public record GuestDetails(Guest guest, List<StayDetails> stays) {

    // Keeps calculated stay values together for one table row
    public record StayDetails(Room room,
                              LocalDate checkIn,
                              LocalDate checkOut,
                              long nights,
                              BigDecimal discount,
                              BigDecimal totalPrice,
                              BigDecimal finalPrice) {
    }
}