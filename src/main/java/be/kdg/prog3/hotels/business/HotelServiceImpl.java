package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.data.HotelRepository;
import be.kdg.prog3.hotels.domain.Hotel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl implements HotelService {
    private final HotelRepository repo;

    public HotelServiceImpl(HotelRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Hotel> getAllHotels() {
        return repo.findAll();
    }

    @Override
    public List<Hotel> getHotelsByMinStarsAndDate(int minStars, String dateIn) {
        return repo.findAll().stream()
                .filter(h -> h.getStars() >= minStars)
                .filter(h -> {
                    if (dateIn == null || dateIn.isEmpty()) return true;
                    try {
                        LocalDate filterDate = LocalDate.parse(dateIn); // yyyy-mm-dd
                        return h.getOpenedOn().isAfter(filterDate);
                    } catch (Exception e) {
                        return true; // ignore invalid input
                    }
                })
                .toList();
    }
}
