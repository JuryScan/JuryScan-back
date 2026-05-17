package unicap.juryscan.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import unicap.juryscan.dto.reCaptcha.RecaptchaResponse;
import unicap.juryscan.exception.RecaptchaException;
import unicap.juryscan.service.reCaptcha.RecaptchaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(properties = "google.recaptcha.secret=test_secret_key")
@ActiveProfiles("test")
public class RecaptchaServiceIntegrationTest {

    @Autowired
    private RecaptchaService recaptchaService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void shouldValidateTokenSuccessfully() {
        RecaptchaResponse mockResponse = new RecaptchaResponse();
        mockResponse.setSuccess(true);

        Mockito.when(restTemplate.postForObject(anyString(), any(), eq(RecaptchaResponse.class)))
                .thenReturn(mockResponse);

        assertDoesNotThrow(() -> recaptchaService.isValid("valid_token"));
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        RecaptchaResponse mockResponse = new RecaptchaResponse();
        mockResponse.setSuccess(false);

        Mockito.when(restTemplate.postForObject(anyString(), any(), eq(RecaptchaResponse.class)))
                .thenReturn(mockResponse);

        assertThrows(RecaptchaException.class, () -> recaptchaService.isValid("invalid_token"));
    }

    @Test
    void shouldThrowExceptionWhenResponseIsNull() {
        Mockito.when(restTemplate.postForObject(anyString(), any(), eq(RecaptchaResponse.class)))
                .thenReturn(null);

        assertThrows(RecaptchaException.class, () -> recaptchaService.isValid("null_token"));
    }
}