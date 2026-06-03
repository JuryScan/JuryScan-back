package unicap.juryscan.mapper;import org.springframework.stereotype.Component;
import unicap.juryscan.dto.integrationAi.AIResponseDTO;
import unicap.juryscan.dto.analysis.AnalysisCreateDTO;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.Failure;@Component
public class AnalysisMapper {    private final FailureMapper failureMapper;    public AnalysisMapper(FailureMapper failureMapper) {
        this.failureMapper = failureMapper;
    }    public Analysis toEntity(AIResponseDTO dto) {
        Analysis analysis = new Analysis();        if (dto.getResult() != null) {
            analysis.setTitulo(dto.getResult().getTitulo());
            analysis.setDescricaoGeral(dto.getResult().getDescricaoGeral());
            analysis.setRelatorioSumarioJuridico(dto.getResult().getRelatorio_sumario_juridico());
            analysis.setSumario(dto.getResult().getSumario());            if (dto.getResult().getFailures() != null && !dto.getResult().getFailures().isEmpty()) {
                analysis.setFalhas(dto.getResult()
                        .getFailures()
                        .stream()
                        .map(f -> {
                            Failure failure = failureMapper.toEntity(f);
                            failure.setAnalise(analysis);
                            return failure;
                        }).toList()
                );
            }
        }        return analysis;
    }    public Analysis toEntity(AnalysisCreateDTO dto){
        Analysis analysis = new Analysis();
        analysis.setTitulo(dto.getTitulo());
        analysis.setDescricaoGeral(dto.getDescricaoGeral());
        analysis.setFalhas(dto
                .getFalhas()
                .stream()
                .map(f -> {
                    Failure failure = failureMapper.toEntity(f);
                    failure.setAnalise(analysis);
                    return failure;
                }).toList()
        );
        return analysis;
    }    public AnalysisResponseDTO toResponseDTO(Analysis entity){
        AnalysisResponseDTO dto = new AnalysisResponseDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescricaoGeral(entity.getDescricaoGeral());
        dto.setRelatorioSumarioJuridico(entity.getRelatorioSumarioJuridico());
        dto.setSumario(entity.getSumario());
        dto.setDataCriacao(entity.getDataCriacao());        if (entity.getFalhas() != null && !entity.getFalhas().isEmpty()) {
            dto.setFalhas(entity.getFalhas()
                    .stream()
                    .map(failureMapper::toResponseDTO)
                    .toList());
        } else {
            dto.setFalhas(java.util.Collections.emptyList());
        }

        return dto;
    }
}