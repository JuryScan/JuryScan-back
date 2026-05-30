package unicap.juryscan.service.lead;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicap.juryscan.dto.lead.LeadDetailedResponseDTO;
import unicap.juryscan.dto.lead.LeadResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.enums.TipoTransacaoEnum;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.exception.InsufficientCreditsException;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.mapper.LeadMapper;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.Lead;
import unicap.juryscan.model.Transaction;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.LeadRepository;
import unicap.juryscan.repository.TransactionRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.wallet.IWalletService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
public class LeadService implements ILeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;
    private final IWalletService walletService;
    private final TransactionRepository transactionRepository;
    private final LeadMapper leadMapper;

    private static final Integer DEFAULT_LEAD_COST = 10;

    public LeadService(LeadRepository leadRepository,
                       UserRepository userRepository,
                       AnalysisRepository analysisRepository,
                       IWalletService walletService,
                       TransactionRepository transactionRepository,
                       LeadMapper leadMapper) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.analysisRepository = analysisRepository;
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
        this.leadMapper = leadMapper;
    }

    @Override
    @Transactional
    public LeadResponseDTO createLead(UUID userId, UUID analysisId) {
        // Buscar usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Validar que é usuário comum
        if (user.getTipoUsuario() != TipoUserEnum.COMUM) {
            throw new IllegalStateException("Apenas usuários comuns podem criar solicitações de lead");
        }

        // Buscar análise
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new ResourceNotFoundException("Análise não encontrada"));

        // Validar que a análise pertence ao usuário
        if (!analysis.getUsuario().getId().equals(userId)) {
            throw new IllegalStateException("Análise não pertence ao usuário");
        }

        // Verificar se já existe lead para esta análise
        if (leadRepository.existsByAnaliseId(analysisId)) {
            throw new IllegalStateException("Já existe uma solicitação de lead para esta análise");
        }

        // Criar lead
        Lead lead = new Lead();
        lead.setUsuarioCliente(user);
        lead.setAnalise(analysis);
        lead.setStatus(StatusLeadEnum.DISPONIVEL);
        lead.setCustoCreditos(DEFAULT_LEAD_COST);

        lead = leadRepository.save(lead);

        return leadMapper.toResponseDTO(lead);
    }

    @Override
    public PageResponse<LeadResponseDTO> getAvailableLeads(Pageable pageable) {
        Page<Lead> page = leadRepository.findByStatus(StatusLeadEnum.DISPONIVEL, pageable);
        return leadMapper.toPageResponse(page);
    }

    @Override
    @Transactional
    public LeadResponseDTO acquireLead(UUID advogadoId, UUID leadId) {
        // Buscar advogado
        User advogado = userRepository.findById(advogadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Advogado não encontrado"));

        // Validar que é advogado
        if (advogado.getTipoUsuario() != TipoUserEnum.ADVOGADO) {
            throw new IllegalStateException("Apenas advogados podem adquirir leads");
        }

        // Buscar lead
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado"));

        // Validar status
        if (lead.getStatus() != StatusLeadEnum.DISPONIVEL) {
            throw new IllegalStateException("Lead não está disponível para aquisição. Status atual: " + lead.getStatus());
        }

        // Verificar saldo
        Integer saldo = walletService.getBalance(advogadoId);
        if (saldo < lead.getCustoCreditos()) {
            throw new InsufficientCreditsException("Saldo insuficiente. Saldo atual: " + saldo +
                    ", créditos necessários: " + lead.getCustoCreditos());
        }

        // Debitar créditos
        walletService.deductCredits(advogadoId, lead.getCustoCreditos());

        // Criar transação
        Transaction transaction = new Transaction();
        transaction.setUsuario(advogado);
        transaction.setTipoTransacao(TipoTransacaoEnum.AQUISICAO_LEAD);
        transaction.setQuantidade(lead.getCustoCreditos());
        transactionRepository.save(transaction);

        // Atualizar lead
        lead.setAdvogado(advogado);
        lead.setStatus(StatusLeadEnum.ADQUIRIDO);
        lead.setDataAquisicao(Timestamp.from(Instant.now()));

        lead = leadRepository.save(lead);

        return leadMapper.toResponseDTO(lead);
    }

    @Override
    public PageResponse<LeadResponseDTO> getLeadsByAdvogado(UUID advogadoId, Pageable pageable) {
        Page<Lead> page = leadRepository.findByAdvogadoId(advogadoId, pageable);
        return leadMapper.toPageResponse(page);
    }

    @Override
    public LeadDetailedResponseDTO getLeadDetails(UUID leadId, UUID advogadoId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado"));

        // Validar que o lead foi adquirido por este advogado
        if (lead.getAdvogado() == null || !lead.getAdvogado().getId().equals(advogadoId)) {
            throw new IllegalStateException("Você não tem permissão para acessar este lead");
        }

        // Validar status
        if (lead.getStatus() != StatusLeadEnum.ADQUIRIDO) {
            throw new IllegalStateException("Lead não foi adquirido");
        }

        return leadMapper.toDetailedResponseDTO(lead);
    }

    @Override
    public PageResponse<LeadResponseDTO> getMyLeadRequests(UUID userId, Pageable pageable) {
        Page<Lead> page = leadRepository.findByUsuarioClienteId(userId, pageable);
        return leadMapper.toPageResponse(page);
    }

    @Override
    @Transactional
    public void cancelLead(UUID userId, UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado"));

        // Validar que o lead pertence ao usuário
        if (!lead.getUsuarioCliente().getId().equals(userId)) {
            throw new IllegalStateException("Você não tem permissão para cancelar este lead");
        }

        // Validar status
        if (lead.getStatus() == StatusLeadEnum.ADQUIRIDO) {
            throw new IllegalStateException("Não é possível cancelar um lead que já foi adquirido");
        }

        if (lead.getStatus() == StatusLeadEnum.CANCELADO) {
            throw new IllegalStateException("Lead já está cancelado");
        }

        lead.setStatus(StatusLeadEnum.CANCELADO);
        leadRepository.save(lead);
    }
}


