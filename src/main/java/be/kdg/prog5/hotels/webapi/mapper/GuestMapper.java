package be.kdg.prog5.hotels.webapi.mapper;

import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.VIPGuest;
import be.kdg.prog5.hotels.webapi.dto.GuestDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GuestMapper {

    public GuestDto toDto(Guest guest) {
        BigDecimal discount = guest.getDiscountPercentage();

        return new GuestDto(
                guest.getId(),
                guest.getFullName(),
                guest.getDob(),
                guest.getEmail(),
                guest.getAvatarUrl(),
                discount,
                guest instanceof VIPGuest && discount.compareTo(BigDecimal.ZERO) > 0
        );
    }
}