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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// Uses a real Spring context to verify the Hotel REST contract
@Sql(scripts = "/sql/room-api-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class HotelApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /* PURPOSE: Verify Hotel responses are JSON DTOs without entity relations
       EXPECTATION: HTTP 200 and no rooms collection in the response */
    @Test
    void shouldGetAllHotelsAsJson() throws Exception {
        mockMvc.perform(get("/api/hotels").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].hotelId").value("api-test-hotel"))
                .andExpect(jsonPath("$[0].name").value("API Test Hotel"))
                .andExpect(jsonPath("$[0].hasSpa").value(false))
                .andExpect(jsonPath("$[0].rooms").doesNotExist());
    }

    /* PURPOSE: Verify missing hotels use the API error format
      EXPECTATION: HTTP 404 with a JSON status field */
    @Test
    void shouldReturnNotFoundForAnUnknownHotel() throws Exception {
        mockMvc.perform(get("/api/hotels/unknown-hotel").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    /* PURPOSE: Verify an admin can create a hotel from JSON
       EXPECTATION: HTTP 201 with the generated hotel id and Location header */
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldCreateHotel() throws Exception {
        mockMvc.perform(post("/api/hotels")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "New API Hotel",
                                  "city": "Ghent",
                                  "country": "Belgium",
                                  "openedOn": "2024-06-01",
                                  "stars": 4,
                                  "hasSpa": true,
                                  "description": "Created through the Hotel API"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/api/hotels/new-api-hotel-ghent-belgium"))
                .andExpect(jsonPath("$.hotelId").value("new-api-hotel-ghent-belgium"))
                .andExpect(jsonPath("$.stars").value(4));
    }

    /* PURPOSE: Verify an admin can update one hotel field
       EXPECTATION: HTTP 204 after a valid description PATCH */
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldUpdateHotelDescription() throws Exception {
        mockMvc.perform(patch("/api/hotels/api-test-hotel/description")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated by the API\"}"))
                .andExpect(status().isNoContent());
    }

    /* PURPOSE: Verify an admin can delete a hotel
      EXPECTATION: HTTP 204 */
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldDeleteHotel() throws Exception {
        mockMvc.perform(delete("/api/hotels/api-test-hotel").with(csrf()))
                .andExpect(status().isNoContent());
    }
}