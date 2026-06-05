package unicap.juryscan.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * KPIs e séries temporais agregadas do dashboard do advogado.
 * Todos os números são calculados a partir de dados reais (leads, análises,
 * falhas, transações e carteira). Campos sem fonte de dados não são expostos.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAdvogadoResponseDTO {

    private UUID advogadoId;

    // ===== KPIs =====
    /** Clientes distintos a partir dos leads ADQUIRIDO deste advogado. */
    private long clientesAtivos;
    /** Leads adquiridos por este advogado (todos os tempos). */
    private long leadsAdquiridosTotais;
    /** Leads adquiridos no mês corrente. */
    private long leadsAdquiridosNoMes;
    /** Leads DISPONIVEL no marketplace (global) — "novos leads" para captar. */
    private long leadsDisponiveis;
    /** Análises do próprio advogado (todos os tempos). */
    private long analisesTotais;
    /** Análises do próprio advogado no mês corrente. */
    private long analisesNoMes;
    /** Saldo de tokens da carteira do advogado. */
    private int saldoTokens;
    /** Total de tokens gastos em aquisição de leads. */
    private long totalGastoEmLeads;
    /** Total de falhas detectadas nas análises dos leads adquiridos. */
    private long totalErros;
    /** clientesAtivos / leadsAdquiridosTotais; null quando não há leads adquiridos. */
    private Double taxaConversao;

    // ===== Séries temporais (últimos 6 meses, mais antigo -> mais recente) =====
    private List<MonthlyCountDTO> leadsPorMes;
    private List<MonthlyCountDTO> analisesPorMes;
    private List<MonthlyCountDTO> errosPorMes;
}
