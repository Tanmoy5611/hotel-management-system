package be.kdg.prog5.hotels.business.booking;

import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Room;

import java.util.List;

// Data needed to render the booking form without extra logic in the controller
public record BookingFormDetails(Room room,
                                 // True when the logged in user is a customer
                                 boolean customer,
                                 // Customer profile is preselected for customer bookings
                                 Guest customerProfile,
                                 // Admin and staff can still choose from all guests
                                 List<Guest> allGuests) {
}