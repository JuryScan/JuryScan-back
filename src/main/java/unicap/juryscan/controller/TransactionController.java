package unicap.juryscan.controller;

import org.springframework.web.bind.annotation.RestController;
import unicap.juryscan.service.transaction.TransactionService;

//TODO implementar controller
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
