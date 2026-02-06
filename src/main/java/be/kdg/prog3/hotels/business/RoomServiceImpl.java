package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog3.hotels.data.HotelRepository;
import be.kdg.prog3.hotels.data.RoomRepository;
import be.kdg.prog3.hotels.domain.*;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Profile({"inmemory", "jdbc", "jpa", "dev", "prod"})
public class RoomServiceImpl implements RoomService {
    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);
    private final RoomRepository repo;
    private final HotelRepository hotelRepo;

    // Injects repository for data access
    public RoomServiceImpl(RoomRepository repo, HotelRepository hotelRepo) {
        this.repo = repo;
        this.hotelRepo = hotelRepo;
    }

    // Returns all rooms
    @Override
    public List<Room> getAllRooms() {
        log.debug("Get all rooms()");
        return repo.findAll();
    }

    /// Returns rooms filtering with parameters like type, sea view, and max price
    @Override
    public List<Room> findRooms(Optional<RoomType> type,
                                Optional<Boolean> seaView,
                                Optional<Double> maxPrice) {
        log.debug("Find rooms(type={}, seaView={}, maxPrice={})", type, seaView, maxPrice);

        return repo.findAll().stream()
                .filter(r -> type.map(t -> r.getType() == t).orElse(true))
                .filter(r -> seaView.map(b -> r.isSeaView() == b).orElse(true))
                .filter(r -> maxPrice.map(p -> r.getPricePerNight() <= p).orElse(true))
                .toList();
    }

     // Adds room to repository and returns it
    @Override
    public Room createRoom(Room room) {
       log.debug("Creating room: {}", room);

        // Load managed Hotel inside transaction
        var hotel = hotelRepo.findHotelById(room.getHotel().getId());

        room.setHotel(hotel);

        return repo.save(room);
    }


    //  get all rooms for a hotel (many-to-one)
    @Override
    public List<Room> getRoomsByHotel(String hotelId) {
        log.debug("Getting rooms for hotel {}", hotelId);
        return repo.findByHotel(hotelId);
    }

    // service throws RoomNotFoundException (represents Business error and handled at controller level)
    @Override
    public List<Room> getRoomsByNumber(int number) {
        log.debug("Get rooms by number {}", number);

        List<Room> rooms = repo.findAll().stream()
                .filter(r -> r.getNumber() == number)
                .toList();

        if (rooms.isEmpty()) {
            throw new RoomNotFoundException(number);
        }

        return rooms;
    }

    @Override
    public List<Room> getRoomsByGuest(Long guestId) {
        log.debug("Get rooms for guest {}", guestId);
        return repo.findByGuest(guestId);
    }

    @Override
    public Room getRoomById(Long roomId) {
        log.debug("Get room by id {}", roomId);

        Room room = repo.findById(roomId);
        if (room == null) {
            throw new RoomNotFoundException(-1); // or make a second exception for id
        }
        return room;
    }

    @Override
    public void deleteRoom(Long roomId) {
        log.debug("Deleting room {}", roomId);
        repo.delete(roomId);
    }

    @Override
    public double calculateDiscountedPrice(Room room, Guest guest) {
        double base = room.getPricePerNight();

        // Only VIPGuest has discount
        if (guest instanceof VIPGuest vip) {
            double discountPercent = vip.getDiscountPercentage();
            return base - (base * discountPercent / 100.0);
        }

        return base; // regular guest - no discount
    }

    @Override
    @Transactional
    public Room save(Room room) {
        return repo.save(room);
    }


    // room description
    @Override
    @Transactional
    public void updateRoomDescription(Long roomId, String description) {

        Room room = repo.findById(roomId);
        room.setDescription(description);
    }
}
