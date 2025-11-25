package be.kdg.prog3.hotels.viewmodel;

import be.kdg.prog3.hotels.domain.RoomType;
import jakarta.validation.constraints.*;

/**
 * Simple ViewModel for Room form (used in add-room.html).
 * This class only handles form data and validation,
 */
public class RoomForm {

    @Min(value = 1, message = "{room.number.min}")
    private int number;

    @NotNull(message = "{room.type.required}")
    private RoomType type;

    @Positive(message = "{room.price.positive}")
    private double pricePerNight;

    private boolean seaView;

    @NotBlank(message = "{room.photo.required}")
    private String photoUrl;

    @NotBlank
    private String hotelId;

    // Getters and setters
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isSeaView() {
        return seaView;
    }

    public void setSeaView(boolean seaView) {
        this.seaView = seaView;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getHotelId() { return hotelId; }

    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
}