package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.data.springdata.SpringDataGuestRepository;
import be.kdg.prog3.hotels.data.springdata.SpringDataRoomRepository;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Room;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Profile("springdata")   // Activate this profile to use Spring Data JPA version
public class SpringDataGuestServiceImpl implements GuestService {

    private final SpringDataGuestRepository repo;
    private final SpringDataRoomRepository roomRepo;

    public SpringDataGuestServiceImpl(SpringDataGuestRepository repo, SpringDataRoomRepository roomRepo) {
        this.repo = repo;
        this.roomRepo = roomRepo;
    }

    // Required Interface methods

    @Override
    public List<Guest> getAllGuests() {
        return repo.findAll();
    }

    @Override
    public List<Guest> getGuestsByRoom(Long roomId) {
        return repo.findByRoom(roomId);
    }

    @Override
    public Guest createGuest(Guest guest) {
        return repo.save(guest);
    }

    @Override
    public Guest getGuestById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void deleteGuest(Long id) {
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
    @Transactional
    public Guest createGuestWithRoom(Guest guest, Long roomId) {

        // 1️⃣ Save guest first
        Guest savedGuest = repo.save(guest);

        if (roomId != null) {
            // 2️⃣ Load room INSIDE transaction
            Room room = roomRepo.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found"));

            // 3️⃣ Modify OWNING SIDE only
            room.addGuest(savedGuest);

            // 4️⃣ Save OWNING SIDE
            roomRepo.save(room);
        }

        return savedGuest;
    }
}