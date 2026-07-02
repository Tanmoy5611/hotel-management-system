package be.kdg.prog5.hotels.webapi.controller;

import be.kdg.prog5.hotels.business.ai.AiChatService;
import be.kdg.prog5.hotels.webapi.controller.ai.AiChatApiController;
import be.kdg.prog5.hotels.webapi.dto.ai.AiChatResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRoomSuggestionDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiSearchFiltersDto;
import be.kdg.prog5.hotels.webapi.exception.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiChatApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class AiChatApiControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiChatService aiChatService;

    @Test
    void chatShouldReturnRankedRoomSuggestions() throws Exception {
        when(aiChatService.findRooms("cheap spa hotel in Antwerp")).thenReturn(new AiChatResponseDto(
                "I found 1 good rooms for you.",
                new AiSearchFiltersDto("Antwerp", new BigDecimal("150"), null, true, null, null),
                List.of(new AiRoomSuggestionDto(
                        12L,
                        "hilton-antwerp",
                        "Hilton Antwerp",
                        204,
                        "Antwerp",
                        "DOUBLE",
                        new BigDecimal("135.00"),
                        0.91,
                        "spa hotel, within budget"
                ))
        ));

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "cheap spa hotel in Antwerp"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("I found 1 good rooms for you."))
                .andExpect(jsonPath("$.filters.city").value("Antwerp"))
                .andExpect(jsonPath("$.rooms[0].roomId").value(12))
                .andExpect(jsonPath("$.rooms[0].score").value(0.91));

        verify(aiChatService).findRooms("cheap spa hotel in Antwerp");
    }

    @Test
    void chatShouldRejectBlankMessage() throws Exception {
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(aiChatService);
    }
}