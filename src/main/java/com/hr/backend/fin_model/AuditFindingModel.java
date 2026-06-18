package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "audit_findings")
public class AuditFindingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String organization;
    @Column(name = "audit_code")
    private String auditCode;
    @Column(name = "finding_code")
    private String findingCode;
    @Column(name = "transaction_no")
    private String transactionNo;
    @Column(name = "gl_account")
    private String glAccount;
    @Column(name = "vendor_employee")
    private String vendorEmployee;
    @Column(columnDefinition = "TEXT")
    private String description;
    private BigDecimal amount = BigDecimal.ZERO;
    private String currency;
    @Column(name = "risk_level")
    private String riskLevel;
    private String status;
    private String source;
    @Column(columnDefinition = "TEXT")
    private String attachment;
    @Column(columnDefinition = "TEXT")
    private String comment;
    @Column(name = "auditor_comment", columnDefinition = "TEXT")
    private String auditorComment;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getAuditCode() { return auditCode; }
    public void setAuditCode(String auditCode) { this.auditCode = auditCode; }
    public String getFindingCode() { return findingCode; }
    public void setFindingCode(String findingCode) { this.findingCode = findingCode; }
    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }
    public String getGlAccount() { return glAccount; }
    public void setGlAccount(String glAccount) { this.glAccount = glAccount; }
    public String getVendorEmployee() { return vendorEmployee; }
    public void setVendorEmployee(String vendorEmployee) { this.vendorEmployee = vendorEmployee; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getAttachment() { return attachment; }
    public void setAttachment(String attachment) { this.attachment = attachment; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getAuditorComment() { return auditorComment; }
    public void setAuditorComment(String auditorComment) { this.auditorComment = auditorComment; }
}
