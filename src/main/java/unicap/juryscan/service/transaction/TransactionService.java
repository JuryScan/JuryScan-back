package unicap.juryscan.service.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.model.Transaction;
import unicap.juryscan.repository.TransactionRepository;

import java.util.UUID;

public class TransactionService implements ITransactionService{

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public PageResponse<Transaction> getTransactionsByUserId(Pageable pageable, UUID id) {
        Page<Transaction> page = transactionRepository.findAllByUsuarioId(id, pageable);
        PageResponse<Transaction> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setItems(page.getContent());
        pageResponse.setPageSize(page.getSize());

        return pageResponse;
    }
}
