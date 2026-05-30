package unicap.juryscan.dto.analysis;

import lombok.*;
import unicap.juryscan.dto.failure.FailureResponseDTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AnalysisResponseDTO {
    private UUID id;
    private String titulo;
    private String descricaoGeral;
    private String relatorioSumarioJuridico;
    private String sumario;
    private Timestamp dataCriacao;
    private List<FailureResponseDTO> falhas;
}
