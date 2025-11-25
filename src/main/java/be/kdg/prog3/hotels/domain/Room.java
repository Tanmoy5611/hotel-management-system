package be.kdg.prog3.hotels.domain;

import java.util.HashSet;
import java.util.Set;

// Attributes of Room class
public class Room {
    private int number;
    private RoomType type;
    private double pricePerNight;
    private boolean seaView;
    private String photoUrl;

    private Hotel hotel;  /// many-to-one

    private final Set<Guest> guests = new HashSet<>();

    public Set<Guest> getGuests() {
        return guests;
    }

    public void addGuest(Guest g) {
        guests.add(g);
    }

    // Default constructor for Spring and Thymeleaf forms to create objects
    public Room() {

    }


    // Constructor
    public Room(int number, RoomType type, double pricePerNight, boolean seaView, String photoUrl) {
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.seaView = seaView;
        this.photoUrl = photoUrl;

    }

    // getters to access attributes
    public int getNumber() {
        return number;
    }

    public RoomType getType() {
        return type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isSeaView() {
        return seaView;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Hotel getHotel() {
        return hotel;

    }

    // Setters

    public void setNumber(int number) {
        this.number = number;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setSeaView(boolean seaView) {

        this.seaView = seaView;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;

    }


    // method to set the hotel of the room
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;

        if  (hotel != null && !hotel.getRooms().contains(this)) {
            hotel.addRoom(this);
        }
    }


    // Override toString method to print
    @Override
    public String toString() {
        String hotelName = (hotel != null ? hotel.getName() : "no-hotel");
        return "#" + number + " " + type + " " + (seaView ? "(sea)" : "") +
                " €" + pricePerNight + " @ " + hotelName;

    }
}

