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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* REST API Integration Test Class
   PURPOSE: Verify that the RoomApiController correctly handles REST requests,
            returns proper HTTP status codes, and respects Spring Security rules */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// Insert fresh API test data before every test
@Sql(scripts = "/sql/room-api-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
// Remove test data after every test
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RoomApiControllerTest {

    // Used to simulate HTTP requests without starting a real server
    @Autowired
    private MockMvc mockMvc;

    /* PURPOSE: Verify the GET-all API contract with JSON room fields
       EXPECTATION: HTTP 200 with a JSON array */
    @Test
    void shouldGetAllRooms() throws Exception {
        mockMvc.perform(get("/api/rooms")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value(101))
                .andExpect(jsonPath("$[0].hotelName").value("API Test Hotel"));
    }

    /* PURPOSE: Verify anonymous API writes do not redirect to login
       EXPECTATION: HTTP 403 */
    @Test
    void anonymousApiRequestShouldReturnForbiddenInsteadOfRedirectingToLogin() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    /* PURPOSE: Verify malformed path ids are handled as client errors
       EXPECTATION: HTTP 400 JSON instead of HTTP 500 */
    @Test
    void invalidRoomIdShouldReturnBadRequestAsJson() throws Exception {
        mockMvc.perform(get("/api/rooms/not-a-number")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    /* PURPOSE: Verify that an ADMIN user may update a room description
       ENDPOINT: PATCH /api/rooms/1/description
       SECURITY RULE: Only ADMIN may modify room records
       NOTE: CSRF token is required because this is a state-changing request
   */
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldUpdateDescription() throws Exception {
        mockMvc.perform(patch("/api/rooms/1/description")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "description":"Updated by test"
                            }
                        """))
                .andExpect(status().isNoContent());
    }

    /* PURPOSE: Verify that a normal USER cannot update a room description
       ENDPOINT: PATCH /api/rooms/1/description
       SECURITY RULE: USER role is forbidden from performing admin-only updates
       NOTE: CSRF token is included so the failure is caused by authorization, not by missing CSRF protection */
    @Test
    @WithMockUser(roles = "STAFF")
    void userShouldNotUpdateDescription() throws Exception {
        mockMvc.perform(patch("/api/rooms/1/description")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "description":"Blocked"
                            }
                        """))
                .andExpect(status().isForbidden());
    }
}