package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.data.springdata.SpringDataHotelRepository;
import be.kdg.prog3.hotels.domain.Hotel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Profile("springdata")   // Activate this profile to use Spring Data JPA version
public class SpringDataHotelServiceImpl implements HotelService {

    // Spring Data JPA repository (JpaRepository<Hotel, String>) which gives CRUD methods automatically
    private final SpringDataHotelRepository repo;

    // Constructor injection: Spring gives the correct JPA repository
    public SpringDataHotelServiceImpl(SpringDataHotelRepository repo) {
        this.repo = repo;
    }

    // old interface methods
    @Override
    public List<Hotel> getAllHotels() {
        // Simply return all hotels from the database
        return repo.findAll();
    }

    @Override
    public List<Hotel> getHotelsByMinStarsAndDate(int minStars, String dateIn) {
        // console app originally ignored date
        // using a custom JPA method query instead of Java streams
        return repo.findHotelsWithMinStars(minStars);
    }


    @Override
    public Hotel createHotel(Hotel hotel) {

        if (hotel.getId() == null || hotel.getId().isBlank()) {
            hotel.setId(
                    hotel.getName()
                            .toLowerCase()
                            .replaceAll("[^a-z0-9]+", "-")
            );
        }

        return repo.save(hotel);
    }

    @Override
    public Hotel getHotelById(String id) {
        // repo.findById() returns Optional, it returns null if not found
        return repo.findById(id).orElse(null);
    }

    @Override
    public void deleteHotel(String id) {
        // JPA handles cascade rules and deletes the hotel (built-in)
        repo.deleteById(id);
    }

    // Search hotels where the name contains given text (case-insensitive)
    public List<Hotel> searchByName(String text) {
        return repo.findByNameContainingIgnoreCase(text);
    }

    // Filter hotels where stars >= minStars
    public List<Hotel> filterByStars(int minStars) {
        return repo.findHotelsWithMinStars(minStars);
    }

    //  Get hotels opened after a certain date
    public List<Hotel> getHotelsOpenedAfter(LocalDate date) {
        return repo.findByOpenedOnAfter(date);
    }

    // Get hotels opened before a certain date
    public List<Hotel> getHotelsOpenedBefore(LocalDate date) {
        return repo.findByOpenedOnBefore(date);
    }

    // Hotels with or without spa
    public List<Hotel> getHotelsWithSpa(boolean hasSpa) {
        return repo.findByHasSpa(hasSpa);
    }

    @Override
    @Transactional
    public void updateHotelDescription(String id, String description) {

        Hotel hotel = repo.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Hotel not found"));

        hotel.setDescription(description);
    }
}