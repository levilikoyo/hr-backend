/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_model;

/**
 *
 * @author apple
 */


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "needs_requests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_needs_request_org_no",
                        columnNames = {"organization", "request_no"}
                )
        }
)
public class NeedsRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "request_no")
    private String requestNo;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "requester_email")
    private String requesterEmail;

    private String department;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String priority;

    @Column(name = "budget_plan")
    private String budgetPlan;

    @Column(name = "estimated_amount")
    private BigDecimal estimatedAmount = BigDecimal.ZERO;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "framework_code")
    private String frameworkCode;

    @Column(name = "gl_account_no")
    private String glAccountNo;

    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "dimension_values", columnDefinition = "JSON")
    private String dimensionValues;

    @Column(name = "attachment_name")
    private String attachmentName;

    private String status;

    @Column(name = "current_approval_level")
    private String currentApprovalLevel;

    @Column(name = "hod_approved_by")
    private String hodApprovedBy;

    @Column(name = "hod_approved_at")
    private LocalDateTime hodApprovedAt;

    @Column(name = "finance_reviewed_by")
    private String financeReviewedBy;

    @Column(name = "finance_reviewed_at")
    private LocalDateTime financeReviewedAt;

    @Column(name = "director_approved_by")
    private String directorApprovedBy;

    @Column(name = "director_approved_at")
    private LocalDateTime directorApprovedAt;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "needsRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<NeedsRequestItemModel> items = new ArrayList<>();

    public NeedsRequestModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public LocalDate getRequiredDate() {
        return requiredDate;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public String getDepartment() {
        return department;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getBudgetPlan() {
        return budgetPlan;
    }

    public BigDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getFrameworkCode() {
        return frameworkCode;
    }

    public String getGlAccountNo() {
        return glAccountNo;
    }

    public String getFundCode() {
        return fundCode;
    }

    public String getDimensionValues() {
        return dimensionValues;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public String getHodApprovedBy() {
        return hodApprovedBy;
    }

    public LocalDateTime getHodApprovedAt() {
        return hodApprovedAt;
    }

    public String getFinanceReviewedBy() {
        return financeReviewedBy;
    }

    public LocalDateTime getFinanceReviewedAt() {
        return financeReviewedAt;
    }

    public String getDirectorApprovedBy() {
        return directorApprovedBy;
    }

    public LocalDateTime getDirectorApprovedAt() {
        return directorApprovedAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<NeedsRequestItemModel> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public void setRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setBudgetPlan(String budgetPlan) {
        this.budgetPlan = budgetPlan;
    }

    public void setEstimatedAmount(BigDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setFrameworkCode(String frameworkCode) {
        this.frameworkCode = frameworkCode;
    }

    public void setGlAccountNo(String glAccountNo) {
        this.glAccountNo = glAccountNo;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public void setDimensionValues(String dimensionValues) {
        this.dimensionValues = dimensionValues;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentApprovalLevel(String currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public void setHodApprovedBy(String hodApprovedBy) {
        this.hodApprovedBy = hodApprovedBy;
    }

    public void setHodApprovedAt(LocalDateTime hodApprovedAt) {
        this.hodApprovedAt = hodApprovedAt;
    }

    public void setFinanceReviewedBy(String financeReviewedBy) {
        this.financeReviewedBy = financeReviewedBy;
    }

    public void setFinanceReviewedAt(LocalDateTime financeReviewedAt) {
        this.financeReviewedAt = financeReviewedAt;
    }

    public void setDirectorApprovedBy(String directorApprovedBy) {
        this.directorApprovedBy = directorApprovedBy;
    }

    public void setDirectorApprovedAt(LocalDateTime directorApprovedAt) {
        this.directorApprovedAt = directorApprovedAt;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setItems(List<NeedsRequestItemModel> items) {
        this.items = items;
    }
}