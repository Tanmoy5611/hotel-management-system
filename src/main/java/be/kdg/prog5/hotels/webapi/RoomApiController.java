package be.kdg.prog5.hotels.webapi;

import be.kdg.prog5.hotels.business.RoomService;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.webapi.dto.RoomDto;
import be.kdg.prog5.hotels.webapi.mapper.RoomMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomApiController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    public RoomApiController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    // GET all rooms
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(
                rooms.stream()
                        .map(roomMapper::toDto)
                        .toList()
        );
    }

    // GET one room
    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return ResponseEntity.ok(roomMapper.toDto(room));
    }

    // DELETE one room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}