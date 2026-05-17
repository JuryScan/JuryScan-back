package unicap.juryscan.services;

import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import unicap.juryscan.dto.payment.ProductRequest;
import unicap.juryscan.dto.payment.StripeResponse;
import unicap.juryscan.service.payment.StripeService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
public class StripeServiceIntegrationTest {

    @Autowired
    private StripeService stripeService;

    @Test
    void shouldCreateCheckoutSessionSuccessfully() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Credits Package");
        request.setAmount(5000L);
        request.setQuantity(1L);

        UUID userId = UUID.randomUUID();

        Session mockedSession = Mockito.mock(Session.class);
        Mockito.when(mockedSession.getId()).thenReturn("cs_test_123");
        Mockito.when(mockedSession.getUrl()).thenReturn("https://checkout.stripe.com/pay/cs_test_123");

        try (MockedStatic<Session> sessionMockedStatic = Mockito.mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.create(any(com.stripe.param.checkout.SessionCreateParams.class)))
                    .thenReturn(mockedSession);

            StripeResponse response = stripeService.checkoutProducts(request, userId);

            assertNotNull(response);
            assertEquals("SUCCESS", response.getStatus());
            assertEquals("payment successful", response.getMessage());
            assertEquals("cs_test_123", response.getSessionId());
            assertEquals("https://checkout.stripe.com/pay/cs_test_123", response.getSessionUrl());
        }
    }
}