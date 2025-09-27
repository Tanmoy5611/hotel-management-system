package be.kdg.prog3.hotels.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Attributes of Hotel class
public class Hotel {
    private final String name;
    private final LocalDate openedOn;
    private final int stars;
    private final boolean hasSpa;
    private final String imageUrl;

    // Create a list - Each hotel has many rooms
    private final List<Room> rooms = new ArrayList<>();

    // Constructor
    public Hotel(String name, LocalDate openedOn, int stars, boolean hasSpa, String imageUrl) {
        this.name = name;
        this.openedOn = openedOn;
        this.stars = stars;
        this.hasSpa = hasSpa;
        this.imageUrl = imageUrl;
    }


    // Getters to access attributes
    public String getName() {
        return name;
    }

    public LocalDate getOpenedOn() {
        return openedOn;
    }

    public int getStars() {
        return stars;
    }

    public boolean isHasSpa() {
        return hasSpa;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    // Method to add a room to the hotel
    public void addRoom(Room room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
        }
        if (room.getHotel() != this) {
            room.setHotel(this);          // Bidirectional relationship
        }
    }

    // Override toString method to print hotel details
    @Override
    public String toString() {
        return name + " [" + stars + "★, spa=" + hasSpa + ", opened=" + openedOn + "]";

    }

}
