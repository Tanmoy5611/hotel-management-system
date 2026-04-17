package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.BookingException;
import be.kdg.prog5.hotels.business.exceptions.GuestNotFoundException;
import be.kdg.prog5.hotels.business.exceptions.RoomAlreadyExistsException;
import be.kdg.prog5.hotels.business.exceptions.RoomNotFoundException;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.domain.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@ActiveProfiles("test")
class RoomServiceTest {

    // (services, repositories) -> Spring manages -> @Autowired
    // so it's used to inject Spring-managed beans from the application context
    @Autowired
    private RoomService roomService;

    @Autowired
    private SpringDataRoomRepository roomRepository;

    @Autowired
    private SpringDataHotelRepository hotelRepository;

    @Autowired
    private SpringDataGuestRepository guestRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SpringDataApplicationUserRepository userRepository;

    // normal objects like entities are created manually and don’t require dependency injection -> no @Autowired
    private ApplicationUser user;

    private Hotel hotel;
    private Guest guest;

    @BeforeEach
    void setup() {

        // Arrange (global)
        // Clean database before each test -> ensures isolation
        roomRepository.deleteAll();
        guestRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();

        // Create user (required because Guest has NOT NULL owner FK)
        user = new ApplicationUser(
                "test@test.com",
                "password",
                RoleType.USER
        );
        userRepository.saveAndFlush(user);


        // Create hotel (Room must always belong to a Hotel)
        hotel = new Hotel(
                "hotel-1",
                "Test Hotel",
                "Antwerp",
                "Belgium",
                LocalDate.now(),
                3,
                false,
                "img.jpg",
                "Nice hotel"
        );
        hotelRepository.saveAndFlush(hotel);

        // Create guest (needed for booking tests)
        guest = new Guest(
                "John Doe",
                LocalDate.of(1995, 5, 10),
                "john@test.com",
                "avatar.jpg"
        );

        guest.setOwner(user);
        guestRepository.saveAndFlush(guest);
    }

    /*
     PURPOSE: Verify that a room can be created correctly through the service layer.
     BUSINESS RULE: Room must be linked to an existing Hotel.
     EXPECTATION: Room is saved and correctly associated with the given hotelId.
     */
    @Test
    void shouldCreateRoomSuccessfully() {

        // Arrange
        Room room = new Room(
                101,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Nice room"
        );

        //  Act
        Room savedRoom = roomService.createRoom(room, "hotel-1");

        //  Assert
        // verify room is saved and linked to hotel
        assertThat(savedRoom.getId()).isNotNull();
        assertThat(savedRoom.getHotel().getHotelId()).isEqualTo("hotel-1");
    }

    /*
     PURPOSE: Verify business validation for duplicate room numbers.
     BUSINESS RULE: A hotel cannot have two rooms with the same number.
     IMPORTANT: This validation is done at SERVICE level (before DB), not relying only on database constraints.
     */
    @Test
    void shouldFailWhenCreatingDuplicateRoom() {

        // Arrange
        Room room1 = new Room(
                101,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Room 1"
        );

        Room room2 = new Room(
                101, // duplicate room number
                RoomType.DOUBLE,
                BigDecimal.valueOf(150),
                true,
                "photo2.jpg",
                "Room 2"
        );

        roomService.createRoom(room1, "hotel-1");

        //  Act + Assert
        // service-level validation BEFORE DB
        assertThatThrownBy(() ->
                roomService.createRoom(room2, "hotel-1")
        ).isInstanceOf(RoomAlreadyExistsException.class);
    }


    /*
     PURPOSE: Verify fetching a room by ID using service layer.
     IMPORTANT: Service uses JOIN FETCH -> returns full aggregate (Room + related data).
     entityManager.clear(): Simulates new request -> avoids cached entity.
     */
    @Test
    void shouldGetRoomByIdSuccessfully() {

        //  Arrange
        Room room = new Room(
                102,
                RoomType.SINGLE,
                BigDecimal.valueOf(120),
                false,
                "photo.jpg",
                "Room"
        );

        Room saved = roomService.createRoom(room, "hotel-1");

        // simulate new request (important for lazy testing)
        entityManager.clear();

        //  Act
        Room found = roomService.getRoomById(saved.getId());

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
    }

    /*
     PURPOSE: Verify exception handling when room does not exist.
     EXPECTATION: Service throws RoomNotFoundException.
     */
    @Test
    void shouldThrowWhenRoomNotFound() {

        // Act + Assert
        assertThatThrownBy(() ->
                roomService.getRoomById(999L)
        ).isInstanceOf(RoomNotFoundException.class);
    }

