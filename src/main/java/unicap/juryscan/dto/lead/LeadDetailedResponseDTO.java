package unicap.juryscan.dto.lead;

import lombok.*;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.enums.StatusLeadEnum;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LeadDetailedResponseDTO {
    private UUID id;
    private StatusLeadEnum status;
    private Integer custoCreditos;
    private Timestamp dataCriacao;
    private Timestamp dataAquisicao;

    // Dados completos do cliente
    private UUID clienteId;
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String cpf;
    private Date dataNascimento;

    // Análise completa
    private AnalysisResponseDTO analise;
}

