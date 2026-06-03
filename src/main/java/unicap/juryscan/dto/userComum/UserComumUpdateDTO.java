package unicap.juryscan.dto.userComum;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserComumUpdateDTO {
    private String nomeCompleto;
    private String telefone;
}
