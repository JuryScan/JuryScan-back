package unicap.juryscan.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.transaction.TransactionResponseDTO;
import unicap.juryscan.infra.ApiResponse;
import unicap.juryscan.model.Transaction;
import unicap.juryscan.service.transaction.ITransactionService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.uri}/transactions")
public class TransactionController {

    private final ITransactionService transactionService;

    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getTransactionsByUser(
            @PathVariable UUID userId,
            @RequestParam("page") int page,
            @RequestParam("page_size") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        PageResponse<Transaction> result = transactionService.getTransactionsByUserId(pageable, userId);

        List<TransactionResponseDTO> items = result.getItems().stream()
                .map(t -> TransactionResponseDTO.builder()
                        .id(t.getId())
                        .tipoTransacao(t.getTipoTransacao())
                        .quantidade(t.getQuantidade())
                        .stripeCheckoutId(t.getStripeCheckoutId())
                        .dataCriacao(t.getDataCriacao())
                        .build())
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            ApiResponse response = new ApiResponse(true, "Nenhuma transação encontrada", 204);
            return ResponseEntity.status(204).body(response);
        }

        PageResponse<TransactionResponseDTO> dto = new PageResponse<>();
        dto.setItems(items);
        dto.setPage(result.getPage());
        dto.setPageSize(result.getPageSize());
        dto.setTotalElements(result.getTotalElements());
        dto.setTotalPages(result.getTotalPages());

        ApiResponse response = new ApiResponse(true, "Transações encontradas com sucesso", dto, 200);
        return ResponseEntity.status(200).body(response);
    }
}
