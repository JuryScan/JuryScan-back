package unicap.juryscan.service.transaction;

import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.model.Transaction;

import java.util.UUID;

public interface ITransactionService {

    PageResponse<Transaction> getTransactionsByUserId(Pageable pageable, UUID id);
}
