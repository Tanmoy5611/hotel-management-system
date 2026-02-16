package be.kdg.prog5.hotels.viewmodel;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

// HotelForm is a ViewModel used only for form
public class HotelForm {

    // Validated form fields (Jakarta Validation)
    // Hotel name cannot be empty (validation message in messages.properties)
    @NotBlank(message = "{hotel.name.required}")
    private String name;

    @NotBlank(message = "{hotel.city.required}")
    private String city;

    @NotBlank(message = "{hotel.country.required}")
    private String country;

    // Opening date must not be null
    @NotNull(message = "{hotel.opened.required}")
    private LocalDate openedOn;

    @NotNull
    @Min(value = 1, message = "{hotel.stars.min}")
    @Max(value = 5, message = "{hotel.stars.max}")
    private int stars;

    // Boolean checkbox: no validation needed
    private boolean hasSpa;

    // Image URL must not be empty
    @NotBlank(message = "{hotel.image.required}")
    private String imageUrl;

    @Size(max = 4000)
    private String description;

    // Getters and Setters to allow Thymeleaf to bind form fields to this object
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}