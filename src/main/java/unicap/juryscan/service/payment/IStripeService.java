package unicap.juryscan.service.payment;


import unicap.juryscan.dto.payment.ProductRequest;
import unicap.juryscan.dto.payment.StripeResponse;

public interface IStripeService {
    public StripeResponse checkoutProducts(ProductRequest productRequest);
}
