package be.kdg.prog5.hotels.infrastructure.ai;

import be.kdg.prog5.hotels.business.ai.AiServiceUnavailableException;
import be.kdg.prog5.hotels.webapi.dto.ai.AiChatPythonRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRecommendationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(PythonAiClient.class)
class PythonAiClientUnitTest {

    @Autowired
    private PythonAiClient pythonAiClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void getRecommendationsShouldMapPythonResponse() {
        server.expect(requestTo("http://localhost:8001/ai/recommendations"))
                .andRespond(withSuccess("""
                        {
                          "recommendations": [
                            {
                              "roomId": 12,
                              "hotelId": "hilton-antwerp",
                              "hotelName": "Hilton Antwerp",
                              "roomNumber": 204,
                              "city": "Antwerp",
                              "roomType": "DOUBLE",
                              "pricePerNight": 135.00,
                              "score": 0.94,
                              "reason": "Matches your preferred city"
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        var response = pythonAiClient.getRecommendations(new AiRecommendationRequestDto(null, List.of(), List.of()));

        assertThat(response.recommendations()).hasSize(1);
        assertThat(response.recommendations().getFirst().hotelName()).isEqualTo("Hilton Antwerp");
    }

    @Test
    void chatShouldThrowDomainExceptionWhenPythonFails() {
        server.expect(requestTo("http://localhost:8001/ai/chat"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> pythonAiClient.chat(new AiChatPythonRequestDto("hello", List.of())))
                .isInstanceOf(AiServiceUnavailableException.class)
                .hasMessage("The AI room finder service is currently unavailable.");
    }
}