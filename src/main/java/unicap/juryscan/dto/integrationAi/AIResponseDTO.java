package unicap.juryscan.dto.integrationAi;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AIResponseDTO {
    private String status;
    private String message;
    private AIResponseResultDTO result;
}

