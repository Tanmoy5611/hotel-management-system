package be.kdg.prog5.hotels.business.room;

import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;

import java.time.LocalDate;
import java.util.List;

// Data for the search results page
// The controller only adds this object to the model
public record SearchResults(List<Room> rooms,
                            String query,
                            String roomType,
                            LocalDate checkIn,
                            LocalDate checkOut,
                            RoomType[] types,
                            boolean emptySearch) {
}