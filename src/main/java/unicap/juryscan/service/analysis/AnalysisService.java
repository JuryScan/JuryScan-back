package unicap.juryscan.service.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicap.juryscan.dto.integrationAi.AIResponseDTO;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.mapper.AnalysisMapper;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.serviceAI.IGenericAIService;
import unicap.juryscan.service.wallet.IWalletService;

import java.util.UUID;

@Service
public class AnalysisService implements IAnalysisService {

    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;
    private final IGenericAIService genericAIService;
    private final IWalletService walletService;

    @Value("${api.pricing.analysis-cost}")
    private Integer analysisCost;

    public AnalysisService(AnalysisRepository analysisRepository, AnalysisMapper analysisMapper,
                          UserRepository userRepository, IGenericAIService genericAIService,
                          IWalletService walletService) {
        this.analysisRepository = analysisRepository;
        this.analysisMapper = analysisMapper;
        this.userRepository = userRepository;
        this.genericAIService = genericAIService;
        this.walletService = walletService;
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

        PageResponse<AnalysisResponseDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setItems(page.getContent());
        pageResponse.setPageSize(page.getSize());

        return pageResponse;
    }

    @Override
    @Transactional
    public AnalysisResponseDTO createAnalysis(UUID userId, byte[] documentBytes) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Validar saldo de créditos antes de processar
        Integer currentBalance = walletService.getBalance(userId);
        if (currentBalance < analysisCost) {
            throw new IllegalStateException("Saldo insuficiente. Saldo atual: " + currentBalance +
                    ", créditos necessários: " + analysisCost);
        }

        // Processar análise com a IA
        AIResponseDTO aiResponse = genericAIService.analyzeDocument(documentBytes);

        // Mapear resposta da IA para entidade
        Analysis analysis = analysisMapper.toEntity(aiResponse);
        analysis.setUsuario(user);
        analysis = analysisRepository.save(analysis);

        // Descontar créditos após análise bem-sucedida
        walletService.deductCredits(userId, analysisCost);

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
