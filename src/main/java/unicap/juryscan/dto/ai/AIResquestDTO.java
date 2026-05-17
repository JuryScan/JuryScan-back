package unicap.juryscan.dto.ai;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class AIRequestDTO {
    private byte[] base64;
}