package unicap.juryscan.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LoginResponseDTO {
    private String token;
    private String message;
    private boolean success;
    private int status;
    private UserAuthenticatedDTO user;
}
