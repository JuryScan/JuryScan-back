package unicap.juryscan.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;
}
