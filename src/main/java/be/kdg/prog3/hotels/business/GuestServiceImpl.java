package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.data.GuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.VIPGuest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Profile({"inmemory", "jdbc", "jpa", "dev", "prod"})
public class GuestServiceImpl implements GuestService {
    private static final Logger log = LoggerFactory.getLogger(GuestServiceImpl.class);

    private final GuestRepository repo;
    private final RoomService roomService;

    public GuestServiceImpl(GuestRepository repo, RoomService roomService) {
        this.repo = repo;
        this.roomService = roomService;
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
    @Transactional
    public void deleteGuest(long id) {

        // Find guest (no Optional in JDBC repository)
        Guest g = repo.findById(id);
        if (g == null) {
            throw new IllegalArgumentException("Guest not found");
        }

        // Unlink guest from all rooms (Many-to-Many)
        for (Room room : g.getRooms()) {
            room.getGuests().remove(g);
        }
        g.getRooms().clear();

        // Delete using ID, because GuestRepository.delete(long id)
        repo.delete(id);
    }

    @Override
    public List<Guest> getVipGuests() {
        log.debug("Fetching VIP guests...");
        return repo.findAll()
                .stream()
                .filter(g -> g instanceof VIPGuest || g.isVip())
                .toList();
    }

    @Override
    public List<Guest> searchGuestsByName(String name) {
        log.debug("Searching guests by name: {}", name);
        return repo.findAll()
                .stream()
                .filter(g -> g.getFullName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    @Override
    public List<Guest> getGuestsWithManyRooms(int minRooms) {
        log.debug("Fetching guests with at least {} rooms...", minRooms);

        // must NOT use g.getRooms() in JDBC — rooms list is empty until JPA loads relations.
        return repo.findAll()
                .stream()
                .filter(g -> roomService.getRoomsByGuest(g.getId()).size() >= minRooms)
                .toList();
    }

    @Transactional
    @Override
    public Guest createGuestWithRoom(Guest guest, Integer roomNumber) {

        // Save guest first
        guest = repo.save(guest);

        // If user typed a room number -  link it
        if (roomNumber != null) {
            Room room = roomService.getRoomByNumber(roomNumber);
            if (room != null) {

                // Bidirectional sync
                guest.addRoom(room);
                room.getGuests().add(guest);

                // Save again so join-table persists
                repo.save(guest);
            }
        }

        return guest;
    }
}