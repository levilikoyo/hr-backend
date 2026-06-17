package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_workflow_history")
public class AuditWorkflowModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String organization;
    @Column(name = "audit_code")
    private String auditCode;
    @Column(name = "step_no")
    private Integer stepNo;
    @Column(name = "workflow_action")
    private String workflowAction;
    private String owner;
    private String status;
    @Column(name = "completed_at")
    private String completedAt;
    @Column(columnDefinition = "TEXT")
    private String comment;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getAuditCode() { return auditCode; }
    public void setAuditCode(String auditCode) { this.auditCode = auditCode; }
    public Integer getStepNo() { return stepNo; }
    public void setStepNo(Integer stepNo) { this.stepNo = stepNo; }
    public String getWorkflowAction() { return workflowAction; }
    public void setWorkflowAction(String workflowAction) { this.workflowAction = workflowAction; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
