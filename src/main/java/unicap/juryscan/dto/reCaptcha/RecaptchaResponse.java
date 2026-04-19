package unicap.juryscan.dto.reCaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RecaptchaResponse {
    private boolean success;
    private String challenge_ts;
    private String hostname;

    // O Google reCAPTCHA pode retornar uma lista de códigos de erro
    @JsonProperty("error-codes")
    List<String> errorCodes;
}