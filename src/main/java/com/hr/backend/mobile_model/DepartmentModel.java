/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.mobile_model;

/**
 *
 * @author apple
 */
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_department_org_code",
                        columnNames = {"organization", "department_code"}
                )
        }
)
public class DepartmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization", nullable = false, length = 150)
    private String organization;

    @Column(name = "department_code", nullable = false, length = 100)
    private String departmentCode;

    @Column(name = "department_name", nullable = false, length = 200)
    private String departmentName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "blocked")
    private Boolean blocked = false;

    @Column(name = "status", length = 50)
    private String status = "ACTIVE";

    @Column(name = "created_by", length = 150)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_by", length = 150)
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }

        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE";
        }

        if (blocked == null) {
            blocked = false;
        }

        normalizeFields();
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();

        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE";
        }

        if (blocked == null) {
            blocked = false;
        }

        normalizeFields();
    }

    private void normalizeFields() {
        if (organization != null) {
            organization = organization.trim();
        }

        if (departmentCode != null) {
            departmentCode = departmentCode.trim().toUpperCase();
        }

        if (departmentName != null) {
            departmentName = departmentName.trim();
        }

        if (status != null) {
            status = status.trim().toUpperCase();
        }
    }

    public DepartmentModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