    /*
     PURPOSE: Verify deleting a room through service layer.
     EXPECTATION: Room should be removed from database.
     */
    @Test
    void shouldDeleteRoomSuccessfully() {

        // Arrange
        Room room = new Room(
                103,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Room"
        );

        Room saved = roomService.createRoom(room, "hotel-1");

        // Act
        roomService.deleteRoom(saved.getId());
        roomRepository.flush();

        // Assert
        assertThat(roomRepository.findById(saved.getId())).isEmpty();
    }

    /*
     PURPOSE: Verify updating room description using JPA dirty checking.
     IMPORTANT: No save() is required -> entity is updated automatically inside transaction.
     */
    @Test
    void shouldUpdateRoomDescription() {

        // Arrange
        Room room = new Room(
                104,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Old description"
        );

        Room saved = roomService.createRoom(room, "hotel-1");

        // Act
        roomService.updateRoomDescription(saved.getId(), "New description");

        entityManager.clear(); // ensure fresh read

        // Assert
        Room updated = roomRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getDescription()).isEqualTo("New description");
    }

    /*
     PURPOSE: Verify booking a room (core aggregate operation).
     BUSINESS RULE: Room is aggregate root => creates Stay internally.
     EXPECTATION: Booking creates a new Stay linked to Room and Guest.
     */
    @Test
    void shouldBookRoomSuccessfully() {

        // Arrange
        Room room = new Room(
                105,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Room"
        );

        Room savedRoom = roomService.createRoom(room, "hotel-1");

        // Act
        roomService.bookRoom(
                savedRoom.getId(),
                guest.getId(),
                LocalDate.now(),
                LocalDate.now().plusDays(2)
        );

        entityManager.clear();

        // Assert
        Room found = roomService.getRoomById(savedRoom.getId());

        // booking creates Stay inside aggregate
        assertThat(found.getStays()).hasSize(1);
        Stay stay = found.getStays().iterator().next();
        assertThat(stay.getGuest().getId()).isEqualTo(guest.getId());
        assertThat(stay.getRoom().getId()).isEqualTo(found.getId());
    }

    /*
     PURPOSE: Verify filtering logic using optional parameters.
     DESIGN: Optional is used to avoid null checks in service layer.
     EXPECTATION: Only rooms matching criteria are returned.
     */
    @Test
    void shouldFindRoomsWithFilters() {

        // Arrange
        Room room = new Room(
                106,
                RoomType.SUITE,
                BigDecimal.valueOf(300),
                true,
                "photo.jpg",
                "Luxury room"
        );

        roomService.createRoom(room, "hotel-1");

        // Act
        // Optional in filters - To allow flexible query parameters without null checks in service logic
        List<Room> result = roomService.findRooms(
                Optional.of(RoomType.SUITE),
                Optional.of(true),
                Optional.of(BigDecimal.valueOf(500))
        );

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result)
                .allMatch(r -> r.getType() == RoomType.SUITE);
    }

    /*
     PURPOSE: Verify domain validation when check-out is before check-in.
     EXPECTATION: Booking should fail with BookingException.
     */
    @Test
    void shouldFailWhenCheckOutBeforeCheckIn() {

        // Arrange
        Room room = new Room(
                300,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Room"
        );

        Room savedRoom = roomService.createRoom(room, "hotel-1");

        // Act + Assert
        assertThatThrownBy(() ->
                roomService.bookRoom(
                        savedRoom.getId(),
                        guest.getId(),
                        LocalDate.now(),
                        LocalDate.now().minusDays(1)
                )
        ).isInstanceOf(BookingException.class);
    }

    /*
     PURPOSE: Verify validation when booking with non-existing Guest.
     EXPECTATION: Service throws GuestNotFoundException.
     */
    @Test
    void shouldFailWhenBookingWithNonExistingGuest() {

        // Arrange
        Room room = new Room(
                200,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Room"
        );

        Room savedRoom = roomService.createRoom(room, "hotel-1");

        Long nonExistingGuestId = 999L; // does not exist

        // Act + Assert
        assertThatThrownBy(() ->
                roomService.bookRoom(
                        savedRoom.getId(),
                        nonExistingGuestId,
                        LocalDate.now(),
                        LocalDate.now().plusDays(2)
                )
        ).isInstanceOf(GuestNotFoundException.class);
    }

    /*
     PURPOSE: Verify validation when booking with non-existing Room.
     EXPECTATION: Service throws RoomNotFoundException.
     */
    @Test
    void shouldFailWhenBookingWithNonExistingRoom() {

        //  Arrange
        Long nonExistingRoomId = 999L;

        //  Act + Assert
        assertThatThrownBy(() ->
                roomService.bookRoom(
                        nonExistingRoomId,
                        guest.getId(),
                        LocalDate.now(),
                        LocalDate.now().plusDays(2)
                )
        ).isInstanceOf(RoomNotFoundException.class);
    }
}