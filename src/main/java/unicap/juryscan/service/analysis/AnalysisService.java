package unicap.juryscan.service.analysis;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unicap.juryscan.dto.ai.AIResponseDTO;
import unicap.juryscan.dto.analysis.AnalysisCreateDTO;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.mapper.AnalysisMapper;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.serviceAI.GeminiAIService;
import unicap.juryscan.service.serviceAI.IGenericAIService;

import java.util.UUID;
//TODO dependendo da interface frontend, retornar dados do usuário em certas consultas
@Service
public class AnalysisService implements IAnalysisService {

    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;

    private final IGenericAIService genericAIService;

    public AnalysisService(AnalysisRepository analysisRepository, AnalysisMapper analysisMapper, UserRepository userRepository, IGenericAIService genericAIService) {
        this.analysisRepository = analysisRepository;
        this.analysisMapper = analysisMapper;
        this.userRepository = userRepository;
        this.genericAIService = genericAIService;
    }

    @Override
    public PageResponse<AnalysisResponseDTO> getAllAnalysis(Pageable pageable) {
        Page<AnalysisResponseDTO> page = analysisRepository
                .findAll(pageable)
                .map(analysisMapper::toResponseDTO);
        PageResponse<AnalysisResponseDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setItems(page.getContent());
        pageResponse.setPageSize(page.getSize());

        return pageResponse;
    }

    @Override
    public PageResponse<AnalysisResponseDTO> getAllAnalysisByUserId(UUID userId, Pageable pageable) {
        Page<AnalysisResponseDTO> page = analysisRepository
                .findAllByUsuarioId(userId, pageable)
                .map(analysisMapper::toResponseDTO);

        //TODO Implementação de mapper de Page para PageResponse
        PageResponse<AnalysisResponseDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setItems(page.getContent());
        pageResponse.setPageSize(page.getSize());

        return pageResponse;
    }

    @Override
    public AnalysisResponseDTO createAnalysis(UUID userId, byte[] documentBytes) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        AIResponseDTO aiResponse = genericAIService.analyzeDocument(documentBytes);

        Analysis analysis = analysisMapper.toEntity(aiResponse);
        analysis.setUsuario(user);
        analysis = analysisRepository.save(analysis);

        return analysisMapper.toResponseDTO(analysis);
    }

    @Override
    public AnalysisResponseDTO getAnalysisById(UUID analysisId) {
        Analysis analysis = analysisRepository
                .findById(analysisId)
                .orElseThrow(() -> new ResourceNotFoundException("Análise não encontrada"));
        return analysisMapper.toResponseDTO(analysis);
    }
}
