package unicap.juryscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unicap.juryscan.dto.payment.ProductRequest;
import unicap.juryscan.dto.payment.StripeResponse;
import unicap.juryscan.infra.ApiResponse;
import unicap.juryscan.model.User;
import unicap.juryscan.service.payment.StripeService;

@RestController
@RequestMapping("${api.uri}/product-checkout")
public class ProductCheckoutController {

    private final StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse> checkoutProducts(
            @RequestBody ProductRequest productRequest,
            @AuthenticationPrincipal User user)
    {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest, user.getId());
        ApiResponse response = new ApiResponse(true, "Checkout realizado com sucesso", stripeResponse, 200);
        return ResponseEntity.status(200).body(response);
    }
}
