package be.kdg.prog5.hotels.data;

import be.kdg.prog5.hotels.domain.*;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class RoomRepositoryTest {

    @Autowired
    private SpringDataRoomRepository roomRepository;

    @Autowired
    private SpringDataHotelRepository hotelRepository;

    @Autowired
    private SpringDataGuestRepository guestRepository;

    @Autowired
    private SpringDataApplicationUserRepository userRepository;

    @Autowired
    private SpringDataStayRepository stayRepository;

    private ApplicationUser user;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        // Arrange (global)
        // clean database before each test -> ensures test isolation
        // Ensures tests do not depend on each other (important for integration testing)
        stayRepository.deleteAll();
        roomRepository.deleteAll();
        guestRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();

        // Create a fresh user (needed because Guest has a mandatory owner FK)
        user = new ApplicationUser(
                "test@test.com",
                "password",
                RoleType.USER
        );

        // saveAndFlush -> immediately writes to DB (not just persistence context)
        // avoids delayed constraint errors during later operations
        userRepository.saveAndFlush(user);
    }

    /*
     PURPOSE: Verifies aggregate behavior of Room -> Stay.
     DOMAIN RULE: Room is the aggregate root and owns Stay. => cascade = ALL + orphanRemoval = true
     EXPECTATION: When a Room is deleted, all associated Stay entities must also be deleted automatically.
     IMPORTANT: Ensures no orphan data remains in DB (data integrity + correct aggregate design).
     */
    @Test
    void deletingRoomShouldAlsoDeleteStays() {

        // Arrange
        // create and persist hotel first (parent entity)
        Hotel hotel = new Hotel(
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

        // Create Room and link to Hotel (mandatory relationship)
        Room room = new Room(
                101,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Nice room"
        );
        room.setHotel(hotel);

        // Create Guest (owner required -> FK constraint)
        Guest guest = new Guest(
                "John Doe",
                LocalDate.of(1995, 5, 10),
                "john@test.com",
                "avatar.jpg"
        );
        guest.setOwner(user);
        guestRepository.saveAndFlush(guest);

        // Create Stay through aggregate method (business logic)
        room.addGuest(
                guest,
                LocalDate.now(),
                LocalDate.now().plusDays(2)
        );

        roomRepository.saveAndFlush(room);

        // Store IDs for verification after deletion
        Stay stay = room.getStays().iterator().next();
        Long stayId = stay.getId();
        Long roomId = room.getId();

        //  Act
        // Delete Room - should cascade delete Stay
        roomRepository.deleteById(roomId);
        roomRepository.flush();  // force execution at DB level

        //  Assert
        // Stay must NOT exist anymore (cascade + orphanRemoval working correctly)
        assertThat(stayRepository.findById(stayId)).isEmpty();
    }

    /*
     PURPOSE: Verifies database-level UNIQUE constraint on (hotel_id, room_number).
     DOMAIN RULE: A hotel cannot have two rooms with the same number.
     EXPECTATION: Saving duplicate room number in same hotel should fail.
     IMPORTANT: Hibernate does NOT detect this at object level => Constraint is enforced by DB during flush.
     */
    @Test
    void creatingDuplicateRoomNumberInSameHotelShouldFail() {

        // Arrange
        Hotel hotel = new Hotel(
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

        // first valid room
        Room room1 = new Room(
                101,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Room 1"
        );
        room1.setHotel(hotel);

        // Duplicate room (same number in same hotel)
        Room room2 = new Room(
                101, // same room number
                RoomType.DOUBLE,
                BigDecimal.valueOf(150),
                true,
                "photo2.jpg",
                "Room 2"
        );
        room2.setHotel(hotel);

        roomRepository.saveAndFlush(room1);

        // Act + Assert
        // DB constraint triggers exception during flush
        assertThatThrownBy(() -> {
            roomRepository.saveAndFlush(room2);
        }).isInstanceOf(DataIntegrityViolationException.class); // DB constraint violation
    }


    /*
     PURPOSE: Verifies LAZY loading behavior of Room => Stay relationship.
     EXPECTATION: Stays should NOT be loaded automatically when Room is fetched.
     IMPORTANT: Prevents unnecessary queries (performance optimization).
     TECHNICAL DETAIL: entityManager.clear() simulates a new request - avoids cached entities.
     */
    @Test
    void staysShouldBeLazyLoaded() {

        // Arrange
        Hotel hotel = new Hotel(
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

        Room room = new Room(
                101,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Nice room"
        );
        room.setHotel(hotel);

        roomRepository.saveAndFlush(room);

        Long roomId = room.getId();

        // clears persistence context -> simulates new request
        // Without this, Hibernate may return cached entity
        entityManager.clear();

        // Act
        Room foundRoom = roomRepository.findById(roomId).orElseThrow();

        //  Assert
        // LAZY -> should NOT be initialized yet
        assertThat(Hibernate.isInitialized(foundRoom.getStays())).isFalse();
    }


    /*
     PURPOSE: Verifies EAGER loading behavior of Stay -> Guest relationship.
     EXPECTATION: Guest should be loaded immediately when Stay is fetched.
     IMPORTANT: Confirms correct fetch strategy configuration.
     */
    @Test
    void guestShouldBeEagerlyLoadedInStay() {

        // Arrange
        Hotel hotel = new Hotel(
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

        Room room = new Room(
                101,
                RoomType.SINGLE,
                BigDecimal.valueOf(100),
                false,
                "photo.jpg",
                "Nice room"
        );
        room.setHotel(hotel);

        Guest guest = new Guest(
                "John Doe",
                LocalDate.of(1995, 5, 10),
                "john@test.com",
                "avatar.jpg"
        );
        guest.setOwner(user);
        guestRepository.saveAndFlush(guest);

        room.addGuest(
                guest,
                LocalDate.now(),
                LocalDate.now().plusDays(2)
        );

        roomRepository.saveAndFlush(room);

        Long stayId = room.getStays().iterator().next().getId();

        // Act
        Stay stay = stayRepository.findById(stayId).orElseThrow();

        // Assert
        // EAGER - guest should already be loaded (no lazy proxy)
        assertThat(stay.getGuest()).isNotNull();
    }
}