package unicap.juryscan.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ponto de uma serie temporal mensal usado pelos graficos do dashboard.
 * O {@code label} ja vem em pt-BR ("Jan".."Dez") para consumo direto no recharts.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyCountDTO {
    private int ano;
    private int mes;       // 1-12
    private String label;  // "Jan".."Dez"
    private long count;
}
