package unicap.juryscan.dto.userAdvogado;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserAdvogadoRegisteredDTO {
    private String token;
    private UserAdvogadoResponseDTO userAdvogado;
}
