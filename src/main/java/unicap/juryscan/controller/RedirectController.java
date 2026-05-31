package unicap.juryscan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/redirect")
public class RedirectController {

    @GetMapping("/payment/success")
    public String paymentSuccess(
            @RequestParam(value = "session_id", required = false) String sessionId,
            Model model) {

        // Adicionar informações ao modelo para exibir na página
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("message", "Pagamento realizado com sucesso!");

        return "payment-success";
    }
}