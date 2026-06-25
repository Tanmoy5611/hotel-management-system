package be.kdg.prog5.hotels.webapi.controller;

import be.kdg.prog5.hotels.business.hotel.HotelService;
import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.webapi.dto.HotelDto;
import be.kdg.prog5.hotels.webapi.dto.NewHotelDto;
import be.kdg.prog5.hotels.webapi.dto.UpdateHotelDescriptionDto;
import be.kdg.prog5.hotels.webapi.mapper.HotelMapper;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
// Handles HTTP details while HotelService keeps business rules
public class HotelApiController {

    private final HotelService hotelService;
    private final HotelMapper hotelMapper;

    public HotelApiController(HotelService hotelService, HotelMapper hotelMapper) {
        this.hotelService = hotelService;
        this.hotelMapper = hotelMapper;
    }

    // returns hotels matching the optional name or city query
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        // Map entities before sending JSON to the client
        List<HotelDto> hotels = hotelService.getAllHotels().stream()
                .map(hotelMapper::toDto)
                .toList();

        if (hotels.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(hotels);
    }

    // GET one hotel by id
    @GetMapping(value = "/{hotelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HotelDto> getHotel(@PathVariable String hotelId) {
        Hotel hotel = hotelService.getHotelByHotelId(hotelId);
        return ResponseEntity.ok(hotelMapper.toDto(hotel));
    }

    // creates a hotel by admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HotelDto> createHotel(@RequestBody @Valid NewHotelDto newHotelDto) {
        // The service generates the business hotel id and saves the entity
        Hotel hotel = hotelService.createHotel(
                newHotelDto.getName(),
                newHotelDto.getCity(),
                newHotelDto.getCountry(),
                newHotelDto.getOpenedOn(),
                newHotelDto.getStars(),
                newHotelDto.getHasSpa(),
                newHotelDto.getImageUrl(),
                newHotelDto.getDescription()
        );

        HotelDto dto = hotelMapper.toDto(hotel);
        return ResponseEntity
                .created(URI.create("/api/hotels/" + dto.getHotelId()))
                .body(dto);
    }

    // Update hotel description by admin
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{hotelId}/description", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateDescription(@PathVariable String hotelId,
                                                  @RequestBody @Valid UpdateHotelDescriptionDto dto) {
        // A PATCH request changes only the description field
        hotelService.updateHotelDescription(hotelId, dto.getDescription());
        return ResponseEntity.noContent().build();
    }

    // DELETE hotel by admin
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable String hotelId) {
        hotelService.deleteHotelByHotelId(hotelId);
        return ResponseEntity.noContent().build();
    }
}