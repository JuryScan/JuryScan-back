package unicap.juryscan.service.serviceAI;

import unicap.juryscan.dto.integrationAi.AIResponseDTO;

public interface IGenericAIService {

    AIResponseDTO analyzeDocument(byte[] documentBytes);
}
