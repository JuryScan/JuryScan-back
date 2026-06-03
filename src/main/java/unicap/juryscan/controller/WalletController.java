package unicap.juryscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unicap.juryscan.infra.ApiResponse;
import unicap.juryscan.service.wallet.IWalletService;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/wallets")
public class WalletController {

    private final IWalletService walletService;

    public WalletController(IWalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<ApiResponse> getBalance(@PathVariable UUID userId) {
        Integer balance = walletService.getBalance(userId);
        ApiResponse response = new ApiResponse(true, "Saldo encontrado com sucesso", balance, 200);
        return ResponseEntity.status(200).body(response);
    }
}
