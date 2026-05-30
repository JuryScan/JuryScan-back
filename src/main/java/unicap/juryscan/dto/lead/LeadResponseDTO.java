package unicap.juryscan.dto.lead;

import lombok.*;
import unicap.juryscan.enums.StatusLeadEnum;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LeadResponseDTO {
    private UUID id;
    private UUID usuarioClienteId;
    private String nomeCliente;
    private UUID analiseId;
    private String tituloAnalise;
    private StatusLeadEnum status;
    private Integer custoCreditos;
    private Timestamp dataCriacao;
    private Timestamp dataAquisicao;
    private UUID advogadoId;
}

