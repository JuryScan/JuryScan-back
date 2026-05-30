package unicap.juryscan.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.dto.lead.LeadDetailedResponseDTO;
import unicap.juryscan.dto.lead.LeadResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.Lead;
import unicap.juryscan.model.User;

@Component
public class LeadMapper {

    private final AnalysisMapper analysisMapper;

    public LeadMapper(AnalysisMapper analysisMapper) {
        this.analysisMapper = analysisMapper;
    }

    public LeadResponseDTO toResponseDTO(Lead lead) {
        return LeadResponseDTO.builder()
                .id(lead.getId())
                .usuarioClienteId(lead.getUsuarioCliente().getId())
                .nomeCliente(lead.getUsuarioCliente().getNomeCompleto())
                .analiseId(lead.getAnalise().getId())
                .tituloAnalise(lead.getAnalise().getTitulo())
                .status(lead.getStatus())
                .custoCreditos(lead.getCustoCreditos())
                .dataCriacao(lead.getDataCriacao())
                .dataAquisicao(lead.getDataAquisicao())
                .advogadoId(lead.getAdvogado() != null ? lead.getAdvogado().getId() : null)
                .build();
    }

    public LeadDetailedResponseDTO toDetailedResponseDTO(Lead lead) {
        User cliente = lead.getUsuarioCliente();
        Analysis analysis = lead.getAnalise();

        AnalysisResponseDTO analysisDTO = analysisMapper.toResponseDTO(analysis);

        return LeadDetailedResponseDTO.builder()
                .id(lead.getId())
                .status(lead.getStatus())
                .custoCreditos(lead.getCustoCreditos())
                .dataCriacao(lead.getDataCriacao())
                .dataAquisicao(lead.getDataAquisicao())
                .clienteId(cliente.getId())
                .nomeCompleto(cliente.getNomeCompleto())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .cpf(cliente.getCpf())
                .dataNascimento(cliente.getDataNascimento())
                .analise(analysisDTO)
                .build();
    }

    public PageResponse<LeadResponseDTO> toPageResponse(Page<Lead> page) {
        PageResponse<LeadResponseDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setPageSize(page.getSize());
        pageResponse.setItems(page.getContent().stream()
                .map(this::toResponseDTO)
                .toList());

        return pageResponse;
    }
}

