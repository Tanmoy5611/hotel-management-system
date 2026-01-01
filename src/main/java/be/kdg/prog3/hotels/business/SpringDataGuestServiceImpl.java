package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.data.springdata.SpringDataGuestRepository;
import be.kdg.prog3.hotels.domain.Guest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Profile("springdata")   // Activate this profile to use Spring Data JPA version
public class SpringDataGuestServiceImpl implements GuestService {

    private final SpringDataGuestRepository repo;
    private final RoomService roomService;

    public SpringDataGuestServiceImpl(SpringDataGuestRepository repo, RoomService roomService) {
        this.repo = repo;
        this.roomService = roomService;
    }

    // Required Interface methods

    @Override
    public List<Guest> getAllGuests() {
        return repo.findAll();
    }

    @Override
    public List<Guest> getGuestsByRoom(int roomNumber) {
        return repo.findByRoom(roomNumber);
    }

    @Override
    public Guest createGuest(Guest guest) {
        return repo.save(guest);
    }

    @Override
    public Guest getGuestById(long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void deleteGuest(long id) {
        repo.deleteById(id);
    }

    public List<Guest> getVipGuests() {
        return repo.findByVipTrue();
    }

    public List<Guest> searchGuestsByName(String name) {
        return repo.findByFullNameContainingIgnoreCase(name);
    }

    public List<Guest> getGuestsWithManyRooms(int minRooms) {
        return repo.findGuestsWithMoreThanRooms(minRooms);
    }

    @Override
    public Guest createGuestWithRoom(Guest guest, Integer roomNumber) {

        // Save guest first (needed to generate the ID)
        guest = repo.save(guest);

        // If room number provided, link room
        if (roomNumber != null) {

            // Load room through RoomService
            var room = roomService.getRoomByNumber(roomNumber);

            if (room != null) {
                // maintain bidirectional relation
                guest.addRoom(room);

                // save again so join table is updated
                repo.save(guest);
            }
        }

        return guest;
    }
}