package unicap.juryscan.dto.dashboard;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetricsDTO {

    private Long leadsAdquiridos;

    private Long leadsDisponiveis;

    private Long analisesNoMes;

    private Integer saldoCreditos;
}

