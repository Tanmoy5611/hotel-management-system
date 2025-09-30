package be.kdg.prog3.hotels.domain;

// Attributes of Room class
public class Room {
    private int number;
    private RoomType type;
    private double pricePerNight;
    private boolean seaView;
    private String photoUrl;

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

