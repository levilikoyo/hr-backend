package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.AuditChecklistItemModel;
import com.hr.backend.fin_model.AuditFileModel;
import com.hr.backend.fin_model.AuditFindingModel;
import com.hr.backend.fin_model.AuditIneligibleCostModel;
import com.hr.backend.fin_model.AuditRequest;
import com.hr.backend.fin_model.AuditWorkflowModel;
import com.hr.backend.fin_repository.AuditChecklistItemRepository;
import com.hr.backend.fin_repository.AuditFileRepository;
import com.hr.backend.fin_repository.AuditFindingRepository;
import com.hr.backend.fin_repository.AuditIneligibleCostRepository;
import com.hr.backend.fin_repository.AuditWorkflowRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audits")
@CrossOrigin(origins = "*")
public class AuditController {

    @Autowired private AuditFileRepository auditRepository;
    @Autowired private AuditChecklistItemRepository checklistRepository;
    @Autowired private AuditFindingRepository findingRepository;
    @Autowired private AuditIneligibleCostRepository ineligibleRepository;
    @Autowired private AuditWorkflowRepository workflowRepository;

    @GetMapping("/organization/{organization}")
    public List<AuditFileModel> byOrganization(@PathVariable String organization) {
        return auditRepository.findByOrganizationOrderByIdDesc(clean(organization));
    }

    @GetMapping("/organization/{organization}/audit/{auditCode}")
    public ResponseEntity<?> getAudit(@PathVariable String organization, @PathVariable String auditCode) {
        String org = clean(organization);
        String code = clean(auditCode);
        AuditFileModel header = auditRepository.findByOrganizationAndAuditCode(org, code).orElse(null);
        if (header == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Audit file not found"));
        }
        AuditRequest response = new AuditRequest();
        response.setHeader(header);
        response.setChecklist(checklistRepository.findByOrganizationAndAuditCodeOrderByLineNoAsc(org, code));
        response.setFindings(findingRepository.findByOrganizationAndAuditCodeOrderByIdAsc(org, code));
        response.setIneligibleCosts(ineligibleRepository.findByOrganizationAndAuditCodeOrderByIdAsc(org, code));
        response.setWorkflow(workflowRepository.findByOrganizationAndAuditCodeOrderByStepNoAsc(org, code));
        return ResponseEntity.ok(response);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> save(@RequestBody AuditRequest request) {
        AuditFileModel incoming = request == null ? null : request.getHeader();
        if (incoming == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Audit file header is required"));
        }
        String organization = clean(incoming.getOrganization());
        String auditCode = clean(incoming.getAuditCode());
        if (organization.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Organization is required"));
        }
        if (auditCode.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Audit code is required"));
        }

        AuditFileModel header = auditRepository
                .findByOrganizationAndAuditCode(organization, auditCode)
                .orElse(new AuditFileModel());
        header.setOrganization(organization);
        header.setAuditCode(auditCode);
        header.setAuditName(clean(incoming.getAuditName()));
        header.setFundCode(clean(incoming.getFundCode()));
        header.setDonorName(clean(incoming.getDonorName()));
        header.setAuditorName(clean(incoming.getAuditorName()));
        header.setPeriodFrom(clean(incoming.getPeriodFrom()));
        header.setPeriodTo(clean(incoming.getPeriodTo()));
        header.setStatus(emptyTo(incoming.getStatus(), "Draft"));
        header.setResponsiblePerson(clean(incoming.getResponsiblePerson()));
        header.setProgress(emptyTo(incoming.getProgress(), "0%"));
        header.setNotes(clean(incoming.getNotes()));
        header.setUpdatedBy(clean(incoming.getUpdatedBy()));
        header.setUpdatedDate(LocalDate.now().toString());
        if (clean(header.getCreatedDate()).isEmpty()) {
            header.setCreatedBy(clean(incoming.getCreatedBy()));
            header.setCreatedDate(LocalDate.now().toString());
        }
        auditRepository.save(header);

        checklistRepository.deleteByOrganizationAndAuditCode(organization, auditCode);
        int lineNo = 1;
        if (request.getChecklist() != null) {
            for (AuditChecklistItemModel line : request.getChecklist()) {
                line.setId(null);
                line.setOrganization(organization);
                line.setAuditCode(auditCode);
                line.setLineNo(lineNo++);
                checklistRepository.save(line);
            }
        }

        findingRepository.deleteByOrganizationAndAuditCode(organization, auditCode);
        if (request.getFindings() != null) {
            for (AuditFindingModel finding : request.getFindings()) {
                finding.setId(null);
                finding.setOrganization(organization);
                finding.setAuditCode(emptyTo(finding.getAuditCode(), auditCode));
                finding.setAmount(zeroSafe(finding.getAmount()));
                findingRepository.save(finding);
            }
        }

        ineligibleRepository.deleteByOrganizationAndAuditCode(organization, auditCode);
        if (request.getIneligibleCosts() != null) {
            for (AuditIneligibleCostModel cost : request.getIneligibleCosts()) {
                cost.setId(null);
                cost.setOrganization(organization);
                cost.setAuditCode(emptyTo(cost.getAuditCode(), auditCode));
                cost.setOriginalAmount(zeroSafe(cost.getOriginalAmount()));
                cost.setIneligibleAmount(zeroSafe(cost.getIneligibleAmount()));
                cost.setFinalAmount(zeroSafe(cost.getFinalAmount()));
                ineligibleRepository.save(cost);
            }
        }

        workflowRepository.deleteByOrganizationAndAuditCode(organization, auditCode);
        int stepNo = 1;
        if (request.getWorkflow() != null) {
            for (AuditWorkflowModel step : request.getWorkflow()) {
                step.setId(null);
                step.setOrganization(organization);
                step.setAuditCode(auditCode);
                step.setStepNo(stepNo++);
                workflowRepository.save(step);
            }
        }

        return ResponseEntity.ok(Map.of("message", "SUCCESS", "auditCode", auditCode));
    }

    @Transactional
    @DeleteMapping("/organization/{organization}/audit/{auditCode}")
    public ResponseEntity<?> deleteAudit(@PathVariable String organization, @PathVariable String auditCode) {
        String org = clean(organization);
        String code = clean(auditCode);
        AuditFileModel header = auditRepository.findByOrganizationAndAuditCode(org, code).orElse(null);
        if (header == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Audit file not found"));
        }
        checklistRepository.deleteByOrganizationAndAuditCode(org, code);
        findingRepository.deleteByOrganizationAndAuditCode(org, code);
        ineligibleRepository.deleteByOrganizationAndAuditCode(org, code);
        workflowRepository.deleteByOrganizationAndAuditCode(org, code);
        auditRepository.delete(header);
        return ResponseEntity.ok(Map.of("message", "SUCCESS"));
    }

    private String clean(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String emptyTo(String value, String fallback) {
        String cleaned = clean(value);
        return cleaned.isEmpty() ? fallback : cleaned;
    }

    private BigDecimal zeroSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
