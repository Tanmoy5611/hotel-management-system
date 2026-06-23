package be.kdg.prog5.hotels.webapi.dto;

import java.time.LocalDate;

// Response data returned by the Hotel API without rooms or JPA details
public class HotelDto {

    private String hotelId;
    private String name;
    private String city;
    private String country;
    private LocalDate openedOn;
    private int stars;
    private boolean hasSpa;
    private String imageUrl;
    private String description;

    public HotelDto() {
    }

    public HotelDto(String hotelId, String name, String city, String country, LocalDate openedOn,
                    int stars, boolean hasSpa, String imageUrl, String description) {
        this.hotelId = hotelId;
        this.name = name;
        this.city = city;
        this.country = country;
        this.openedOn = openedOn;
        this.stars = stars;
        this.hasSpa = hasSpa;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

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