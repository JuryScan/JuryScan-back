package unicap.juryscan.mapper;

import org.springframework.stereotype.Component;
import unicap.juryscan.dto.failure.FailureCreateDTO;
import unicap.juryscan.dto.failure.FailureResponseDTO;
import unicap.juryscan.model.Failure;

@Component
public class FailureMapper {

    public Failure toEntity(FailureCreateDTO dto){
        Failure failure = new Failure();
        failure.setConfianca(dto.getConfianca());
        failure.setDescricao(dto.getDescricao());
        failure.setTitulo(dto.getTitulo());
        failure.setSugestaoCorrecao(dto.getSugestaoCorrecao());
        failure.setSeveridade(dto.getSeveridade());
        return failure;
    }

    public FailureResponseDTO toResponseDTO(Failure entity){
        FailureResponseDTO dto = new FailureResponseDTO();
        dto.setConfianca(entity.getConfianca());
        dto.setId(entity.getId());
        dto.setSeveridade(entity.getSeveridade());
        dto.setSugestaoCorrecao(entity.getSugestaoCorrecao());
        dto.setTitulo(entity.getTitulo());
        return dto;
    }
}
