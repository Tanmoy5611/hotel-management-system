package be.kdg.prog5.hotels.business.ai;

import be.kdg.prog5.hotels.business.security.SecurityService;
import be.kdg.prog5.hotels.data.SpringDataCustomerRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.Customer;
import be.kdg.prog5.hotels.domain.Stay;
import be.kdg.prog5.hotels.infrastructure.ai.PythonAiClient;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRecommendationRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRecommendationResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRoomFeatureDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AiRecommendationServiceImpl implements AiRecommendationService {

    // Repositories stay in Spring because Python should not read the application database directly
    private final SpringDataCustomerRepository customerRepository;
    private final SpringDataStayRepository stayRepository;
    private final SpringDataRoomRepository roomRepository;

    // Security decides whose booking history may be used for personalization
    private final SecurityService securityService;

    // Mapper converts domain entities into stable ML feature DTOs
    private final AiDataMapper aiDataMapper;

    // Client owns the HTTP boundary to the Python model service
    private final PythonAiClient pythonAiClient;

    public AiRecommendationServiceImpl(SpringDataCustomerRepository customerRepository,
                                       SpringDataStayRepository stayRepository,
                                       SpringDataRoomRepository roomRepository,
                                       SecurityService securityService,
                                       AiDataMapper aiDataMapper,
                                       PythonAiClient pythonAiClient) {
        this.customerRepository = customerRepository;
        this.stayRepository = stayRepository;
        this.roomRepository = roomRepository;
        this.securityService = securityService;
        this.aiDataMapper = aiDataMapper;
        this.pythonAiClient = pythonAiClient;
    }

    @Override
    public AiRecommendationResponseDto getRecommendationsForCurrentCustomer() {
        // The current customer is optional because guests without login should not break the endpoint
        Customer customer = findCurrentCustomer();

        // Past bookings are the personalization signal for the recommendation model
        List<AiRoomFeatureDto> pastBookings = customer == null
                ? List.of()
                : stayRepository.findByGuestIdWithDetails(customer.getProfile().getId())
                .stream()
                .map(aiDataMapper::toPastBooking)
                .toList();

        // No booking history means no personal ML signal, so return empty instead of fake recommendations
        if (pastBookings.isEmpty()) {
            return new AiRecommendationResponseDto(List.of());
        }

        // Candidate rooms are all current rooms the model can rank for this customer
        List<AiRoomFeatureDto> candidateRooms = roomRepository.findAllWithHotel()
                .stream()
                .map(aiDataMapper::toCandidateRoom)
                .toList();

        // Spring prepares clean data, Python performs scoring and explanations
        return pythonAiClient.getRecommendations(new AiRecommendationRequestDto(
                customer == null ? null : customer.getId(),
                pastBookings,
                candidateRooms
        ));
    }

    private Customer findCurrentCustomer() {
        // SecurityService hides framework-specific authentication details from AI code
        String email = securityService.getLoggedInUsername();
        if (email == null || !securityService.isCustomer()) {
            return null;
        }

        return customerRepository.findByProfileEmail(email).orElse(null);
    }
}