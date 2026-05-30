package unicap.juryscan.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.lead.LeadCreateRequestDTO;
import unicap.juryscan.dto.lead.LeadDetailedResponseDTO;
import unicap.juryscan.dto.lead.LeadResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.infra.ApiResponse;
import unicap.juryscan.model.User;
import unicap.juryscan.service.lead.ILeadService;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/leads")
public class LeadController {

    private final ILeadService leadService;

    public LeadController(ILeadService leadService) {
        this.leadService = leadService;
    }

    // ===== ENDPOINTS PARA USUÁRIO COMUM =====

    @PostMapping("/request")
    public ResponseEntity<ApiResponse> createLead(
            @RequestBody LeadCreateRequestDTO request,
            @AuthenticationPrincipal User user) {
        LeadResponseDTO lead = leadService.createLead(user.getId(), request.getAnalysisId());
        ApiResponse response = new ApiResponse(true, "Solicitação de advogado criada com sucesso", lead, 201);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse> getMyLeadRequests(
            @RequestParam("page") int page,
            @RequestParam("page_size") int pageSize,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        PageResponse<LeadResponseDTO> leads = leadService.getMyLeadRequests(user.getId(), pageable);

        if (leads.getItems().isEmpty()) {
            ApiResponse response = new ApiResponse(true, "Nenhuma solicitação encontrada", 204);
            return ResponseEntity.status(204).body(response);
        }

        ApiResponse response = new ApiResponse(true, "Solicitações encontradas com sucesso", leads, 200);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{leadId}/cancel")
    public ResponseEntity<ApiResponse> cancelLead(
            @PathVariable UUID leadId,
            @AuthenticationPrincipal User user) {
        leadService.cancelLead(user.getId(), leadId);
        ApiResponse response = new ApiResponse(true, "Solicitação cancelada com sucesso", 200);
        return ResponseEntity.status(200).body(response);
    }

    // ===== ENDPOINTS PARA ADVOGADO =====

    @GetMapping("/available")
    public ResponseEntity<ApiResponse> getAvailableLeads(
            @RequestParam("page") int page,
            @RequestParam("page_size") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        PageResponse<LeadResponseDTO> leads = leadService.getAvailableLeads(pageable);

        if (leads.getItems().isEmpty()) {
            ApiResponse response = new ApiResponse(true, "Nenhum lead disponível", 204);
            return ResponseEntity.status(204).body(response);
        }

        ApiResponse response = new ApiResponse(true, "Leads disponíveis encontrados com sucesso", leads, 200);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/{leadId}/acquire")
    public ResponseEntity<ApiResponse> acquireLead(
            @PathVariable UUID leadId,
            @AuthenticationPrincipal User user) {
        LeadResponseDTO lead = leadService.acquireLead(user.getId(), leadId);
        ApiResponse response = new ApiResponse(true, "Lead adquirido com sucesso", lead, 200);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/acquired")
    public ResponseEntity<ApiResponse> getAcquiredLeads(
            @RequestParam("page") int page,
            @RequestParam("page_size") int pageSize,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        PageResponse<LeadResponseDTO> leads = leadService.getLeadsByAdvogado(user.getId(), pageable);

        if (leads.getItems().isEmpty()) {
            ApiResponse response = new ApiResponse(true, "Nenhum lead adquirido", 204);
            return ResponseEntity.status(204).body(response);
        }

        ApiResponse response = new ApiResponse(true, "Leads adquiridos encontrados com sucesso", leads, 200);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{leadId}/details")
    public ResponseEntity<ApiResponse> getLeadDetails(
            @PathVariable UUID leadId,
            @AuthenticationPrincipal User user) {
        LeadDetailedResponseDTO leadDetails = leadService.getLeadDetails(leadId, user.getId());
        ApiResponse response = new ApiResponse(true, "Detalhes do lead obtidos com sucesso", leadDetails, 200);
        return ResponseEntity.status(200).body(response);
    }
}

