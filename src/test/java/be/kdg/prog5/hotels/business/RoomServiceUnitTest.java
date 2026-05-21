package be.kdg.prog5.hotels.business;

import be.kdg.prog5.hotels.business.exceptions.RoomAlreadyExistsException;
import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/* Unit Test Class
   PURPOSE: Test RoomServiceImpl with mocked repositories and mocked logging
   This keeps the test focused on service/business logic only */
@SpringBootTest(classes = RoomServiceImpl.class)
class RoomServiceUnitTest {

    // Mocked because this unit test should not use the real database
    @MockBean
    private SpringDataRoomRepository roomRepo;

    // Mocked because createRoom needs to look up the Hotel by business id
    @MockBean
    private SpringDataHotelRepository hotelRepo;

    // Mocked so tests can verify activity logging without depending on the security context
    @MockBean
    private SafeActivityLogger safeActivityLogger;

    // Real service bean under test; Spring injects the mocks above into its constructor
    @Autowired
    private RoomServiceImpl roomService;

    /* PURPOSE: Verify successful room creation
       EXPECTATION: Hotel is assigned, room is saved, and activity is logged */
    @Test
    void createRoomShouldAssignHotelSaveRoomAndLogActivity() {
        // Arrange
        Hotel hotel = createHotel();
        Room room = createRoom(101);

        when(hotelRepo.findByHotelId("api-test-hotel")).thenReturn(Optional.of(hotel));
        when(roomRepo.existsByHotelAndNumber(hotel, 101)).thenReturn(false);
        when(roomRepo.save(room)).thenReturn(room);

        // Act
        Room savedRoom = roomService.createRoom(room, "api-test-hotel");

        // Assert
        assertThat(savedRoom).isSameAs(room);
        assertThat(room.getHotel()).isSameAs(hotel);

        // Verify the service used the expected repository calls and logging arguments
        verify(hotelRepo).findByHotelId("api-test-hotel");
        verify(roomRepo).existsByHotelAndNumber(hotel, 101);
        verify(roomRepo).save(room);
        verify(safeActivityLogger).log(
                ActivityType.CREATE_ROOM,
                "Room 101 created in hotel api-test-hotel"
        );
    }

    /* PURPOSE: Verify duplicate room number protection
       EXPECTATION: Duplicate room throws exception and room is not saved */
    @Test
    void createRoomShouldThrowWhenRoomNumberAlreadyExistsInHotel() {
        // Arrange
        Hotel hotel = createHotel();
        Room room = createRoom(101);

        when(hotelRepo.findByHotelId("api-test-hotel")).thenReturn(Optional.of(hotel));
        when(roomRepo.existsByHotelAndNumber(hotel, 101)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> roomService.createRoom(room, "api-test-hotel"))
                .isInstanceOf(RoomAlreadyExistsException.class);

        verify(roomRepo, never()).save(room);
        verifyNoInteractions(safeActivityLogger);
    }

    /* PURPOSE: Verify missing hotel behavior
       EXPECTATION: Service fails before duplicate check, save, or logging */
    @Test
    void createRoomShouldThrowWhenHotelDoesNotExist() {
        // Arrange
        Room room = createRoom(101);

        when(hotelRepo.findByHotelId("missing-hotel")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> roomService.createRoom(room, "missing-hotel"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Hotel not found: missing-hotel");

        verify(roomRepo, never()).existsByHotelAndNumber(null, 101);
        verify(roomRepo, never()).save(room);
        verifyNoInteractions(safeActivityLogger);
    }

    /* PURPOSE: Verify search input sanitization when no dates are selected
       EXPECTATION: Query is trimmed and repository results are returned directly */
    @Test
    void searchAvailableRoomsShouldCleanQueryAndReturnDatabaseFilteredRoomsWhenDatesAreMissing() {
        // Arrange
        Room room = createRoom(101);
        when(roomRepo.searchRooms("brussels", RoomType.DOUBLE)).thenReturn(List.of(room));

        // Act
        List<Room> rooms = roomService.searchAvailableRooms("  brussels  ", RoomType.DOUBLE, null, null);

        // Assert
        assertThat(rooms).containsExactly(room);
        verify(roomRepo).searchRooms("brussels", RoomType.DOUBLE);
    }

    /* PURPOSE: Verify invalid date validation
       EXPECTATION: service throws before any repository call */
    @Test
    void searchAvailableRoomsShouldRejectInvalidDateRangeBeforeCallingRepository() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = checkIn;

        // Act + Assert
        assertThatThrownBy(() -> roomService.searchAvailableRooms("brussels", null, checkIn, checkOut))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Check-out must be after check-in");

        verifyNoInteractions(roomRepo);
    }

    /* PURPOSE: Verify availability filtering for overlapping bookings
       EXPECTATION: Unavailable room is removed, available room remains */
    @Test
    void searchAvailableRoomsShouldFilterOutRoomsWithOverlappingStays() {
        // Arrange
        LocalDate existingCheckIn = LocalDate.now().plusDays(20);
        LocalDate existingCheckOut = existingCheckIn.plusDays(4);
        LocalDate requestedCheckIn = existingCheckIn.plusDays(1);
        LocalDate requestedCheckOut = existingCheckOut.plusDays(1);

        Room unavailableRoom = createRoom(101);
        unavailableRoom.addGuest(
                new Guest("Existing Guest", LocalDate.of(1990, 1, 1), "guest1@test.com", "guest.jpg"),
                existingCheckIn,
                existingCheckOut
        );

        Room availableRoom = createRoom(102);

        when(roomRepo.searchRoomsWithStays("", null)).thenReturn(List.of(unavailableRoom, availableRoom));

        // Act
        List<Room> rooms = roomService.searchAvailableRooms(null, null, requestedCheckIn, requestedCheckOut);

        // Assert
        assertThat(rooms).containsExactly(availableRoom);
        verify(roomRepo).searchRoomsWithStays("", null);
    }

    /* Test Helper Method
       PURPOSE: Create a Hotel object for service unit tests */
    private Hotel createHotel() {
        return new Hotel(
                "api-test-hotel",
                "API Test Hotel",
                "Antwerp",
                "Belgium",
                LocalDate.of(2020, 1, 1),
                5,
                false,
                "hotel.jpg",
                "Test hotel"
        );
    }

    /* Test Helper Method
       PURPOSE: Create a Room object with a variable room number */
    private Room createRoom(int number) {
        return new Room(
                number,
                RoomType.DOUBLE,
                BigDecimal.valueOf(120),
                true,
                "room.jpg",
                "Nice room"
        );
    }
}