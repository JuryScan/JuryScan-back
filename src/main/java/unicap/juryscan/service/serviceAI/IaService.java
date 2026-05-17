package unicap.juryscan.serviceAI;

import unicap.juryscan.dto.ai.AIResponseDTO;


public interface IaService {
    IAResponseDTO processInput(byte[] request);
}