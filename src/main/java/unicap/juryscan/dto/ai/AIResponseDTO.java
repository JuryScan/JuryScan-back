package unicap.juryscan.dto.ai;

import lombok.*;
import unicap.juryscan.dto.failure.FailureCreateDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AIResponseDTO {
    private String status;
    private String message;
    private Object result; // alterar para classe quando definir result
}

