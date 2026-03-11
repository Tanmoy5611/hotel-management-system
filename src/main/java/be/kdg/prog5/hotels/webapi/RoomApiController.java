package be.kdg.prog5.hotels.webapi;

import be.kdg.prog5.hotels.business.RoomService;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.webapi.dto.NewRoomDto;
import be.kdg.prog5.hotels.webapi.dto.RoomDto;
import be.kdg.prog5.hotels.webapi.dto.UpdateRoomDescriptionDto;
import be.kdg.prog5.hotels.webapi.mapper.RoomMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
        List<RoomDto> rooms = roomService.getAllRooms()
                .stream()
                .map(roomMapper::toDto)
                .toList();

        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rooms);
    }

    // GET one room
    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return ResponseEntity.ok(roomMapper.toDto(room));
    }

    /// DELETE one room by Admin
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    /// Create a new room by Admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoomDto> createRoom(
            @RequestBody @Valid NewRoomDto newRoomDto) {

        // DTO to Entity
        Room room = roomMapper.toEntity(newRoomDto);

        // Service handles aggregate
        Room savedRoom = roomService.createRoom(room, newRoomDto.getHotelId());

        // Entity to DTO
        RoomDto dto = roomMapper.toDto(savedRoom);

        return ResponseEntity
                .created(URI.create("/api/rooms/" + dto.getId()))
                .body(dto);
    }

    /// Update room description by admin
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/description")
    public ResponseEntity<Void> updateRoomDescription(
            @PathVariable Long id,
            @RequestBody @Valid UpdateRoomDescriptionDto dto) {

        roomService.updateRoomDescription(id, dto.getDescription());

        return ResponseEntity.noContent().build();
    }
}