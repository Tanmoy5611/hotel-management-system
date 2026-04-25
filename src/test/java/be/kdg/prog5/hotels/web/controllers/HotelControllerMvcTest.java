package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.data.SpringDataActivityLogRepository;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/* MVC Integration Test Class
   PURPOSE: Verify that the HotelController correctly handles incoming HTTP requests,
   returns the expected Thymeleaf views, and places the correct data in the model */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HotelControllerMvcTest {

    // Used to simulate browser HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // Repositories cleaned before each test for isolation
    @Autowired
    private SpringDataHotelRepository hotelRepository;

    @Autowired
    private SpringDataActivityLogRepository activityLogRepository;

    @Autowired
    private SpringDataStayRepository stayRepository;

    @Autowired
    private SpringDataRoomRepository roomRepository;

    @Autowired
    private SpringDataGuestRepository guestRepository;

    @Autowired
    private SpringDataApplicationUserRepository userRepository;

    @BeforeEach
    void setup() {
        // Clean database before each MVC test so the model data is predictable
        activityLogRepository.deleteAll();
        stayRepository.deleteAll();
        roomRepository.deleteAll();
        guestRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();
    }

    /* PURPOSE: Verify that the hotels overview page loads correctly
       EXPECTATION: HTTP 200 OK, hotels.html view returned, model contains hotel list and total count */
    @Test
    void shouldLoadHotelsPage() throws Exception {
        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(view().name("hotels"))
                .andExpect(model().attributeExists("hotels"))
                .andExpect(model().attributeExists("total"));
    }

    /* PURPOSE: Verify that searching hotels by name still returns the hotels page
       EXPECTATION: HTTP 200 OK, hotels.html view returned, filtered hotels list exists in model */
    @Test
    void shouldSearchHotelsByName() throws Exception {
        mockMvc.perform(get("/hotels")
                        .param("name", "Grand"))
                .andExpect(status().isOk())
                .andExpect(view().name("hotels"))
                .andExpect(model().attributeExists("hotels"));
    }

    /* PURPOSE: Verify that a specific hotel detail page loads correctly
     BUSINESS CASE: User clicks one hotel from the list and opens detail page
     EXPECTATION: HTTP 200 OK, hotel-detail.html view returned, model contains hotel and related page data */
    @Test
    void shouldLoadHotelDetailsPage() throws Exception {
        // Arrange
        hotelRepository.saveAndFlush(new Hotel(
                "mvc-test-hotel",
                "MVC Test Hotel",
                "Antwerp",
                "Belgium",
                LocalDate.of(2020, 1, 1),
                4,
                true,
                "/images/hotels/test.jpg",
                "Hotel used for MVC detail page test."
        ));

        // Act + Assert
        mockMvc.perform(get("/hotels/{hotelId}", "mvc-test-hotel"))
                .andExpect(status().isOk())
                .andExpect(view().name("hotel-detail"))
                .andExpect(model().attributeExists("hotel"))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attributeExists("guestsPerRoom"))
                .andExpect(model().attributeExists("totalGuests"));
    }
}
