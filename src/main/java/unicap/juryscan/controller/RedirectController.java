package unicap.juryscan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Fallback de redirecionamento do gateway de pagamento.
 *
 * O Stripe já é configurado em {@code StripeService} para retornar o usuário
 * diretamente ao frontend. Estas rotas permanecem como rede de segurança para
 * sessões/links antigos, redirecionando (302) o usuário para as páginas do
 * frontend em vez de renderizar uma página própria do backend.
 */
@Controller
@RequestMapping("/redirect")
public class RedirectController {

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam(value = "session_id", required = false) String sessionId) {
        String query = sessionId != null ? "?session_id=" + sessionId : "";
        return "redirect:" + frontendUrl + "/pagamento/sucesso" + query;
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        return "redirect:" + frontendUrl + "/pagamento/cancelado";
    }
}
