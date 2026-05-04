package unicap.juryscan.service.payment;


import com.stripe.exception.StripeException;
import unicap.juryscan.dto.payment.ProductRequest;
import unicap.juryscan.dto.payment.StripeResponse;

import java.util.UUID;

public interface IStripeService {
    StripeResponse checkoutProducts(ProductRequest productRequest, UUID clientId) throws StripeException;
}
