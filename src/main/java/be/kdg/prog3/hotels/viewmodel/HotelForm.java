package be.kdg.prog3.hotels.viewmodel;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class HotelForm {

    @NotBlank(message = "{hotel.name.required}")
    private String name;

    @NotNull(message = "{hotel.opened.required}")
    private LocalDate openedOn;

    @Min(value = 1, message = "{hotel.stars.min}")
    @Max(value = 5, message = "{hotel.stars.max}")
    private int stars;

    private boolean hasSpa;
    @NotBlank(message = "{hotel.image.required}")
    private String imageUrl;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getOpenedOn() {
        return openedOn;
    }

    public void setOpenedOn(LocalDate openedOn) {
        this.openedOn = openedOn;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public boolean isHasSpa() {
        return hasSpa;
    }

    public void setHasSpa(boolean hasSpa) {
        this.hasSpa = hasSpa;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}


