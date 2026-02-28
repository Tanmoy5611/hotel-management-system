package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private static final Logger log =
            LoggerFactory.getLogger(RoomServiceImpl.class);

    private final SpringDataRoomRepository roomRepo;
    private final SpringDataHotelRepository hotelRepo;
    private final SpringDataGuestRepository guestRepo;


    public RoomServiceImpl(SpringDataRoomRepository roomRepo,
                           SpringDataHotelRepository hotelRepo,
                           SpringDataGuestRepository guestRepo) {
        this.roomRepo = roomRepo;
        this.hotelRepo = hotelRepo;
        this.guestRepo = guestRepo;
    }

    // Read rooms
    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        log.debug("Getting all rooms: ");

        return roomRepo.findAllWithHotel();
    }

    // Search rooms by criteria
    @Override
    @Transactional(readOnly = true)
    public List<Room> findRooms(Optional<RoomType> type,
                                Optional<Boolean> seaView,
                                Optional<BigDecimal> maxPrice) {
        log.debug("Filtering rooms: type={}, seaView={}, maxPrice={}",
                type, seaView, maxPrice);

        return roomRepo.findFilteredRooms(
                type.orElse(null),
                seaView.orElse(null),
                maxPrice.orElse(null)
        );
    }

    // Create room - (Aggregate Root operation)
    @Override
    public Room createRoom(Room room, String hotelId) {
        log.debug("Creating room for hotel business id: {}", room + hotelId);

        // Room must always belong to a hotel
        // Fetch hotel using BUSINESS ID
        Hotel hotel = hotelRepo.findByHotelId(hotelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Hotel not found"));

        // Assign aggregate relation
        room.setHotel(hotel);

        return roomRepo.save(room);
    }

    // get rooms by number
    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByNumber(int roomNumber) {
        log.debug("Getting rooms with number {}", roomNumber);

        List<Room> rooms = roomRepo.findByNumberWithHotel(roomNumber);

        if (rooms.isEmpty()) {
            throw new RoomNotFoundException(roomNumber);
        }

        return rooms;
    }

    // get room by id
    /// LOAD FULL AGGREGATE
    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(Long roomId) {
        log.debug("Getting room aggregate with id {}", roomId);

        return roomRepo.findByIdWithHotelAndGuests(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    // Delete room; if not found, throw exception
    /// DELETE (Room is Aggregate Root of Stay)
    @Override
    public void deleteRoom(Long roomId) {
        log.debug("Deleting room {}", roomId);

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        // Because Room owns Stay with cascade + orphanRemoval,
        // deleting Room automatically deletes all related Stay rows
        roomRepo.delete(room);
    }

    // Update room description
    @Override
    @Transactional
    public void updateRoomDescription(Long roomId, String description) {
        log.debug("Updating room description for room {}", roomId);

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        room.setDescription(description);
        // No save() needed -> JPA dirty checking

    }

    // Proper Aggregate Operation (For UI have a “Book Room” feature)
    @Override
    public void bookRoom(Long roomId, Long guestId,
                         LocalDate checkIn,
                         LocalDate checkOut) {
        log.debug("Service: Adding guest {} to room {}", guestId, roomId);

        // lazy-loading query when we access room.getStays()
        Room room = roomRepo.findByIdWithHotelAndGuests(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        Guest guest = guestRepo.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));

        // Use the domain helper method (Aggregate Root logic)
        room.addGuest(guest, checkIn, checkOut);

        // CascadeType.ALL handles the saving of the new Stay record
        roomRepo.save(room);
    }


    /// Home page
    @Override
    @Transactional(readOnly = true)
    public List<Room> getBestValueRooms() {
        log.debug("Fetching top 4 best value rooms");

        return roomRepo.findTop4ByOrderByPricePerNightAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getPremiumRooms() {
        log.debug("Fetching top 4 premium rooms");

        return roomRepo.findTop4ByOrderByPricePerNightDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getTopPickedRooms() {
        log.debug("Fetching top picked rooms via aggregate count query");

        // The query handles the JOIN and COUNT (No N+1)
        // limit to 4 here to satisfy the UI requirement
        return roomRepo.findTopPickedRooms().stream()
                .limit(4)
                .toList();
    }
}