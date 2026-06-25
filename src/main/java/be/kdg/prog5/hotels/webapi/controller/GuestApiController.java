package be.kdg.prog5.hotels.webapi.controller;

import be.kdg.prog5.hotels.business.guest.GuestService;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.webapi.dto.GuestDto;
import be.kdg.prog5.hotels.webapi.dto.NewGuestDto;
import be.kdg.prog5.hotels.webapi.mapper.GuestMapper;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestApiController {

    private final GuestService guestService;
    private final GuestMapper guestMapper;

    public GuestApiController(GuestService guestService, GuestMapper guestMapper) {
        this.guestService = guestService;
        this.guestMapper = guestMapper;
    }

    // Week 10 Client search endpoint: returns guests matching the optional name or email query
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GuestDto>> searchGuests(
            @RequestParam(required = false) String query) {

        List<GuestDto> guests = guestService.searchGuests(query, null)
                .stream()
                .map(guestMapper::toDto)
                .toList();

        if (guests.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(guests);
    }

    // GET one guest by id
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestDto> getGuest(@PathVariable Long id) {
        Guest guest = guestService.getGuestWithDetails(id);
        return ResponseEntity.ok(guestMapper.toDto(guest));
    }

    // Week 10 Client add endpoint: creates a guest without using Thymeleaf forms
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestDto> createGuest(
            @RequestBody @Valid NewGuestDto newGuestDto) {

        Guest savedGuest = guestService.createGuestFromClient(
                newGuestDto.getFullName(),
                newGuestDto.getDob(),
                newGuestDto.getEmail(),
                newGuestDto.getAvatarUrl(),
                newGuestDto.getDiscountPercentage()
        );

        GuestDto dto = guestMapper.toDto(savedGuest);

        return ResponseEntity
                .created(URI.create("/api/guests/" + dto.getId()))
                .body(dto);
    }

    // Week 10 Client delete endpoint: deletes a guest
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        // The service checks whether the current user owns this guest or is an admin
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }
}