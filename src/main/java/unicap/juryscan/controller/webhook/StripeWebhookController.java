package unicap.juryscan.controller.webhook;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.service.webhook.StripeWebhookService;

@RestController
@RequestMapping("${api.uri}/webhook/stripe")
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    public StripeWebhookController(StripeWebhookService stripeWebhookService) {
        this.stripeWebhookService = stripeWebhookService;
    }

    @PostMapping("/checkout-success")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws SignatureVerificationException {

        Event event = stripeWebhookService.constructEvent(payload, sigHeader);
        // Processar eventos específicos, por exemplo, checkout.session.completed
        stripeWebhookService.handleCheckoutSessionCompleted(event);

        return ResponseEntity.ok("Webhook processado com sucesso");
    }
}
