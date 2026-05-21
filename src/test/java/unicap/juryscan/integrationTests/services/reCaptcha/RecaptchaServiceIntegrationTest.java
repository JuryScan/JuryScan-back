package unicap.juryscan.integrationTests.services.reCaptcha;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import unicap.juryscan.exception.RecaptchaException;
import unicap.juryscan.service.reCaptcha.RecaptchaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest(classes = {RecaptchaService.class, RecaptchaServiceIntegrationTest.LocalRestConfig.class}, properties = {
        "google.recaptcha.secret=test_secret_key"
})
@ActiveProfiles("test")
public class RecaptchaServiceIntegrationTest {


    @TestConfiguration
    static class LocalRestConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @Autowired
    private RecaptchaService recaptchaService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        String jsonResponse = "{\"success\": true}";

        mockServer.expect(requestTo(containsString("https://www.google.com/recaptcha/api/siteverify")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        assertDoesNotThrow(() -> recaptchaService.isValid("valid_token"));
        mockServer.verify();
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        String jsonResponse = "{\"success\": false}";

        mockServer.expect(requestTo(containsString("https://www.google.com/recaptcha/api/siteverify")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        assertThrows(RecaptchaException.class, () -> recaptchaService.isValid("invalid_token"));
        mockServer.verify();
    }
}