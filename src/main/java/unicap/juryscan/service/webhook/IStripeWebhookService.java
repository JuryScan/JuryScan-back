package unicap.juryscan.service.webhook;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;

public interface IStripeWebhookService {

    Event constructEvent(String payload, String sigHeader) throws SignatureVerificationException;

    void handleCheckoutSessionCompleted(Event event);
}
