package be.kdg.prog5.hotels.business.ai;

import be.kdg.prog5.hotels.data.SpringDataRoomRepository;
import be.kdg.prog5.hotels.infrastructure.ai.PythonAiClient;
import be.kdg.prog5.hotels.webapi.dto.ai.AiChatPythonRequestDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiChatResponseDto;
import be.kdg.prog5.hotels.webapi.dto.ai.AiRoomFeatureDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AiChatServiceImpl implements AiChatService {

    // Chat search needs the latest room catalog from the Spring database
    private final SpringDataRoomRepository roomRepository;

    // Domain entities are mapped before they leave the Spring boundary
    private final AiDataMapper aiDataMapper;

    // Python handles language parsing and ranking
    private final PythonAiClient pythonAiClient;

    public AiChatServiceImpl(SpringDataRoomRepository roomRepository,
                             AiDataMapper aiDataMapper,
                             PythonAiClient pythonAiClient) {
        this.roomRepository = roomRepository;
        this.aiDataMapper = aiDataMapper;
        this.pythonAiClient = pythonAiClient;
    }

    @Override
    public AiChatResponseDto findRooms(String message) {
        // Send every candidate room so Python can rank based on the user's natural-language request
        List<AiRoomFeatureDto> availableRooms = roomRepository.findAllWithHotel()
                .stream()
                .map(aiDataMapper::toCandidateRoom)
                .toList();

        // Trim at the Java boundary and validate again inside Python for defense in depth
        return pythonAiClient.chat(new AiChatPythonRequestDto(message.trim(), availableRooms));
    }
}