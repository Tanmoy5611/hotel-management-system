package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.RoomAlreadyExistsException;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private static final Logger log =
            LoggerFactory.getLogger(RoomServiceImpl.class);

    private static final int HOME_PAGE_ROOM_LIMIT = 4;

    private final SpringDataRoomRepository roomRepo;
    private final SpringDataHotelRepository hotelRepo;

    // logging is a business concern
    private final SafeActivityLogger safeActivityLogger;


    // Injects room dependencies after booking logic was moved to BookingService
    public RoomServiceImpl(SpringDataRoomRepository roomRepo,
                           SpringDataHotelRepository hotelRepo,
                           SafeActivityLogger safeActivityLogger) {
        this.roomRepo = roomRepo;
        this.hotelRepo = hotelRepo;
        this.safeActivityLogger = safeActivityLogger;
    }

    // Reads all rooms with hotel data for the room overview
    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        log.debug("Getting all rooms");

        return roomRepo.findAllWithHotel();
    }

    // Filters rooms by optional type, sea view, and maximum price
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

    // Creates a room inside the selected hotel aggregate
    @Override
    public Room createRoom(Room room, String hotelId) {
        log.debug("Creating room {} for hotel {}", room.getNumber(), hotelId);

        // Room must always belong to a hotel
        // Fetch hotel using BUSINESS ID
        Hotel hotel = hotelRepo.findByHotelId(hotelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Hotel not found: " + hotelId));

        // duplicate room number check
        if (roomRepo.existsByHotelAndNumber(hotel, room.getNumber())) {
            throw new RoomAlreadyExistsException(room.getNumber(), hotelId);
        }

        // Assign aggregate relation
        room.setHotel(hotel);

        Room savedRoom = roomRepo.save(room);

        // Logging activity for created room
        safeActivityLogger.log(
                ActivityType.CREATE_ROOM,
                "Room " + room.getNumber() + " created in hotel " + hotelId
        );

        return savedRoom;
    }

    // Looks up rooms by room number and fails when none exist
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

    // Loads the full room aggregate for detail and booking pages
    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(Long roomId) {
        log.debug("Getting room aggregate with id {}", roomId);

        // Sorting is handled in DB using ORDER BY check-in date
        return roomRepo.findByIdWithHotelAndGuestsSortedByCheckIn(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }

    // Deletes a room and relies on Room ownership to remove related stays
    @Override
    public void deleteRoom(Long roomId) {
        log.debug("Deleting room {}", roomId);

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        String roomNumber = String.valueOf(room.getNumber());
        String hotelName = room.getHotel().getName();

        // Because Room owns Stay with cascade + orphanRemoval,
        // deleting Room automatically deletes all related Stay rows
        roomRepo.delete(room);

        // Logging activity for deleted room
        safeActivityLogger.log(
                ActivityType.DELETE_ROOM,
                "Room " + roomNumber + " in hotel " + hotelName + " deleted"
        );
    }

    // Updates the room description through dirty checking
    @Override
    @Transactional
    public void updateRoomDescription(Long roomId, String description) {
        log.debug("Updating room description for room {}", roomId);

        // Validation belongs in the service layer, not the controller
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
        room.setDescription(description.trim());
        // No save() needed -> JPA dirty checking

        // Logging activity for updated room
        safeActivityLogger.log(
                ActivityType.UPDATE_ROOM,
                "Updated description of room " + room.getNumber() + " in hotel " + room.getHotel().getName()
        );
    }

    // Reads cheapest rooms for the home page best value section
    @Override
    @Transactional(readOnly = true)
    public List<Room> getBestValueRooms() {
        log.debug("Fetching top 4 best value rooms");

        return roomRepo.findCheapestRooms(
                PageRequest.of(0, HOME_PAGE_ROOM_LIMIT)
        );
    }

    // Reads most expensive rooms for the home page premium section
    @Override
    @Transactional(readOnly = true)
    public List<Room> getPremiumRooms() {
        log.debug("Fetching top 4 premium rooms");

        return roomRepo.findMostExpensiveRooms(
                PageRequest.of(0, HOME_PAGE_ROOM_LIMIT)
        );
    }

    // Reads most booked rooms for the home page top picks section
    @Override
    @Transactional(readOnly = true)
    public List<Room> getTopPickedRooms() {
        log.debug("Fetching top picked rooms via aggregate count query");

        return roomRepo.findTopPickedRooms(
                PageRequest.of(0, HOME_PAGE_ROOM_LIMIT)
        );
    }

    // Searches available rooms from the home page filters
    @Override
    @Transactional(readOnly = true)
    public List<Room> searchAvailableRooms(String query,
                                           RoomType roomType,
                                           LocalDate checkIn,
                                           LocalDate checkOut) {

        log.debug("Searching rooms: query={}, roomType={}, checkIn={}, checkOut={}",
                query, roomType, checkIn, checkOut);

        // Keep query as empty string instead of null to avoid PostgreSQL type issues
        String cleanedQuery = (query == null) ? "" : query.trim();

        // Validate dates (basic business rule)
        if (checkIn != null && checkOut != null && !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }

        // Fetch rooms from database using one flexible query
        // (query + roomType handled in repository)
        List<Room> rooms = roomRepo.searchRooms(cleanedQuery, roomType);

        // If no dates provided -> just return filtered rooms (no availability check)
        if (checkIn == null || checkOut == null) {
            return rooms;
        }

        // Otherwise -> filter manually for availability
        // (kept in service layer because it's domain logic, not DB logic)
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms) {
            // Use domain method isAvailable() to verify if the room is available
            // for the given check-in and check-out dates
            if (room.isAvailable(checkIn, checkOut)) {
                // If available, add it to the result list
                availableRooms.add(room);
            }
        }

        // Return only the rooms that passed the availability check
        return availableRooms;
    }
}