package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.data.HotelRepository;
import be.kdg.prog3.hotels.domain.Hotel;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl implements HotelService {
    private static final Logger log = LoggerFactory.getLogger(HotelServiceImpl.class);
    private final HotelRepository repo;

    // Injects repository for data access
    public HotelServiceImpl(HotelRepository repo) {
        this.repo = repo;
    }

    // Returns all hotels
    @Override
    public List<Hotel> getAllHotels() {
        log.debug("Getting all hotels()");
        return repo.findAll();
    }


    // Returns hotels filtering with minimum stars and opened a certain date
    @Override
    public List<Hotel> getHotelsByMinStarsAndDate(int minStars, String dateIn) {
        log.debug("Getting hotels by min Stars And Date(minStars={}, opened date in={})", minStars, dateIn);

        return repo.findAll().stream()
                .filter(h -> h.getStars() >= minStars)
                .filter(h -> {
                    if (dateIn == null || dateIn.isEmpty()) return true;
                    try {
                        LocalDate filterDate = LocalDate.parse(dateIn); // yyyy-mm-dd format
                        return h.getOpenedOn().isAfter(filterDate);
                    } catch (Exception e) {
                        return true; // ignore invalid date input by exception
                    }
                })
                .toList();
    }


    // Creates hotel and saves it to the repository
    @Override
    public Hotel createdHotel(Hotel hotel) {
        log.debug("Creating hotel: {}", hotel);

        // Auto generate ID based on hotel name
        String id = hotel.getName()
                .toLowerCase()
                .replace(" ", "-")
                .replace("'", "")
                .trim();

        hotel.setId(id);

        return repo.save(hotel);

    }

   // Get Hotel by ID
    @Override
    public Hotel getHotelById(String id) {
        log.debug("Getting hotel by id: {}", id);
        return repo.findById(id);
    }

    @Override
    public void deleteHotel(String id) {
        log.debug("Deleting hotel {}", id);
        repo.delete(id);
    }

}
