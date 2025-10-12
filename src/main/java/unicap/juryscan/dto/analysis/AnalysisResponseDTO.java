package unicap.juryscan.dto.analysis;

import jakarta.annotation.security.DenyAll;
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
    private Timestamp dataCriacao;
}
