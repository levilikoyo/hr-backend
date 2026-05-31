/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.NeedsRequestItemModel;
import com.hr.backend.fin_model.NeedsRequestModel;
import com.hr.backend.fin_repository.NeedsRequestItemRepository;
import com.hr.backend.fin_repository.NeedsRequestRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/needs-requests")
@CrossOrigin(origins = "*")
public class NeedsRequestController {

    @Autowired
    private NeedsRequestRepository needsRequestRepository;

    @Autowired
    private NeedsRequestItemRepository needsRequestItemRepository;

    @GetMapping("/test")
    public String test() {
        return "Needs Requests API is working";
    }

    @PostMapping
    public NeedsRequestModel create(@RequestBody NeedsRequestModel request) {

        if (request.getRequestNo() == null || request.getRequestNo().trim().isEmpty()) {
            request.setRequestNo(generateRequestNo(request.getOrganization()));
        }

        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDate.now());
        }

        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            request.setStatus("PENDING_APPROVAL");
        }

        BigDecimal total = BigDecimal.ZERO;

        if (request.getItems() != null) {
            for (NeedsRequestItemModel item : request.getItems()) {

                item.setNeedsRequest(request);
                item.setOrganization(request.getOrganization());

                if (item.getBudgetPlan() == null || item.getBudgetPlan().trim().isEmpty()) {
                    item.setBudgetPlan(request.getBudgetPlan());
                }

                if (item.getGlAccountNo() == null || item.getGlAccountNo().trim().isEmpty()) {
                    item.setGlAccountNo(request.getGlAccountNo());
                }

                if (item.getFundCode() == null || item.getFundCode().trim().isEmpty()) {
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

        return needsRequestRepository.save(request);
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
                "PENDING_APPROVAL"
        );
    }

    @PutMapping("/{id}/approve")
    public NeedsRequestModel approve(
            @PathVariable Long id,
            @RequestParam(required = false) String approvedBy) {

        NeedsRequestModel request = needsRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        recalculateRequestTotal(request);

        request.setStatus("APPROVED");
        request.setApprovedBy(approvedBy == null ? "Mobile Approver" : approvedBy);
        request.setApprovedAt(LocalDateTime.now());

        return needsRequestRepository.save(request);
    }

    @PutMapping("/{id}/reject")
    public NeedsRequestModel reject(
            @PathVariable Long id,
            @RequestParam(required = false) String rejectedBy,
            @RequestParam(required = false) String reason) {

        NeedsRequestModel request = needsRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED");
        request.setRejectedBy(rejectedBy == null ? "Mobile Approver" : rejectedBy);
        request.setRejectedAt(LocalDateTime.now());
        request.setRejectionReason(reason);

        return needsRequestRepository.save(request);
    }

    @PutMapping("/{requestId}/items/{itemId}/quantity")
    public NeedsRequestModel updateItemQuantity(
            @PathVariable Long requestId,
            @PathVariable Long itemId,
            @RequestParam BigDecimal quantity) {

        NeedsRequestItemModel item = needsRequestItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getNeedsRequest().getId().equals(requestId)) {
            throw new RuntimeException("Item does not belong to this request");
        }

        BigDecimal unitPrice = safeBigDecimal(item.getUnitPrice());
        BigDecimal safeQuantity = safeBigDecimal(quantity);

        item.setQuantity(safeQuantity);
        item.setTotalAmount(safeQuantity.multiply(unitPrice));

        needsRequestItemRepository.save(item);

        NeedsRequestModel request = needsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        recalculateRequestTotal(request);

        return needsRequestRepository.save(request);
    }

    @DeleteMapping("/{requestId}/items/{itemId}")
    public NeedsRequestModel deleteItem(
            @PathVariable Long requestId,
            @PathVariable Long itemId) {

        NeedsRequestModel request = needsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getItems() == null || request.getItems().size() <= 1) {
            throw new RuntimeException("You cannot delete all items");
        }

        Optional<NeedsRequestItemModel> optionalItem =
                needsRequestItemRepository.findById(itemId);

        if (optionalItem.isEmpty()) {
            throw new RuntimeException("Item not found");
        }

        NeedsRequestItemModel item = optionalItem.get();

        if (!item.getNeedsRequest().getId().equals(requestId)) {
            throw new RuntimeException("Item does not belong to this request");
        }

        needsRequestItemRepository.delete(item);

        NeedsRequestModel updatedRequest = needsRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        recalculateRequestTotal(updatedRequest);

        return needsRequestRepository.save(updatedRequest);
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
}
