package unicap.juryscan.dto.integrationAi;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AIRequestDTO {
    private byte[] base64;
}