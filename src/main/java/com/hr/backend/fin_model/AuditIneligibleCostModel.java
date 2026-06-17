package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "audit_ineligible_costs")
public class AuditIneligibleCostModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String organization;
    @Column(name = "audit_code")
    private String auditCode;
    @Column(name = "ineligible_code")
    private String ineligibleCode;
    @Column(name = "fund_code")
    private String fundCode;
    @Column(name = "transaction_no")
    private String transactionNo;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "original_amount")
    private BigDecimal originalAmount = BigDecimal.ZERO;
    @Column(name = "ineligible_amount")
    private BigDecimal ineligibleAmount = BigDecimal.ZERO;
    @Column(name = "final_amount")
    private BigDecimal finalAmount = BigDecimal.ZERO;
    private String currency;
    @Column(name = "recovery_status")
    private String recoveryStatus;
    private String decision;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getAuditCode() { return auditCode; }
    public void setAuditCode(String auditCode) { this.auditCode = auditCode; }
    public String getIneligibleCode() { return ineligibleCode; }
    public void setIneligibleCode(String ineligibleCode) { this.ineligibleCode = ineligibleCode; }
    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }
    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
    public BigDecimal getIneligibleAmount() { return ineligibleAmount; }
    public void setIneligibleAmount(BigDecimal ineligibleAmount) { this.ineligibleAmount = ineligibleAmount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getRecoveryStatus() { return recoveryStatus; }
    public void setRecoveryStatus(String recoveryStatus) { this.recoveryStatus = recoveryStatus; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
}
