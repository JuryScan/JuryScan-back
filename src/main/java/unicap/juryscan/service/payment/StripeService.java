package unicap.juryscan.service.payment;

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

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public StripeResponse checkoutProducts(ProductRequest productRequest, UUID userId) throws StripeException {
        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(productRequest.getName())
                .build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("BRL")
                .setUnitAmount(productRequest.getAmount())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(productRequest.getQuantity())
                .setPriceData(priceData)
                .build();

        // O Stripe redireciona o usuário de volta ao frontend. {CHECKOUT_SESSION_ID} é
        // substituído pela Stripe pelo id da sessão, permitindo ao front confirmar o pagamento.
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/pagamento/sucesso?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/pagamento/cancelado")
                .setClientReferenceId(userId.toString())
                .addLineItem(lineItem)
                .build();

        Session session = Session.create(params);

        return StripeResponse.builder()
                .status("SUCCESS")
                .message("payment successful")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
