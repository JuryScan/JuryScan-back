package unicap.juryscan.dto.analysis;

import lombok.*;

import java.sql.Timestamp;
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
}
