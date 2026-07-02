package be.kdg.prog5.hotels.webapi.controller;

import be.kdg.prog5.hotels.business.ai.AiBookingService;
import be.kdg.prog5.hotels.business.security.SecurityService;
import be.kdg.prog5.hotels.webapi.controller.ai.AiBookingApiController;
import be.kdg.prog5.hotels.webapi.exception.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiBookingApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class AiBookingApiControllerUnitTest {

    // MockMvc calls the controller without starting the full web server
    @Autowired
    private MockMvc mockMvc;

    // Booking service is mocked because this test only checks controller routing
    @MockBean
    private AiBookingService aiBookingService;

    // Security service is mocked so the session response is deterministic
    @MockBean
    private SecurityService securityService;

    @Test
    void sessionShouldReturnFalseForAnonymousVisitor() throws Exception {
        // Arrange
        when(securityService.isCustomer()).thenReturn(false);

        // Act + Assert
        mockMvc.perform(get("/api/ai/bookings/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value(false));

        verify(securityService).isCustomer();
        verifyNoInteractions(aiBookingService);
    }

    @Test
    void sessionShouldReturnTrueForCustomerVisitor() throws Exception {
        // Arrange
        when(securityService.isCustomer()).thenReturn(true);

        // Act + Assert
        mockMvc.perform(get("/api/ai/bookings/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value(true));

        verify(securityService).isCustomer();
        verifyNoInteractions(aiBookingService);
    }
}