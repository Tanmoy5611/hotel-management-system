package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.domain.Hotel;
import java.time.LocalDate;
import java.util.List;

// Service interface for all business logic related to Hotels.
// (The controller will call these methods instead of talking to the repository directly.)

public interface HotelService {
    List<Hotel> getAllHotels();       // return all hotels (used in /hotels page)

    // filter hotels based on min stars + opened date (used in search UI)
    List<Hotel> getHotelsByMinStarsAndDate(int minStars, String dateIn);

    // Creates a new hotel and saves it to the database
    Hotel createdHotel(Hotel hotel);

    // find a single hotel by id (used in hotel-detail page)
    Hotel getHotelById(String id);

    // delete hotel by id (also deletes its rooms because of cascade)
    void deleteHotel(String id);

    /// Spring Data method queries
    // search hotels by partial name (method query)
    List<Hotel> searchByName(String text);

    // filter only by stars (method query)
    List<Hotel> filterByStars(int minStars);

    // find hotels opened after a given date
    List<Hotel> getHotelsOpenedAfter(LocalDate date);

    // find hotels opened before a given date
    List<Hotel> getHotelsOpenedBefore(LocalDate date);

    // get only hotels that have or don't have spa
    List<Hotel> getHotelsWithSpa(boolean hasSpa);
}