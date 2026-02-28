package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.domain.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private static final Logger log =
            LoggerFactory.getLogger(HotelServiceImpl.class);

    private final SpringDataHotelRepository hotelRepo;

    public HotelServiceImpl(SpringDataHotelRepository hotelRepo) {
        this.hotelRepo = hotelRepo;
    }

    /// Read Hotels
    @Override
    @Transactional(readOnly = true)
    public List<Hotel> getAllHotels() {
        log.debug("Getting all hotels");

        return hotelRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> getHotelsByMinStarsAndOpenedAfter(int minStars, LocalDate openedAfter) {
        log.debug("Getting hotels with stars >= {} and opened after {}", minStars, openedAfter);

        List<Hotel> hotels = hotelRepo.findByStarsGreaterThanEqual(minStars);

        // Optional second filter in Java
        if (openedAfter != null) {
            hotels = hotels.stream()
                    .filter(h -> h.getOpenedOn() != null &&
                            h.getOpenedOn().isAfter(openedAfter))
                    .toList();
        }

        return hotels;
    }

    @Override
    @Transactional(readOnly = true)
    public Hotel getHotelByHotelId(String hotelId) {
        log.debug("Getting hotel with business id {}", hotelId);

        return hotelRepo.findByHotelIdWithAggregate(hotelId)

                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByHotelId(String hotelId) {
        log.debug("Checking if hotelId {} already exists", hotelId);
        return hotelRepo.existsByHotelId(hotelId);
    }


    /// Create hotel
    @Override
    public Hotel createHotel(Hotel hotel) {
        log.debug("Creating hotel {}", hotel);

        // Generates normalized hotel ID if missing
        if (hotel.getHotelId() == null || hotel.getHotelId().isBlank()) {

            String generateHotelId = (hotel.getName() + "-" +
                    hotel.getCity() + "-" +
                    hotel.getCountry())
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("(^-|-$)", "");

            String finalHotelId = generateHotelId;
            int counter = 1;

            // Ensure uniqueness by exist hotel checking
            while (hotelRepo.existsByHotelId(finalHotelId)) {
                finalHotelId = generateHotelId + "-" + counter;
                counter++;
            }

            hotel.setHotelId(finalHotelId);
        }

        return hotelRepo.save(hotel);
    }

    /// Delete hotel
    @Override
    public void deleteHotelByHotelId(String hotelId) {
        log.debug("Deleting hotel with business id {}", hotelId);

        Hotel hotel = hotelRepo.findByHotelId(hotelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Hotel not found"));

        //  No manual room deletion
        // Cascade handles it automatically
        hotelRepo.delete(hotel);
    }

    /// Search hotel by name
    @Override
    @Transactional(readOnly = true)
    public List<Hotel> searchByName(String text) {
        log.debug("Searching for hotels containing {}", text);

        return hotelRepo.findByNameContainingIgnoreCase(text);
    }

    /// Update hotel description
    @Override
    @Transactional
    public void updateHotelDescription(String hotelId, String description) {
        log.debug("Updating description for hotel {}", hotelId);

        Hotel hotel = hotelRepo.findByHotelId(hotelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Hotel not found"));

        hotel.setDescription(description);
        // JPA dirty checking persists automatically
    }

    /// Home Page
    @Override
    @Transactional(readOnly = true)
    public List<Hotel> getFeaturedHotels() {
        log.debug("Getting top 4 featured hotels by stars");

        return hotelRepo.findTop4ByOrderByStarsDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> getBeachSpaHotels() {
        log.debug("Getting top 4 hotels with spa");

        return hotelRepo.findTop4ByHasSpaTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hotel> getCityHotels(LocalDate openedAfter) {
        log.debug("Getting top 4 city hotels opened after {}", openedAfter);

        return hotelRepo.findTop4ByOpenedOnAfterOrderByOpenedOnDesc(openedAfter);
    }
}
