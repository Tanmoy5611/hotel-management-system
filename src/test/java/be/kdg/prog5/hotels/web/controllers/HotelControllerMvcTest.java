package be.kdg.prog5.hotels.web.controllers;

import be.kdg.prog5.hotels.data.SpringDataActivityLogRepository;
import be.kdg.prog5.hotels.data.SpringDataApplicationUserRepository;
import be.kdg.prog5.hotels.data.SpringDataGuestRepository;
import be.kdg.prog5.hotels.data.SpringDataHotelRepository;
import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.data.SpringDataStayRepository;
import be.kdg.prog5.hotels.domain.ActivityType;
import be.kdg.prog5.hotels.domain.Hotel;
import be.kdg.prog5.hotels.domain.ApplicationUser;
import be.kdg.prog5.hotels.domain.Guest;
import be.kdg.prog5.hotels.domain.RoleType;
import be.kdg.prog5.hotels.domain.Room;
import be.kdg.prog5.hotels.domain.RoomType;
import be.kdg.prog5.hotels.domain.Stay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    /* PURPOSE: Verify that the admin bookings overview page loads correctly
       BUSINESS CASE: Administrator opens the booking management screen
       EXPECTATION: HTTP 200 OK, admin-bookings.html view returned, active bookings shown with CSRF protection */
    @Test
    @WithMockUser(username = "admin@hotelapp.com", roles = "ADMIN")
    void adminShouldLoadBookingsPage() throws Exception {
        createBooking();

        mockMvc.perform(get("/admin/bookings"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-bookings"))
                .andExpect(model().attributeExists("bookings"))
                .andExpect(content().string(containsString("Booking Guest")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    /* PURPOSE: Verify that past bookings are hidden from the active bookings overview
       BUSINESS CASE: Administrator should only see current/upcoming bookings
       EXPECTATION: Active booking is visible, past booking is excluded from the page */
    @Test
    @WithMockUser(username = "admin@hotelapp.com", roles = "ADMIN")
    void adminBookingsPageShouldHidePastBookings() throws Exception {
        createBooking();
        createPastBooking();

        mockMvc.perform(get("/admin/bookings"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Booking Guest")))
                .andExpect(content().string(not(containsString("Past Guest"))));
    }

    /* PURPOSE: Verify that an administrator may cancel a booking successfully
       BUSINESS CASE: Admin removes an existing stay from the system
       EXPECTATION: Booking deleted from database, redirect returned, activity log created */
    @Test
    @WithMockUser(username = "admin@hotelapp.com", roles = "ADMIN")
    void adminShouldCancelBooking() throws Exception {
        Long bookingId = createBooking();

        mockMvc.perform(post("/admin/bookings/{stayId}/cancel", bookingId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/bookings"));

        assertThat(stayRepository.findById(bookingId)).isEmpty();
        assertThat(activityLogRepository.findAll())
                .anyMatch(log -> log.getAction() == ActivityType.DELETE_BOOKING
                        && log.getDescription().contains("Booking Guest"));
    }

    /* PURPOSE: Verify that cancelling a booking creates a visible activity log entry
      BUSINESS CASE: Admin dashboard should track important booking management actions
      EXPECTATION: Activity dashboard displays DELETE_BOOKING action with correct booking details */
    @Test
    @WithMockUser(username = "admin@hotelapp.com", roles = "ADMIN")
    void adminDashboardShouldShowCancelledBookingActivity() throws Exception {
        Long bookingId = createBooking();

        mockMvc.perform(post("/admin/bookings/{stayId}/cancel", bookingId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/admin/activity"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("DELETE BOOKING")))
                .andExpect(content().string(containsString("Booking for Booking Guest")));
    }

    private Long createBooking() {
        ApplicationUser admin = userRepository.save(new ApplicationUser(
                "admin@hotelapp.com",
                "encoded-password",
                RoleType.ADMIN
        ));

        Hotel hotel = hotelRepository.save(new Hotel(
                "booking-test-hotel",
                "Booking Test Hotel",
                "Brussels",
                "Belgium",
                LocalDate.of(2021, 5, 1),
                5,
                true,
                "/images/hotels/test.jpg",
                "Hotel used for booking MVC tests."
        ));

        Room room = new Room(
                701,
                RoomType.SUITE,
                BigDecimal.valueOf(250),
                true,
                "/images/rooms/test.jpg",
                "Room used for booking MVC tests."
        );
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);

        Guest guest = new Guest(
                "Booking Guest",
                LocalDate.of(1995, 6, 15),
                "booking.guest@test.com",
                "/images/guests/guest.jpg"
        );
        guest.setOwner(admin);
        Guest savedGuest = guestRepository.save(guest);

        savedRoom.addGuest(
                savedGuest,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(13)
        );

        Room roomWithStay = roomRepository.saveAndFlush(savedRoom);

        return roomWithStay.getStays().iterator().next().getId();
    }

    private void createPastBooking() {
        ApplicationUser admin = userRepository.findByEmail("admin@hotelapp.com")
                .orElseGet(() -> userRepository.save(new ApplicationUser(
                        "admin@hotelapp.com",
                        "encoded-password",
                        RoleType.ADMIN
                )));

        Hotel hotel = hotelRepository.save(new Hotel(
                "past-booking-test-hotel",
                "Past Booking Test Hotel",
                "Ghent",
                "Belgium",
                LocalDate.of(2021, 5, 1),
                5,
                true,
                "/images/hotels/test.jpg",
                "Hotel used for past booking MVC tests."
        ));

        Room room = new Room(
                702,
                RoomType.DOUBLE,
                BigDecimal.valueOf(150),
                false,
                "/images/rooms/test.jpg",
                "Room used for past booking MVC tests."
        );
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);

        Guest guest = new Guest(
                "Past Guest",
                LocalDate.of(1992, 3, 20),
                "past.guest@test.com",
                "/images/guests/guest.jpg"
        );
        guest.setOwner(admin);
        Guest savedGuest = guestRepository.save(guest);

        stayRepository.saveAndFlush(new Stay(
                savedRoom,
                savedGuest,
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(7)
        ));
    }
}