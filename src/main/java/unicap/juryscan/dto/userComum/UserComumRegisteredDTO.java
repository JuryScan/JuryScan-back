package unicap.juryscan.dto.userComum;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserComumRegisteredDTO {
    private String token;
    private UserComumResponseDTO userComum;
}
