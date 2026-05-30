package unicap.juryscan.service.lead;

import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.lead.LeadDetailedResponseDTO;
import unicap.juryscan.dto.lead.LeadResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;

import java.util.UUID;

public interface ILeadService {

    LeadResponseDTO createLead(UUID userId, UUID analysisId);

    PageResponse<LeadResponseDTO> getAvailableLeads(Pageable pageable);

    LeadResponseDTO acquireLead(UUID advogadoId, UUID leadId);

    PageResponse<LeadResponseDTO> getLeadsByAdvogado(UUID advogadoId, Pageable pageable);

    LeadDetailedResponseDTO getLeadDetails(UUID leadId, UUID advogadoId);

    PageResponse<LeadResponseDTO> getMyLeadRequests(UUID userId, Pageable pageable);

    void cancelLead(UUID userId, UUID leadId);
}

