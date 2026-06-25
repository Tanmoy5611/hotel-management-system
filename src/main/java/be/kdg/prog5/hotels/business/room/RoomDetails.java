package be.kdg.prog5.hotels.business.room;

import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.Stay;

import java.time.LocalDate;
import java.util.List;

// Data prepared for the room detail page
// Keeps customer visibility rules outside the controller
public record RoomDetails(Room room,
                          // Already filtered based on the logged in user
                          List<Stay> stays,
                          // True when the current user is a customer
                          boolean customer,
                          // Controls if the Room Bookings section is shown
                          boolean showRoomBookings,
                          // Used by the view to mark past and upcoming stays
                          LocalDate today) {
}