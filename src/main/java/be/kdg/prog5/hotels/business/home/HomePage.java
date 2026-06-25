package be.kdg.prog5.hotels.business.home;

import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;

import java.util.List;

// Data for the home page sections
// The controller only passes this record to the view
public record HomePage(List<Hotel> featuredHotels,
                       List<Hotel> beachSpaHotels,
                       List<Hotel> cityHotels,
                       List<Room> bestValueRooms,
                       List<Room> premiumRooms,
                       List<Room> topPickedRooms) {
}