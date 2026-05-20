package unicap.juryscan.service.reCaptcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import unicap.juryscan.dto.reCaptcha.RecaptchaResponse;
import unicap.juryscan.exception.RecaptchaException;

@Service
public class RecaptchaService implements IRecaptchaService {

    @Value("${google.recaptcha.secret}")
    private String secretKey;

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private final RestTemplate restTemplate;

    public RecaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void isValid(String token){

        String url = String.format(GOOGLE_RECAPTCHA_VERIFY_URL, secretKey, token);

        RecaptchaResponse response = restTemplate.postForObject(url, null, RecaptchaResponse.class);
        if (response == null || !response.isSuccess()) throw new RecaptchaException("Falha na validação do reCAPTCHA. Por favor, tente novamente.");
    }
}