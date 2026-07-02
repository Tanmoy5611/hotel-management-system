package be.kdg.prog5.hotels.business.ai;

import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.Stay;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRoomFeatureDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AiDataMapper {

    public AiRoomFeatureDto toCandidateRoom(Room room) {
        // Candidate rooms do not have stay-specific features like nights or customer discount
        return toFeature(room, null, BigDecimal.ZERO);
    }

    public AiRoomFeatureDto toPastBooking(Stay stay) {
        // Past bookings include stay context so Python can learn from real customer behavior
        return toFeature(
                stay.getRoom(),
                Math.toIntExact(stay.getNumberOfNights()),
                stay.getGuest().getDiscountPercentage()
        );
    }

    private AiRoomFeatureDto toFeature(Room room, Integer nights, BigDecimal discountPercentage) {
        // This method is the single feature mapping point for both chat and recommendations
        Hotel hotel = room.getHotel();

        // Keep the DTO flat because FastAPI schemas should not depend on JPA object graphs
        return new AiRoomFeatureDto(
                room.getId(),
                hotel.getHotelId(),
                hotel.getName(),
                room.getNumber(),
                hotel.getCity(),
                hotel.getStars(),
                hotel.hasSpa(),
                room.getType().name(),
                room.getPricePerNight(),
                room.isSeaView(),
                nights,
                discountPercentage == null ? BigDecimal.ZERO : discountPercentage
        );
    }
}