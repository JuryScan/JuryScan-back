package unicap.juryscan.dto.dashboard;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetricsDTO {

    // KPIs de leads
    private Long leadsAdquiridos;
    private Long leadsAdquiridosNoMes;
    private Long leadsDisponiveis;
    private Long clientesAtivos;

    // KPIs de analises / falhas
    private Long analisesNoMes;
    private Long analisesTotais;
    private Long totalErros;

    // Creditos
    private Long totalGastoEmCreditos;
    private Integer saldoCreditos;

    // Indicador derivado: clientes atendidos / leads adquiridos (nulo quando nao ha leads adquiridos)
    private Double taxaConversao;

    // Series mensais para os graficos (ultimos 6 meses, com meses vazios preenchidos por zero)
    private List<MonthlyCountDTO> leadsPorMes;
    private List<MonthlyCountDTO> analisesPorMes;
    private List<MonthlyCountDTO> errosPorMes;
}
