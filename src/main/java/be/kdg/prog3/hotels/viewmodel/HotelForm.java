package be.kdg.prog3.hotels.viewmodel;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

// HotelForm is a ViewModel used only for form
public class HotelForm {

    // Validated form fields

    // Hotel name cannot be empty (validation message in messages.properties)
    @NotBlank(message = "{hotel.name.required}")
    private String name;

    // Opening date must not be null
    @NotNull(message = "{hotel.opened.required}")
    private LocalDate openedOn;

    @Min(value = 1, message = "{hotel.stars.min}")
    @Max(value = 5, message = "{hotel.stars.max}")
    private int stars;

    // Boolean checkbox: no validation needed
    private boolean hasSpa;

    // Image URL must not be empty
    @NotBlank(message = "{hotel.image.required}")
    private String imageUrl;

    // Getters and Setters to allow Thymeleaf to bind form fields to this object
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


