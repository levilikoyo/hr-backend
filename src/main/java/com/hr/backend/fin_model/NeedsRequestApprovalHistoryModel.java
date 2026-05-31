/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_model;

/**
 *
 * @author apple
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "needs_request_approval_history")
public class NeedsRequestApprovalHistoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "needs_request_id")
    private Long needsRequestId;

    @Column(name = "request_no")
    private String requestNo;

    @Column(name = "approval_level")
    private String approvalLevel;

    private String action;

    @Column(name = "acted_by")
    private String actedBy;

    @Column(name = "acted_role")
    private String actedRole;

    @Column(name = "action_comment", columnDefinition = "TEXT")
    private String actionComment;

    @Column(name = "acted_at", insertable = false, updatable = false)
    private LocalDateTime actedAt;

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Long getNeedsRequestId() {
        return needsRequestId;
    }

    public void setNeedsRequestId(Long needsRequestId) {
        this.needsRequestId = needsRequestId;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(String approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActedBy() {
        return actedBy;
    }

    public void setActedBy(String actedBy) {
        this.actedBy = actedBy;
    }

    public String getActedRole() {
        return actedRole;
    }

    public void setActedRole(String actedRole) {
        this.actedRole = actedRole;
    }

    public String getActionComment() {
        return actionComment;
    }

    public void setActionComment(String actionComment) {
        this.actionComment = actionComment;
    }

    public LocalDateTime getActedAt() {
        return actedAt;
    }
}
