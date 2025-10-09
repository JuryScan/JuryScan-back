package unicap.juryscan.dto.auth;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AuthenticationDTO {
    private String email;
    private String password;
}
