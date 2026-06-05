package unicap.juryscan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Fallback de retorno do gateway. O fluxo principal ja envia o usuario direto para o frontend
 * (success_url / cancel_url do Stripe apontam para /pagamento/sucesso e /pagamento/cancelado).
 * Estas rotas seguem existindo para sessoes antigas e apenas redirecionam (302) para o frontend.
 */
@Controller
@RequestMapping("/redirect")
public class RedirectController {

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @GetMapping("/payment/success")
    public RedirectView paymentSuccess(
            @RequestParam(value = "session_id", required = false) String sessionId) {
        String suffix = sessionId != null ? "?session_id=" + sessionId : "";
        return new RedirectView(frontendUrl + "/pagamento/sucesso" + suffix);
    }

    @GetMapping("/payment/cancel")
    public RedirectView paymentCancel() {
        return new RedirectView(frontendUrl + "/pagamento/cancelado");
    }
}
