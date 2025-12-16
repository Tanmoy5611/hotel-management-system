package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.data.HotelRepository;
import be.kdg.prog3.hotels.domain.Hotel;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service                                                  // it is a Spring service bean (business layer)
@Profile({"inmemory", "jdbc", "jpa", "dev", "prod"})      // This service will be active in these profiles
public class HotelServiceImpl implements HotelService {

    // logger (used to show info in console for debugging)
    private static final Logger log = LoggerFactory.getLogger(HotelServiceImpl.class);
    private final HotelRepository repo;       // repository is injected means this class Never talks to DB directly

    // constructor injection (Spring gives us the correct repo based on active profile)
    public HotelServiceImpl(HotelRepository repo) {
        this.repo = repo;
    }

    /// Basic CRUD operations

    // return all hotels (used in /hotels page)
    @Override
    public List<Hotel> getAllHotels() {
        log.debug("Getting all hotels()");
        return repo.findAll();
    }

    // Returns hotels filtering with minimum stars and opened a certain date
    @Override
    public List<Hotel> getHotelsByMinStarsAndDate(int minStars, String dateIn) {
        log.debug("Getting hotels by min Stars And Date(minStars={}, opened date in={})", minStars, dateIn);

        /// this is the 'fallback' filtering logic using Java streams
        return repo.findAll().stream()
                // Stars filter
                .filter(h -> h.getStars() >= minStars)
                // Date filter (optional)
                .filter(h -> {
                    // if no date provided then do not filter by date
                    if (dateIn == null || dateIn.isEmpty()) return true;

                    try {
                        // Convert String to LocalDate (format: yyyy-MM-dd)
                        LocalDate filterDate = LocalDate.parse(dateIn); // yyyy-mm-dd format
                        return h.getOpenedOn().isAfter(filterDate);    // keep only hotels opened after this date
                    } catch (Exception e) {
                        return true; // ignore invalid date input by exception
                    }
                })
                .toList();
    }


    // create a new hotel and save it (used in add-hotel form)
    @Override
    public Hotel createdHotel(Hotel hotel) {
        log.debug("Creating hotel: {}", hotel);

        // generate URL-friendly ID based on the hotel name like: /hotels/plaza-athenee-paris
        String id = hotel.getName()
                .toLowerCase()
                .replace(" ", "-")    // spaces → dashes
                .replace("'", "")    // remove '
                .trim();

        hotel.setId(id);

        return repo.save(hotel);

    }

    // find Hotel by ID (used in detail page)
    @Override
    public Hotel getHotelById(String id) {
        log.debug("Getting hotel by id: {}", id);
        return repo.findHotelById(id);
    }

    // delete hotel by id
    @Override
    public void deleteHotel(String id) {
        log.debug("Deleting hotel {}", id);
        repo.delete(id);
    }

    // week 10 Extra methods for Spring Data Jpa (fallback java implementations)
    // ( Real JPA implementations are inside HotelRepositoryJPA)

    @Override
    public List<Hotel> searchByName(String text) {
        log.debug("Fallback searchByName('{}') using repo.findAll()", text);
        return repo.findAll().stream()
                .filter(h -> h.getName().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }


    @Override
    public List<Hotel> filterByStars(int minStars) {
        log.debug("Fallback filterByStars({}) using repo.findAll()", minStars);
        return repo.findAll().stream()
                .filter(h -> h.getStars() >= minStars)
                .toList();
    }

    @Override
    public List<Hotel> getHotelsOpenedAfter(LocalDate date) {
        log.debug("Fallback openedAfter({})", date);
        return repo.findAll().stream()
                .filter(h -> h.getOpenedOn().isAfter(date))
                .toList();
    }

    @Override
    public List<Hotel> getHotelsOpenedBefore(LocalDate date) {
        log.debug("Fallback openedBefore({})", date);
        return repo.findAll().stream()
                .filter(h -> h.getOpenedOn().isBefore(date))
                .toList();
    }

    @Override
    public List<Hotel> getHotelsWithSpa(boolean hasSpa) {
        log.debug("Fallback getHotelsWithSpa({})", hasSpa);
        return repo.findAll().stream()
                .filter(h -> h.isHasSpa() == hasSpa)
                .toList();
    }

}
