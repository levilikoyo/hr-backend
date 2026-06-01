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
import java.util.List;

@Entity
@Table(name = "needs_requests")
public class NeedsRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "request_no")
    private String requestNo;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "request_date")
    private LocalDate requestDate;

    private String priority;

    @Column(name = "budget_plan")
    private String budgetPlan;

    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "gl_account_no")
    private String glAccountNo;

    @Column(name = "dimension_values", columnDefinition = "TEXT")
    private String dimensionValues;

    @Column(name = "estimated_amount")
    private BigDecimal estimatedAmount;

    private String status;

    @Column(name = "current_approval_level")
    private String currentApprovalLevel;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "requester_name")
    private String requesterName;

    @Column(name = "requester_email")
    private String requesterEmail;

    @Column(name = "requester_role")
    private String requesterRole;

    @Column(name = "requester_department")
    private String requesterDepartment;

    @Column(name = "addressed_department")
    private String addressedDepartment;

    @Column(name = "created_by")
    private String createdBy;

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

    @OneToMany(mappedBy = "needsRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<NeedsRequestItemModel> items;

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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
        return glAccountNo;
    }

    public void setGlAccountCode(String glAccountCode) {
        this.glAccountNo = glAccountCode;
    }

    public String getDimensionValues() {
        return dimensionValues;
    }

    public void setDimensionValues(String dimensionValues) {
        this.dimensionValues = dimensionValues;
    }

    public BigDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    public void setEstimatedAmount(BigDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(String currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getRequesterRole() {
        return requesterRole;
    }

    public void setRequesterRole(String requesterRole) {
        this.requesterRole = requesterRole;
    }

    public String getRequesterDepartment() {
        return requesterDepartment;
    }

    public void setRequesterDepartment(String requesterDepartment) {
        this.requesterDepartment = requesterDepartment;
    }

    public String getAddressedDepartment() {
        return addressedDepartment;
    }

    public void setAddressedDepartment(String addressedDepartment) {
        this.addressedDepartment = addressedDepartment;
    }

    public String getDepartment() {
        return addressedDepartment;
    }

    public void setDepartment(String department) {
        this.addressedDepartment = department;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getHodApprovedBy() {
        return hodApprovedBy;
    }

    public void setHodApprovedBy(String hodApprovedBy) {
        this.hodApprovedBy = hodApprovedBy;
    }

    public LocalDateTime getHodApprovedAt() {
        return hodApprovedAt;
    }

    public void setHodApprovedAt(LocalDateTime hodApprovedAt) {
        this.hodApprovedAt = hodApprovedAt;
    }

    public String getFinanceReviewedBy() {
        return financeReviewedBy;
    }

    public void setFinanceReviewedBy(String financeReviewedBy) {
        this.financeReviewedBy = financeReviewedBy;
    }

    public LocalDateTime getFinanceReviewedAt() {
        return financeReviewedAt;
    }

    public void setFinanceReviewedAt(LocalDateTime financeReviewedAt) {
        this.financeReviewedAt = financeReviewedAt;
    }

    public String getDirectorApprovedBy() {
        return directorApprovedBy;
    }

    public void setDirectorApprovedBy(String directorApprovedBy) {
        this.directorApprovedBy = directorApprovedBy;
    }

    public LocalDateTime getDirectorApprovedAt() {
        return directorApprovedAt;
    }

    public void setDirectorApprovedAt(LocalDateTime directorApprovedAt) {
        this.directorApprovedAt = directorApprovedAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public List<NeedsRequestItemModel> getItems() {
        return items;
    }

    public void setItems(List<NeedsRequestItemModel> items) {
        this.items = items;
    }
}