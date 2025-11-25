package be.kdg.prog3.hotels.business;

import be.kdg.prog3.hotels.data.GuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestServiceImpl implements GuestService {
    private static final Logger log = LoggerFactory.getLogger(GuestServiceImpl.class);
    private final GuestRepository repo;

    public GuestServiceImpl(GuestRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Guest> getAllGuests() {
        log.debug("Fetching all guests...");
        return repo.findAll();
    }

    @Override
    public List<Guest> getGuestsByRoom(int roomNumber) {
        log.debug("Fetching guests for room {}", roomNumber);
        return repo.findByRoom(roomNumber);
    }

    @Override
    public Guest createGuest(Guest guest) {
        log.debug("Creating new guest: {}", guest);
        return repo.save(guest);
    }

    @Override
    public Guest getGuestById(long id) {
        log.debug("Fetching guest by id {}", id);
        return repo.findById(id);
    }

    @Override
    public void deleteGuest(long id) {
        log.debug("Deleting guest by id {}", id);
        repo.delete(id);
    }
}