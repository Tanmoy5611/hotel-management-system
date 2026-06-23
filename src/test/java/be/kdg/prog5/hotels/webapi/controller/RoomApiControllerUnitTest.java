package be.kdg.prog5.hotels.webapi.controller;

import be.kdg.prog5.hotels.business.RoomService;
import be.kdg.prog5.hotels.business.exceptions.RoomAlreadyExistsException;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import be.kdg.prog5.hotels.webapi.dto.RoomDto;
import be.kdg.prog5.hotels.webapi.exception.ApiExceptionHandler;
import be.kdg.prog5.hotels.webapi.mapper.RoomMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Unit Test Class
   PURPOSE: Test one REST API endpoint with mocked dependencies
   Only RoomApiController is real here; RoomService and RoomMapper are mocked. */
@WebMvcTest(RoomApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class RoomApiControllerUnitTest {

    // Used to simulate HTTP requests without starting a real server
    @Autowired
    private MockMvc mockMvc;

    // Mocked because this is a controller unit test, not a service/integration test
    @MockBean
    private RoomService roomService;

    // Mocked so the test controls the API response mapping
    @MockBean
    private RoomMapper roomMapper;

    /* PURPOSE: Verify that a valid POST request creates a room
       EXPECTATION: HTTP 201 Created, Location header, and response body returned */
    @Test
    void createRoomShouldReturnCreatedRoomDto() throws Exception {
        // Arrange
        Room savedRoom = new Room(101, RoomType.DOUBLE,
                new BigDecimal("120.00"), true, "room.jpg", "Nice room");
        RoomDto responseDto = new RoomDto(7L, 101, "DOUBLE", new BigDecimal("120.00"), true,
                "room.jpg", "Nice room", "api-test-hotel", "API Test Hotel");

        when(roomService.createRoom(101, RoomType.DOUBLE,
                new BigDecimal("120.00"), true, "room.jpg", "Nice room", "api-test-hotel"))
                .thenReturn(savedRoom);
        when(roomMapper.toDto(savedRoom)).thenReturn(responseDto);

        // Act + Assert
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "number": 101,
                                  "pricePerNight": 120.00,
                                  "type": "DOUBLE",
                                  "seaView": true,
                                  "photoUrl": "room.jpg",
                                  "description": "Nice room",
                                  "hotelId": "api-test-hotel"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/rooms/7"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.number").value(101))
                .andExpect(jsonPath("$.hotelName").value("API Test Hotel"));

        // Verify important controller interactions with mocked dependencies
        verify(roomService).createRoom(101, RoomType.DOUBLE,
                new BigDecimal("120.00"), true, "room.jpg", "Nice room", "api-test-hotel");
        verify(roomMapper).toDto(savedRoom);
    }

    /* PURPOSE: Verify validation behavior when required fields are missing
       EXPECTATION: HTTP 400 Bad Request and no service/mapper call */
    @Test
    void createRoomShouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pricePerNight": 120.00,
                                  "type": "DOUBLE",
                                  "hotelId": "api-test-hotel"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(roomService);
        verifyNoInteractions(roomMapper);
    }

    /* PURPOSE: Verify invalid enum JSON is handled before the service runs
      EXPECTATION: HTTP 400 and no dependency interaction */
    @Test
    void createRoomShouldReturnBadRequestForAnInvalidRoomType() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "number": 101,
                                  "pricePerNight": 120.00,
                                  "type": "PENTHOUSE",
                                  "hotelId": "api-test-hotel"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(roomService);
        verifyNoInteractions(roomMapper);
    }

    /* PURPOSE: Verify duplicate room handling from the service layer
       EXPECTATION: HTTP 409 Conflict and no DTO response mapping */
    @Test
    void createRoomShouldReturnConflictWhenRoomAlreadyExists() throws Exception {
        // Arrange
        when(roomService.createRoom(101, RoomType.DOUBLE,
                new BigDecimal("120.00"), true, "room.jpg", "Nice room", "api-test-hotel"))
                .thenThrow(new RoomAlreadyExistsException(101, "api-test-hotel"));

        // Act + Assert
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "number": 101,
                                  "pricePerNight": 120.00,
                                  "type": "DOUBLE",
                                  "seaView": true,
                                  "photoUrl": "room.jpg",
                                  "description": "Nice room",
                                  "hotelId": "api-test-hotel"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Room number 101 already exists in hotel api-test-hotel"));

        // Service was called, but no success DTO should be created after the exception
        verify(roomService).createRoom(101, RoomType.DOUBLE,
                new BigDecimal("120.00"), true, "room.jpg", "Nice room", "api-test-hotel");
        verify(roomMapper, never()).toDto(any());
    }
}