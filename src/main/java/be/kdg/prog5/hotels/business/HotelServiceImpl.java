package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.HotelNotFoundException;
import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private static final Logger log =
            LoggerFactory.getLogger(HotelServiceImpl.class);

    private final SpringDataHotelRepository hotelRepo;

    private final SafeActivityLogger safeActivityLogger;


    public HotelServiceImpl(SpringDataHotelRepository hotelRepo,
                            SafeActivityLogger safeActivityLogger) {
        this.hotelRepo = hotelRepo;
        this.safeActivityLogger = safeActivityLogger;
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

        if (openedAfter != null) {
            return hotelRepo.findByStarsGreaterThanEqualAndOpenedOnAfter(minStars, openedAfter);
        }
        return hotelRepo.findByStarsGreaterThanEqual(minStars);

    }

    @Override
    @Transactional(readOnly = true)
    public Hotel getHotelByHotelId(String hotelId) {
        log.debug("Getting hotel with business id {}", hotelId);

        return hotelRepo.findByHotelIdWithAggregate(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
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

            // capture saved hotel
            Hotel savedHotel = hotelRepo.save(hotel);

            // activity logging for created hotel
            safeActivityLogger.log(
                    ActivityType.CREATE_HOTEL,
                    "Hotel " + savedHotel.getName() +
                            " in " + savedHotel.getCity() + ", " + savedHotel.getCountry() + " created"
            );

        return savedHotel;
    }

    /// Delete hotel
    @Override
    public void deleteHotelByHotelId(String hotelId) {
        log.debug("Deleting hotel with business id {}", hotelId);

        Hotel hotel = hotelRepo.findByHotelId(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        // store values BEFORE delete (safe logging)
        String name = hotel.getName();
        String city = hotel.getCity();
        String country = hotel.getCountry();

        //  No manual room deletion
        // Cascade handles it automatically
        hotelRepo.delete(hotel);

        // Logging activity for deleted hotel
        safeActivityLogger.log(
                ActivityType.DELETE_HOTEL,
                "Hotel " + name +
                        " in " + city + ", " + country + " deleted"
        );
    }

    /// Search hotel by name
    @Override
    @Transactional(readOnly = true)
    public List<Hotel> searchByName(String text) {
        log.debug("Searching for hotels containing {}", text);

        // Input sanitization belongs in the service; controller passes raw values
        String cleanedText = (text == null) ? "" : text.trim();

        return hotelRepo.findByNameContainingIgnoreCase(cleanedText);
    }

    /// Sort hotels
    @Override
    @Transactional(readOnly = true)
    public List<Hotel> sortHotels(List<Hotel> hotels, String sort) {
        if (sort == null || sort.isBlank()) {
            return hotels;
        }

        List<Hotel> sortedHotels = new ArrayList<>(hotels);

        // Sorts hotels by name or stars in memory
        switch (sort) {
            case "name" ->
                    sortedHotels.sort(Comparator.comparing(
                            Hotel::getName,
                            String.CASE_INSENSITIVE_ORDER
                    ));
            case "stars" ->
                    sortedHotels.sort(Comparator.comparingInt(Hotel::getStars).reversed());
            default ->
                    log.warn("Unknown hotel sort key '{}'", sort);
        }

        return sortedHotels;
    }

    /// Update hotel description
    @Override
    @Transactional
    public void updateHotelDescription(String hotelId, String description) {
        log.debug("Updating description for hotel {}", hotelId);

        // validation and trimming belong in the service
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        Hotel hotel = hotelRepo.findByHotelId(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        hotel.setDescription(description.trim());
        // JPA dirty checking persists automatically

        // logging activity for updated hotel
        safeActivityLogger.log(
                ActivityType.UPDATE_HOTEL,
                "Updated description of hotel " + hotel.getName()
        );
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