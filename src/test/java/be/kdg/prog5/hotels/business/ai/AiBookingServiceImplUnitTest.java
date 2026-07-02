package be.kdg.prog5.hotels.business.ai;

import be.kdg.prog5.hotels.business.customer.CustomerService;
import be.kdg.prog5.hotels.business.security.SecurityService;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.domain.Customer;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingConfirmRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiBookingQuoteRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiBookingServiceImplUnitTest {

    @Mock
    private SpringDataRoomRepository roomRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private SecurityService securityService;

    @Mock
    private Room room;

    @Mock
    private Hotel hotel;

    @Mock
    private Customer customer;

    @Mock
    private Guest profile;

    @InjectMocks
    private AiBookingServiceImpl aiBookingService;

    @Test
    void quoteShouldReturnAvailabilityAndFinalPrice() {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate checkOut = checkIn.plusDays(3);
        givenCustomer();
        givenRoom(true);

        var quote = aiBookingService.quote(new AiBookingQuoteRequestDto(22L, null, checkIn, checkOut));

        assertThat(quote.available()).isTrue();
        assertThat(quote.nights()).isEqualTo(3);
        assertThat(quote.totalPrice()).isEqualByComparingTo("600.00");
        assertThat(quote.discountPercentage()).isEqualByComparingTo("10");
        assertThat(quote.finalPrice()).isEqualByComparingTo("540.00");
    }

    @Test
    void quoteShouldReturnUnavailableWhenRoomHasOverlap() {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate checkOut = checkIn.plusDays(3);
        givenCustomer();
        givenRoom(false);

        var quote = aiBookingService.quote(new AiBookingQuoteRequestDto(22L, null, checkIn, checkOut));

        assertThat(quote.available()).isFalse();
        assertThat(quote.message()).contains("not available");
    }

    @Test
    void confirmShouldDelegateToCustomerBookingService() {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate checkOut = checkIn.plusDays(2);
        when(securityService.isCustomer()).thenReturn(true);
        when(securityService.getLoggedInUsername()).thenReturn("customer@example.com");

        var result = aiBookingService.confirm(new AiBookingConfirmRequestDto(22L, checkIn, checkOut));

        assertThat(result.success()).isTrue();
        verify(customerService).bookOwnRoom("customer@example.com", 22L, checkIn, checkOut);
    }

    @Test
    void quoteShouldRejectAnonymousUsers() {
        when(securityService.isCustomer()).thenReturn(false);

        assertThatThrownBy(() -> aiBookingService.quote(new AiBookingQuoteRequestDto(
                22L,
                null,
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5)
        ))).isInstanceOf(AccessDeniedException.class);
    }

    private void givenCustomer() {
        when(securityService.isCustomer()).thenReturn(true);
        when(securityService.getLoggedInUsername()).thenReturn("customer@example.com");
        when(customerService.getCustomerByEmail("customer@example.com")).thenReturn(customer);
        when(customer.getProfile()).thenReturn(profile);
        when(profile.getDiscountPercentage()).thenReturn(new BigDecimal("10"));
    }

    private void givenRoom(boolean available) {
        when(roomRepository.findByIdWithHotelAndGuests(22L)).thenReturn(Optional.of(room));
        when(room.getId()).thenReturn(22L);
        when(room.getNumber()).thenReturn(450);
        when(room.getHotel()).thenReturn(hotel);
        when(room.getType()).thenReturn(RoomType.SUITE);
        when(room.getPricePerNight()).thenReturn(new BigDecimal("200.00"));
        when(room.isAvailable(LocalDate.now().plusDays(3), LocalDate.now().plusDays(6))).thenReturn(available);
        when(hotel.getName()).thenReturn("Hilton Antwerp");
        when(hotel.getCity()).thenReturn("Antwerp");
    }
}
