package unicap.juryscan.dto.transaction;

import lombok.*;
import unicap.juryscan.enums.TipoTransacaoEnum;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionResponseDTO {
    private UUID id;
    private TipoTransacaoEnum tipoTransacao;
    private Integer quantidade;
    private String stripeCheckoutId;
    private Timestamp dataCriacao;
}
