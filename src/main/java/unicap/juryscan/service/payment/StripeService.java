package unicap.juryscan.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import unicap.juryscan.dto.payment.ProductRequest;
import unicap.juryscan.dto.payment.StripeResponse;

import java.util.UUID;

@Service
public class StripeService implements IStripeService{

    // stripe - API (payload)
    // productName, amount, quantity, currency
    // returns -> sessionId and url

    @Value("${stripe.api.key}")
    private String apiKey;

    public StripeResponse checkoutProducts(ProductRequest productRequest, UUID userId){
        Stripe.apiKey = apiKey;
        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(productRequest.getName())
                .build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(productRequest.getCurrency() == null ? "BRL" : productRequest.getCurrency())
                .setUnitAmount(productRequest.getAmount())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(productRequest.getQuantity())
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/static/success")
                .setCancelUrl("http://localhost:3000/static/cancel")
                .setClientReferenceId(userId.toString())
                .addLineItem(lineItem)
                .build();

        Session session = null;
        //TODO adicionar global exception handler
        try {
            session = Session.create(params);
        } catch(StripeException e){
            System.out.println(e.getMessage());
        }

        return StripeResponse.builder()
                .status("SUCCESS") // hardcoded
                .message("payment successful")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
