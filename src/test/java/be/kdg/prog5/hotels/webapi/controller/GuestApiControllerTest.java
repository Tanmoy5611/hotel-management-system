package be.kdg.prog5.hotels.webapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// Uses guest fixtures with an owner required by the domain model
@Sql(scripts = "/sql/guest-api-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/guest-api-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GuestApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /* PURPOSE: Verify Guest responses expose DTO fields only
       EXPECTATION: HTTP 200 without owner or stays relations */
    @Test
    void shouldGetGuestsAsJson() throws Exception {
        mockMvc.perform(get("/api/guests").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fullName").value("API Test Guest"))
                .andExpect(jsonPath("$[0].owner").doesNotExist())
                .andExpect(jsonPath("$[0].stays").doesNotExist());
    }

    /* PURPOSE: Verify one guest can be read through the API
       EXPECTATION: HTTP 200 with the stored email */
    @Test
    void shouldGetOneGuestAsJson() throws Exception {
        mockMvc.perform(get("/api/guests/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("guest@example.com"));
    }

    /* PURPOSE: Verify the separate client can create a guest from JSON
     EXPECTATION: HTTP 201 without login or CSRF */
    @Test
    void clientShouldCreateGuestFromDto() throws Exception {
        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "New API Guest",
                                  "dob": "1992-04-10",
                                  "email": "new.guest@example.com",
                                  "discountPercentage": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/guests/2"))
                .andExpect(jsonPath("$.fullName").value("New API Guest"))
                .andExpect(jsonPath("$.vip").value(true));
    }

    /* PURPOSE: Verify an admin can delete a guest
      EXPECTATION: HTTP 204 with a CSRF token */
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldDeleteGuest() throws Exception {
        mockMvc.perform(delete("/api/guests/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}