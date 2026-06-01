package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.NeedsRequestApprovalHistoryModel;
import com.hr.backend.fin_model.NeedsRequestItemModel;
import com.hr.backend.fin_model.NeedsRequestModel;
import com.hr.backend.fin_repository.NeedsRequestApprovalHistoryRepository;
import com.hr.backend.fin_repository.NeedsRequestItemRepository;
import com.hr.backend.fin_repository.NeedsRequestRepository;
import com.hr.backend.service.MobileNotificationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/needs-requests")
@CrossOrigin(origins = "*")
public class NeedsRequestController {

    @Autowired
    private NeedsRequestRepository needsRequestRepository;

    @Autowired
    private NeedsRequestItemRepository needsRequestItemRepository;

    @Autowired
    private NeedsRequestApprovalHistoryRepository approvalHistoryRepository;

    @Autowired
    private MobileNotificationService mobileNotificationService;

    @GetMapping("/test")
    public String test() {
        return "Needs Requests API is working";
    }

    @PostMapping
    public NeedsRequestModel create(@RequestBody NeedsRequestModel request) {

        if (isBlank(request.getRequestNo())) {
            request.setRequestNo(generateRequestNo(request.getOrganization()));
        }

        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDate.now());
        }

        request.setStatus("PENDING_HOD_APPROVAL");
        request.setCurrentApprovalLevel("HOD");

        BigDecimal total = BigDecimal.ZERO;

        if (request.getItems() != null) {
            for (NeedsRequestItemModel item : request.getItems()) {

                item.setNeedsRequest(request);
                item.setOrganization(request.getOrganization());

                if (isBlank(item.getBudgetPlan())) {
                    item.setBudgetPlan(request.getBudgetPlan());
                }

                if (isBlank(item.getGlAccountNo())) {
                    item.setGlAccountNo(request.getGlAccountNo());
                }

                if (isBlank(item.getFundCode())) {
                    item.setFundCode(request.getFundCode());
                }

                BigDecimal quantity = safeBigDecimal(item.getQuantity());
                BigDecimal unitPrice = safeBigDecimal(item.getUnitPrice());
                BigDecimal lineTotal = quantity.multiply(unitPrice);

                item.setQuantity(quantity);
                item.setUnitPrice(unitPrice);
                item.setTotalAmount(lineTotal);

                total = total.add(lineTotal);
            }
        }

        request.setEstimatedAmount(total);

        NeedsRequestModel saved = needsRequestRepository.save(request);

        saveHistory(
                saved,
                "REQUESTER",
                "SUBMITTED",
                saved.getCreatedBy(),
                "REQUESTER",
                "Expression de besoin submitted"
        );

        mobileNotificationService.notifyRole(
                saved.getOrganization(),
                "HOD",
                "New request pending HOD approval",
                saved.getRequestNo() + " - " + saved.getTitle(),
                "REQUEST_SUBMITTED",
                saved
        );

        return saved;
    }

    @GetMapping("/organization/{organization}")
    public List<NeedsRequestModel> getByOrganization(
            @PathVariable String organization) {

        return needsRequestRepository.findByOrganizationOrderByIdDesc(organization);
    }

    @GetMapping("/pending-approval/{organization}")
    public List<NeedsRequestModel> getPendingApproval(
            @PathVariable String organization) {

        return needsRequestRepository.findByOrganizationAndStatusOrderByIdDesc(
                organization,
                "PENDING_HOD_APPROVAL"
        );
    }

    @GetMapping("/pending-approval/{organization}/{role}")
    public List<NeedsRequestModel> getPendingApprovalByRole(
            @PathVariable String organization,
            @PathVariable String role) {

        String normalizedRole = safe(role).toUpperCase();

        if ("ADMIN".equals(normalizedRole)) {
            return needsRequestRepository.findPendingByOrganizationAndStatusPrefix(
                    organization,
                    "PENDING_"
            );
        }

        String status = getPendingStatusForRole(normalizedRole);

        return needsRequestRepository.findByOrganizationAndStatusOrderByIdDesc(
                organization,
                status
        );
    }

    @GetMapping("/{id}/history")
    public List<NeedsRequestApprovalHistoryModel> getHistory(
            @PathVariable Long id) {

        NeedsRequestModel request = needsRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        return approvalHistoryRepository.findByOrganizationAndNeedsRequestIdOrderByIdAsc(
                request.getOrganization(),
                request.getId()
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestParam(required = false) String approvedBy,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String comment) {

        NeedsRequestModel request = needsRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if ("APPROVED".equals(request.getStatus())) {
            return ResponseEntity.badRequest().body("Request is already approved");
        }

        if ("REJECTED".equals(request.getStatus())) {
            return ResponseEntity.badRequest().body("Request is already rejected");
        }

        String approverRole = safe(role).toUpperCase();

        if (!canApproveCurrentLevel(request, approverRole)) {
            return ResponseEntity.status(403).body(
                    "You are not allowed to approve this level: "
                    + request.getCurrentApprovalLevel()
            );
        }

        recalculateRequestTotal(request);

        LocalDateTime now = LocalDateTime.now();
        String currentLevel = safe(request.getCurrentApprovalLevel()).toUpperCase();

        if ("HOD".equals(currentLevel)) {

            request.setHodApprovedBy(approvedBy);
            request.setHodApprovedAt(now);

            request.setStatus("PENDING_FINANCE_REVIEW");
            request.setCurrentApprovalLevel("FINANCE");

            saveHistory(
                    request,
                    "HOD",
                    "APPROVED",
                    approvedBy,
                    approverRole,
                    comment
            );

            mobileNotificationService.notifyRole(
                    request.getOrganization(),
                    "FINANCE",
                    "Request pending Finance review",
                    request.getRequestNo() + " - " + request.getTitle(),
                    "PENDING_FINANCE_REVIEW",
                    request
            );

        } else if ("FINANCE".equals(currentLevel)) {

            request.setFinanceReviewedBy(approvedBy);
            request.setFinanceReviewedAt(now);

            request.setStatus("PENDING_DIRECTOR_APPROVAL");
            request.setCurrentApprovalLevel("DIRECTOR");

            saveHistory(
                    request,
                    "FINANCE",
                    "APPROVED",
                    approvedBy,
                    approverRole,
                    comment
            );

            mobileNotificationService.notifyRole(
                    request.getOrganization(),
                    "DIRECTOR",
                    "Request pending Director approval",
                    request.getRequestNo() + " - " + request.getTitle(),
                    "PENDING_DIRECTOR_APPROVAL",
                    request
            );

        } else if ("DIRECTOR".equals(currentLevel)) {

            request.setDirectorApprovedBy(approvedBy);
            request.setDirectorApprovedAt(now);

            request.setApprovedBy(approvedBy);
            request.setApprovedAt(now);

            request.setStatus("APPROVED");
            request.setCurrentApprovalLevel("COMPLETED");

            saveHistory(
                    request,
                    "DIRECTOR",
                    "APPROVED",
                    approvedBy,
                    approverRole,
                    comment
            );

            mobileNotificationService.notifyUser(
                    request.getOrganization(),
                    request.getRequesterEmail(),
                    "Request fully approved",
                    request.getRequestNo() + " - " + request.getTitle(),
                    "REQUEST_APPROVED",
                    request
            );

        } else {
            return ResponseEntity.badRequest().body(
                    "Invalid approval level: " + request.getCurrentApprovalLevel()
            );
        }

        NeedsRequestModel saved = needsRequestRepository.save(request);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String rejectedBy,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String reason) {

        NeedsRequestModel request = needsRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if ("APPROVED".equals(request.getStatus())) {
            return ResponseEntity.badRequest().body("Approved request cannot be rejected");
        }

        if ("REJECTED".equals(request.getStatus())) {
            return ResponseEntity.badRequest().body("Request is already rejected");
        }

        String approverRole = safe(role).toUpperCase();
        String rejectedLevel = safe(request.getCurrentApprovalLevel()).toUpperCase();

        if (!canApproveCurrentLevel(request, approverRole)) {
            return ResponseEntity.status(403).body(
                    "You are not allowed to reject this level: "
                    + request.getCurrentApprovalLevel()
            );
        }

        request.setStatus("REJECTED");
        request.setCurrentApprovalLevel("REJECTED");
        request.setRejectedBy(rejectedBy);
        request.setRejectedAt(LocalDateTime.now());
        request.setRejectionReason(reason);

        saveHistory(
                request,
                rejectedLevel,
                "REJECTED",
                rejectedBy,
                approverRole,
                reason
        );

        mobileNotificationService.notifyUser(
                request.getOrganization(),
                request.getRequesterEmail(),
                "Request rejected",
                request.getRequestNo() + " - " + request.getTitle(),
                "REQUEST_REJECTED",
                request
        );

        NeedsRequestModel saved = needsRequestRepository.save(request);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{requestId}/items/{itemId}/quantity")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Long requestId,
            @PathVariable Long itemId,
            @RequestParam BigDecimal quantity) {

        NeedsRequestModel request = needsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if ("APPROVED".equals(request.getStatus())
                || "REJECTED".equals(request.getStatus())) {
            return ResponseEntity.badRequest().body("Cannot modify completed request");
        }

        NeedsRequestItemModel item = needsRequestItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getNeedsRequest() == null
                || !item.getNeedsRequest().getId().equals(requestId)) {
            return ResponseEntity.badRequest().body("Item does not belong to this request");
        }

        BigDecimal unitPrice = safeBigDecimal(item.getUnitPrice());
        BigDecimal safeQuantity = safeBigDecimal(quantity);

        item.setQuantity(safeQuantity);
        item.setTotalAmount(safeQuantity.multiply(unitPrice));

        needsRequestItemRepository.save(item);

        recalculateRequestTotal(request);

        NeedsRequestModel saved = needsRequestRepository.save(request);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{requestId}/items/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long requestId,
            @PathVariable Long itemId) {

        NeedsRequestModel request = needsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if ("APPROVED".equals(request.getStatus())
                || "REJECTED".equals(request.getStatus())) {
            return ResponseEntity.badRequest().body("Cannot modify completed request");
        }

        if (request.getItems() == null || request.getItems().size() <= 1) {
            return ResponseEntity.badRequest().body("You cannot delete all items");
        }

        Optional<NeedsRequestItemModel> optionalItem =
                needsRequestItemRepository.findById(itemId);

        if (optionalItem.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found");
        }

        NeedsRequestItemModel item = optionalItem.get();

        if (item.getNeedsRequest() == null
                || !item.getNeedsRequest().getId().equals(requestId)) {
            return ResponseEntity.badRequest().body("Item does not belong to this request");
        }

        needsRequestItemRepository.delete(item);

        NeedsRequestModel updatedRequest = needsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        recalculateRequestTotal(updatedRequest);

        NeedsRequestModel saved = needsRequestRepository.save(updatedRequest);

        return ResponseEntity.ok(saved);
    }

    private boolean canApproveCurrentLevel(NeedsRequestModel request, String role) {

        if ("ADMIN".equals(role)) {
            return true;
        }

        String level = safe(request.getCurrentApprovalLevel()).toUpperCase();

        if ("HOD".equals(level)) {
            return "HOD".equals(role);
        }

        if ("FINANCE".equals(level)) {
            return "FINANCE".equals(role);
        }

        if ("DIRECTOR".equals(level)) {
            return "DIRECTOR".equals(role);
        }

        return false;
    }

    private String getPendingStatusForRole(String role) {

        if ("HOD".equals(role)) {
            return "PENDING_HOD_APPROVAL";
        }

        if ("FINANCE".equals(role)) {
            return "PENDING_FINANCE_REVIEW";
        }

        if ("DIRECTOR".equals(role)) {
            return "PENDING_DIRECTOR_APPROVAL";
        }

        return "NO_STATUS";
    }

    private void saveHistory(
            NeedsRequestModel request,
            String level,
            String action,
            String actedBy,
            String actedRole,
            String comment) {

        NeedsRequestApprovalHistoryModel history =
                new NeedsRequestApprovalHistoryModel();

        history.setOrganization(request.getOrganization());
        history.setNeedsRequestId(request.getId());
        history.setRequestNo(request.getRequestNo());
        history.setApprovalLevel(level);
        history.setAction(action);
        history.setActedBy(actedBy);
        history.setActedRole(actedRole);
        history.setActionComment(comment);

        approvalHistoryRepository.save(history);
    }

    private void recalculateRequestTotal(NeedsRequestModel request) {

        BigDecimal total = BigDecimal.ZERO;

        if (request.getItems() != null) {
            for (NeedsRequestItemModel item : request.getItems()) {

                BigDecimal quantity = safeBigDecimal(item.getQuantity());
                BigDecimal unitPrice = safeBigDecimal(item.getUnitPrice());
                BigDecimal lineTotal = quantity.multiply(unitPrice);

                item.setQuantity(quantity);
                item.setUnitPrice(unitPrice);
                item.setTotalAmount(lineTotal);

                total = total.add(lineTotal);
            }
        }

        request.setEstimatedAmount(total);
    }

    private BigDecimal safeBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String generateRequestNo(String organization) {

        String year = String.valueOf(LocalDate.now().getYear());

        long count = needsRequestRepository
                .findByOrganizationOrderByIdDesc(organization)
                .size() + 1L;

        return "EB-" + year + "-" + String.format("%04d", count);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}