package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.domain.Hotel;

import java.time.LocalDate;
import java.util.List;

// Service interface for all business logic related to Hotels.
// (The controller will call these methods instead of talking to the repository directly.)

public interface HotelService {

    // Return all hotels (used in /hotels overview page)
    List<Hotel> getAllHotels();

    // Filter hotels based on minimum stars and optional opening date
    List<Hotel> getHotelsByMinStarsAndOpenedAfter(int minStars, LocalDate openedAfter);

    // Create and persist a new hotel
    Hotel createHotel(Hotel hotel);

    boolean existsByHotelId(String hotelId);

    // Find a single hotel by BUSINESS identifier (hotelId, used in URLs)
    Hotel getHotelByHotelId(String hotelId);

    // Delete a hotel by BUSINESS identifier
    void deleteHotelByHotelId(String hotelId);

    // Search hotels by partial name (case-insensitive)
    List<Hotel> searchByName(String text);

    // Update only the description of a hotel
    void updateHotelDescription(String hotelId, String description);

    /// Home page
    List<Hotel> getFeaturedHotels();
    List<Hotel> getBeachSpaHotels();
    List<Hotel> getCityHotels(LocalDate openedAfter);

}