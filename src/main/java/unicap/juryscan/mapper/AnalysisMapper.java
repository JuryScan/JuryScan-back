package unicap.juryscan.mapper;

import org.springframework.stereotype.Component;
import unicap.juryscan.dto.ai.AIResponseDTO;
import unicap.juryscan.dto.analysis.AnalysisCreateDTO;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.Failure;

@Component
public class AnalysisMapper {

    private final FailureMapper failureMapper;

    public AnalysisMapper(FailureMapper failureMapper) {
        this.failureMapper = failureMapper;
    }

    public Analysis toEntity(AIResponseDTO dto) {
        Analysis analysis = new Analysis();
        analysis.setTitulo(dto.getTitulo());
        analysis.setDescricaoGeral(dto.getDescricaoGeral());
        analysis.setFalhas(dto
                .getFailures()
                .stream()
                .map(f -> {
                    Failure failure = failureMapper.toEntity(f);
                    failure.setAnalise(analysis);
                    return failure;
                }).toList()
        );
        return analysis;
    }

    public Analysis toEntity(AnalysisCreateDTO dto){
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
    }

    public AnalysisResponseDTO toResponseDTO(Analysis entity){
        AnalysisResponseDTO dto = new AnalysisResponseDTO();
        dto.setTitulo(entity.getTitulo());
        dto.setDescricaoGeral(entity.getDescricaoGeral());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setId(entity.getId());
        return dto;
    }
}
