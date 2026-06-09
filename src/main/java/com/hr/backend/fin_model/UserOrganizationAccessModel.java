package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_organization_access",
        uniqueConstraints = @UniqueConstraint(columnNames = {"username", "organization_code"})
)
public class UserOrganizationAccessModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "organization_code", nullable = false)
    private String organizationCode;

    @Column(name = "role_in_organization")
    private String roleInOrganization;

    @Column(name = "default_organization")
    private Boolean defaultOrganization;

    private String status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        normalizeDefaults();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeDefaults();
    }

    private void normalizeDefaults() {
        username = username == null ? null : username.trim().toLowerCase();
        organizationCode = organizationCode == null ? null : organizationCode.trim().toUpperCase();
        if (roleInOrganization == null || roleInOrganization.trim().isEmpty()) {
            roleInOrganization = "USER";
        } else {
            roleInOrganization = roleInOrganization.trim().toUpperCase();
        }
        if (defaultOrganization == null) {
            defaultOrganization = false;
        }
        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE";
        } else {
            status = status.trim().toUpperCase();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOrganizationCode() { return organizationCode; }
    public void setOrganizationCode(String organizationCode) { this.organizationCode = organizationCode; }
    public String getRoleInOrganization() { return roleInOrganization; }
    public void setRoleInOrganization(String roleInOrganization) { this.roleInOrganization = roleInOrganization; }
    public Boolean getDefaultOrganization() { return defaultOrganization; }
    public void setDefaultOrganization(Boolean defaultOrganization) { this.defaultOrganization = defaultOrganization; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
