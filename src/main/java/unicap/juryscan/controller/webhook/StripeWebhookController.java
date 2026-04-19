package unicap.juryscan.controller.webhook;

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

    //TODO implementar handler
    @PostMapping("/")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader){

        return ResponseEntity.ok().build();
    }
}
