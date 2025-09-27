package be.kdg.prog3.hotels.domain;

// Attributes of Room class
public class Room {
    private final int number;
    private final RoomType type;
    private final double pricePerNight;
    private final boolean seaView;
    private final String photoUrl;

    private Hotel hotel;  /// many-to-one

    // Constructor
    public Room(int numbers, RoomType type, double pricePerNight, boolean seaView, String photoUrl) {
        this.number = numbers;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.seaView = seaView;
        this.photoUrl = photoUrl;
    }

    // getters to access attributes
    public int getNumbers() {
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

    // method to set the hotel of the room
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;

        if  (hotel != null & !hotel.getRooms().contains(this)) {
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

