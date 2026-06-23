package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;

import java.util.List;
import java.util.Map;
import java.util.Set;

// Combines all data needed by the hotel detail page
public record HotelDetails(Hotel hotel,
                           Set<Room> rooms,
                           Map<Long, List<Guest>> guestsPerRoom,
                           int totalGuests) {
}