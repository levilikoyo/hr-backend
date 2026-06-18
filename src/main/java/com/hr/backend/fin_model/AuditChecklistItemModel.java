package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_checklist_items")
public class AuditChecklistItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String organization;
    @Column(name = "audit_code")
    private String auditCode;
    @Column(name = "line_no")
    private Integer lineNo;
    private String reference;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "document_type")
    private String documentType;
    private String responsible;
    @Column(name = "due_date")
    private String dueDate;
    private String status;
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
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAttachment() { return attachment; }
    public void setAttachment(String attachment) { this.attachment = attachment; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getAuditorComment() { return auditorComment; }
    public void setAuditorComment(String auditorComment) { this.auditorComment = auditorComment; }
}
