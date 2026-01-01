package be.kdg.prog3.hotels.business;
import be.kdg.prog3.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog3.hotels.data.springdata.SpringDataRoomRepository;
import be.kdg.prog3.hotels.domain.Guest;
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
    public Room createdRoom(Room room) {
        return repo.save(room);
    }

    @Override
    public List<Room> getRoomsByHotel(String hotelId) {
        List<Room> rooms = repo.findAll();

        return rooms.stream()
                .filter(r -> String.valueOf(r.getHotel().getId()).equals(hotelId))
                .toList();
    }

    ///  throws exception
    @Override
    public Room getRoomByNumber(int number) {
        return repo.findById(number)
                .orElseThrow(() -> new RoomNotFoundException(number));
    }

    @Override
    public List<Room> getRoomsByGuest(long guestId) {
        return repo.findAll().stream()
                .filter(r -> r.getGuests().stream()
                        .anyMatch(g -> g.getId() == guestId))
                .toList();
    }

    @Override
    public void deleteRoom(int number) {
        repo.deleteById(number);
    }

    @Override
    public double calculateDiscountedPrice(Room room, Guest guest) {
        double price = room.getPricePerNight();
        if (guest.isVip()) price *= 0.9;
        return price;
    }
}