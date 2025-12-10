package unicap.juryscan.service.serviceAI;

import unicap.juryscan.dto.ai.AIResponseDTO;

public interface IGenericAIService {

    AIResponseDTO analyzeDocument(byte[] documentBytes);
}
