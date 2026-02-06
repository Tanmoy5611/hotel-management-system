package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog3.hotels.data.springdata.SpringDataRoomRepository;
import be.kdg.prog3.hotels.domain.Guest;
import be.kdg.prog3.hotels.domain.Hotel;
import be.kdg.prog3.hotels.domain.Room;
import be.kdg.prog3.hotels.domain.RoomType;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Profile("springdata")
public class SpringDataRoomServiceImpl implements RoomService {

    private final SpringDataRoomRepository repo;

    public SpringDataRoomServiceImpl(SpringDataRoomRepository repo) {
        this.repo = repo;
    }

    // Old methods implementation
    @Override
    public List<Room> getAllRooms() {
        return repo.findAll();
    }

    @Override
    public List<Room> findRooms(Optional<RoomType> type,
                                Optional<Boolean> seaView,
                                Optional<Double> maxPrice) {

        List<Room> rooms = repo.findAll();

        return rooms.stream()
                .filter(r -> type.map(t -> r.getType() == t).orElse(true))
                .filter(r -> seaView.map(s -> r.isSeaView() == s).orElse(true))
                .filter(r -> maxPrice.map(m -> r.getPricePerNight() <= m).orElse(true))
                .toList();
    }

    @Transactional
    @Override
    public Room createRoom(Room room) {
        return repo.save(room);
    }

    @Override
    public List<Room> getRoomsByHotel(String hotelId) {
        return repo.findAll().stream()
                .filter(r -> r.getHotel() != null &&
                        r.getHotel().getId().equals(hotelId))
                .toList();
    }

    //  throws exception
    @Override
    public List<Room> getRoomsByNumber(int number) {
        List<Room> rooms = repo.findAll().stream()
                .filter(r -> r.getNumber() == number)
                .toList();

        if (rooms.isEmpty()) {
            throw new RoomNotFoundException(number);
        }

        return rooms;
    }

    @Override
    public Room getRoomById(Long roomId) {
        return repo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    @Override
    public List<Room> getRoomsByGuest(Long guestId) {
        //  no lazy loading, no streams, no session error
        return repo.findRoomsByGuestId(guestId);
    }


    @Override
    public void deleteRoom(Long roomId) {
        repo.deleteById(roomId);
    }

    @Override
    public double calculateDiscountedPrice(Room room, Guest guest) {
        double price = room.getPricePerNight();
        if (guest.isVip()) {
            price *= 0.9;
        }
        return price;
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

        Room room = repo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        room.setDescription(description);
    }
}