package be.kdg.prog5.hotels.business.ai;

import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingActionResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingCancelRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingConfirmRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingQuoteRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingQuoteResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingSummaryDto;

import java.util.List;

// Business boundary for chatbot booking and cancellation flows
public interface AiBookingService {
    // Checks dates, room availability, and price before the user confirms
    AiBookingQuoteResponseDto quote(AiBookingQuoteRequestDto request);

    // Creates a booking after rechecking availability in the domain layer
    AiBookingActionResponseDto confirm(AiBookingConfirmRequestDto request);

    // Lists current customer bookings so the bot can offer cancel choices
    List<AiBookingSummaryDto> currentCustomerBookings();

    // Cancels only a booking owned by the current customer
    AiBookingActionResponseDto cancel(AiBookingCancelRequestDto request);
}