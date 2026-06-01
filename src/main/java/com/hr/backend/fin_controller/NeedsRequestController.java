

package com.hr.backend.fin_controller;



import com.hr.backend.fin_model.NeedsRequestApprovalHistoryModel;
import com.hr.backend.fin_model.NeedsRequestItemModel;
import com.hr.backend.fin_model.NeedsRequestModel;
import com.hr.backend.fin_repository.NeedsRequestApprovalHistoryRepository;
import com.hr.backend.fin_repository.NeedsRequestItemRepository;
import com.hr.backend.fin_repository.NeedsRequestRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

                if (isBlank(item.getDimensionValues())) {
                    item.setDimensionValues(request.getDimensionValues());
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

        return saved;
    }

    @GetMapping("/organization/{organization}")
    public List<NeedsRequestModel> getByOrganization(@PathVariable String organization) {
        return needsRequestRepository.findByOrganizationOrderByIdDesc(organization);
    }

    @GetMapping("/pending-approval/{organization}")
    public List<NeedsRequestModel> getPendingApproval(@PathVariable String organization) {
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
            return needsRequestRepository.findByOrganizationAndStatusStartingWithOrderByIdDesc(
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
    public List<NeedsRequestApprovalHistoryModel> getHistory(@PathVariable Long id) {

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

        if ("HOD".equals(safe(request.getCurrentApprovalLevel()).toUpperCase())) {

            request.setHodApprovedBy(approvedBy);
            request.setHodApprovedAt(now);
            request.setStatus("PENDING_FINANCE_REVIEW");
            request.setCurrentApprovalLevel("FINANCE");

            saveHistory(request, "HOD", "APPROVED", approvedBy, approverRole, comment);

        } else if ("FINANCE".equals(safe(request.getCurrentApprovalLevel()).toUpperCase())) {

            request.setFinanceReviewedBy(approvedBy);
            request.setFinanceReviewedAt(now);
            request.setStatus("PENDING_DIRECTOR_APPROVAL");
            request.setCurrentApprovalLevel("DIRECTOR");

            saveHistory(request, "FINANCE", "APPROVED", approvedBy, approverRole, comment);

        } else if ("DIRECTOR".equals(safe(request.getCurrentApprovalLevel()).toUpperCase())) {

            request.setDirectorApprovedBy(approvedBy);
            request.setDirectorApprovedAt(now);

            request.setApprovedBy(approvedBy);
            request.setApprovedAt(now);

            request.setStatus("APPROVED");
            request.setCurrentApprovalLevel("COMPLETED");

            saveHistory(request, "DIRECTOR", "APPROVED", approvedBy, approverRole, comment);
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

        if (!canApproveCurrentLevel(request, approverRole)) {
            return ResponseEntity.status(403).body(
                    "You are not allowed to reject this level: "
                            + request.getCurrentApprovalLevel()
            );
        }

        String levelBeforeReject = safe(request.getCurrentApprovalLevel());

        request.setStatus("REJECTED");
        request.setCurrentApprovalLevel("REJECTED");
        request.setRejectedBy(rejectedBy);
        request.setRejectedAt(LocalDateTime.now());
        request.setRejectionReason(reason);

        saveHistory(
                request,
                levelBeforeReject,
                "REJECTED",
                rejectedBy,
                approverRole,
                reason
        );

        NeedsRequestModel saved = needsRequestRepository.save(request);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{requestId}/items/{itemId}/quantity")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Long requestId,
            @PathVariable Long itemId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) String role) {

        NeedsRequestModel request = getRequestOrThrow(requestId);

        if (isCompleted(request)) {
            return ResponseEntity.badRequest().body("Cannot modify completed request");
        }

        String normalizedRole = safe(role).toUpperCase();

        if (!canModifyQuantity(request, normalizedRole)) {
            return ResponseEntity.status(403).body("Only HOD or DIRECTOR can modify quantity at their approval level");
        }

        NeedsRequestItemModel item = getItemOrThrow(requestId, itemId);

        BigDecimal safeQuantity = safeBigDecimal(quantity);
        BigDecimal unitPrice = safeBigDecimal(item.getUnitPrice());

        item.setQuantity(safeQuantity);
        item.setTotalAmount(safeQuantity.multiply(unitPrice));

        needsRequestItemRepository.save(item);

        NeedsRequestModel refreshedRequest = getRequestOrThrow(requestId);
        recalculateRequestTotal(refreshedRequest);

        saveHistory(
                refreshedRequest,
                safe(refreshedRequest.getCurrentApprovalLevel()),
                "UPDATED",
                updatedBy,
                normalizedRole,
                "Quantity updated for item: " + safe(item.getItemName())
        );

        NeedsRequestModel saved = needsRequestRepository.save(refreshedRequest);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{requestId}/finance-fields")
    public ResponseEntity<?> updateFinanceFields(
            @PathVariable Long requestId,
            @RequestBody FinanceUpdateRequest financeUpdateRequest) {

        NeedsRequestModel request = getRequestOrThrow(requestId);

        if (isCompleted(request)) {
            return ResponseEntity.badRequest().body("Cannot modify completed request");
        }

        String role = safe(financeUpdateRequest.getRole()).toUpperCase();

        if (!canModifyFinanceFields(request, role)) {
            return ResponseEntity.status(403).body("Only Finance can modify budget, fund, currency, G/L account and dimensions at Finance level");
        }

        if (!isBlank(financeUpdateRequest.getBudgetPlan())) {
            request.setBudgetPlan(financeUpdateRequest.getBudgetPlan());
        }

        if (!isBlank(financeUpdateRequest.getFundCode())) {
            request.setFundCode(financeUpdateRequest.getFundCode());
        }

        if (!isBlank(financeUpdateRequest.getCurrencyCode())) {
            request.setCurrencyCode(financeUpdateRequest.getCurrencyCode());
        }

        if (!isBlank(financeUpdateRequest.getGlAccountNo())) {
            request.setGlAccountNo(financeUpdateRequest.getGlAccountNo());
        }

        if (!isBlank(financeUpdateRequest.getGlAccountCode())) {
            request.setGlAccountNo(financeUpdateRequest.getGlAccountCode());
        }

        if (!isBlank(financeUpdateRequest.getDimensionValues())) {
            request.setDimensionValues(financeUpdateRequest.getDimensionValues());
        }

        if (request.getItems() != null) {
            for (NeedsRequestItemModel item : request.getItems()) {

                if (!isBlank(financeUpdateRequest.getBudgetPlan())) {
                    item.setBudgetPlan(financeUpdateRequest.getBudgetPlan());
                }

                if (!isBlank(financeUpdateRequest.getFundCode())) {
                    item.setFundCode(financeUpdateRequest.getFundCode());
                }

                if (!isBlank(financeUpdateRequest.getGlAccountNo())) {
                    item.setGlAccountNo(financeUpdateRequest.getGlAccountNo());
                }

                if (!isBlank(financeUpdateRequest.getGlAccountCode())) {
                    item.setGlAccountNo(financeUpdateRequest.getGlAccountCode());
                }

                if (!isBlank(financeUpdateRequest.getDimensionValues())) {
                    item.setDimensionValues(financeUpdateRequest.getDimensionValues());
                }
            }
        }

        recalculateRequestTotal(request);

        saveHistory(
                request,
                "FINANCE",
                "UPDATED",
                financeUpdateRequest.getUpdatedBy(),
                role,
                "Finance fields updated"
        );

        NeedsRequestModel saved = needsRequestRepository.save(request);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{requestId}/items/{itemId}/finance-fields")
    public ResponseEntity<?> updateItemFinanceFields(
            @PathVariable Long requestId,
            @PathVariable Long itemId,
            @RequestBody FinanceUpdateRequest financeUpdateRequest) {

        NeedsRequestModel request = getRequestOrThrow(requestId);

        if (isCompleted(request)) {
            return ResponseEntity.badRequest().body("Cannot modify completed request");
        }

        String role = safe(financeUpdateRequest.getRole()).toUpperCase();

        if (!canModifyFinanceFields(request, role)) {
            return ResponseEntity.status(403).body("Only Finance can modify unit price and item finance fields at Finance level");
        }

        NeedsRequestItemModel item = getItemOrThrow(requestId, itemId);

        if (financeUpdateRequest.getUnitPrice() != null) {
            BigDecimal unitPrice = safeBigDecimal(financeUpdateRequest.getUnitPrice());
            BigDecimal quantity = safeBigDecimal(item.getQuantity());

            item.setUnitPrice(unitPrice);
            item.setTotalAmount(quantity.multiply(unitPrice));
        }

        if (!isBlank(financeUpdateRequest.getBudgetPlan())) {
            item.setBudgetPlan(financeUpdateRequest.getBudgetPlan());
        }

        if (!isBlank(financeUpdateRequest.getFundCode())) {
            item.setFundCode(financeUpdateRequest.getFundCode());
        }

        if (!isBlank(financeUpdateRequest.getGlAccountNo())) {
            item.setGlAccountNo(financeUpdateRequest.getGlAccountNo());
        }

        if (!isBlank(financeUpdateRequest.getGlAccountCode())) {
            item.setGlAccountNo(financeUpdateRequest.getGlAccountCode());
        }

        if (!isBlank(financeUpdateRequest.getDimensionValues())) {
            item.setDimensionValues(financeUpdateRequest.getDimensionValues());
        }

        needsRequestItemRepository.save(item);

        NeedsRequestModel refreshedRequest = getRequestOrThrow(requestId);
        recalculateRequestTotal(refreshedRequest);

        saveHistory(
                refreshedRequest,
                "FINANCE",
                "UPDATED",
                financeUpdateRequest.getUpdatedBy(),
                role,
                "Unit price / finance fields updated for item: " + safe(item.getItemName())
        );

        NeedsRequestModel saved = needsRequestRepository.save(refreshedRequest);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{requestId}/items/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long requestId,
            @PathVariable Long itemId,
            @RequestParam(required = false) String deletedBy,
            @RequestParam(required = false) String role) {

        NeedsRequestModel request = getRequestOrThrow(requestId);

        if (isCompleted(request)) {
            return ResponseEntity.badRequest().body("Cannot modify completed request");
        }

        if (request.getItems() == null || request.getItems().size() <= 1) {
            return ResponseEntity.badRequest().body("You cannot delete all items");
        }

        Optional<NeedsRequestItemModel> optionalItem = needsRequestItemRepository.findById(itemId);

        if (optionalItem.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found");
        }

        NeedsRequestItemModel item = optionalItem.get();

        if (item.getNeedsRequest() == null || !item.getNeedsRequest().getId().equals(requestId)) {
            return ResponseEntity.badRequest().body("Item does not belong to this request");
        }

        needsRequestItemRepository.delete(item);

        NeedsRequestModel updatedRequest = getRequestOrThrow(requestId);
        recalculateRequestTotal(updatedRequest);

        saveHistory(
                updatedRequest,
                safe(updatedRequest.getCurrentApprovalLevel()),
                "UPDATED",
                deletedBy,
                safe(role).toUpperCase(),
                "Item deleted: " + safe(item.getItemName())
        );

        NeedsRequestModel saved = needsRequestRepository.save(updatedRequest);

        return ResponseEntity.ok(saved);
    }

    private NeedsRequestModel getRequestOrThrow(Long requestId) {
        return needsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    private NeedsRequestItemModel getItemOrThrow(Long requestId, Long itemId) {

        NeedsRequestItemModel item = needsRequestItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getNeedsRequest() == null || !item.getNeedsRequest().getId().equals(requestId)) {
            throw new RuntimeException("Item does not belong to this request");
        }

        return item;
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

    private boolean canModifyQuantity(NeedsRequestModel request, String role) {

        if ("ADMIN".equals(role)) {
            return true;
        }

        String level = safe(request.getCurrentApprovalLevel()).toUpperCase();

        if ("HOD".equals(level)) {
            return "HOD".equals(role);
        }

        if ("DIRECTOR".equals(level)) {
            return "DIRECTOR".equals(role);
        }

        return false;
    }

    private boolean canModifyFinanceFields(NeedsRequestModel request, String role) {

        if ("ADMIN".equals(role)) {
            return true;
        }

        String level = safe(request.getCurrentApprovalLevel()).toUpperCase();

        return "FINANCE".equals(level) && "FINANCE".equals(role);
    }

    private boolean isCompleted(NeedsRequestModel request) {
        String status = safe(request.getStatus()).toUpperCase();
        return "APPROVED".equals(status) || "REJECTED".equals(status);
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

        NeedsRequestApprovalHistoryModel history = new NeedsRequestApprovalHistoryModel();

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

    public static class FinanceUpdateRequest {

        private String updatedBy;
        private String role;
        private BigDecimal unitPrice;
        private String budgetPlan;
        private String fundCode;
        private String currencyCode;
        private String glAccountNo;
        private String glAccountCode;
        private String dimensionValues;
        private Map<String, String> dimensions;

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getBudgetPlan() {
            return budgetPlan;
        }

        public void setBudgetPlan(String budgetPlan) {
            this.budgetPlan = budgetPlan;
        }

        public String getFundCode() {
            return fundCode;
        }

        public void setFundCode(String fundCode) {
            this.fundCode = fundCode;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public String getGlAccountNo() {
            return glAccountNo;
        }

        public void setGlAccountNo(String glAccountNo) {
            this.glAccountNo = glAccountNo;
        }

        public String getGlAccountCode() {
            return glAccountCode;
        }

        public void setGlAccountCode(String glAccountCode) {
            this.glAccountCode = glAccountCode;
        }

        public String getDimensionValues() {
            return dimensionValues;
        }

        public void setDimensionValues(String dimensionValues) {
            this.dimensionValues = dimensionValues;
        }

        public Map<String, String> getDimensions() {
            return dimensions;
        }

        public void setDimensions(Map<String, String> dimensions) {
            this.dimensions = dimensions;
        }
    }
}