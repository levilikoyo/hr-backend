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

@Entity
@Table(
        name = "account_reporting_mappings",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {
                    "organization",
                    "source_framework_code",
                    "source_gl_code",
                    "target_framework_code"
            })
        }
)
public class AccountReportingMappingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "source_framework_code", nullable = false)
    private String sourceFrameworkCode;

    @Column(name = "source_gl_code", nullable = false)
    private String sourceGlCode;

    @Column(name = "source_gl_name")
    private String sourceGlName;

    @Column(name = "target_framework_code", nullable = false)
    private String targetFrameworkCode;

    @Column(name = "target_reporting_code")
    private String targetReportingCode;

    @Column(name = "target_reporting_name")
    private String targetReportingName;

    @Column(name = "target_category")
    private String targetCategory;

    private String status = "Active";

    @Column(name = "created_date")
    private String createdDate;

    public AccountReportingMappingModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getSourceFrameworkCode() {
        return sourceFrameworkCode;
    }

    public String getSourceGlCode() {
        return sourceGlCode;
    }

    public String getSourceGlName() {
        return sourceGlName;
    }

    public String getTargetFrameworkCode() {
        return targetFrameworkCode;
    }

    public String getTargetReportingCode() {
        return targetReportingCode;
    }

    public String getTargetReportingName() {
        return targetReportingName;
    }

    public String getTargetCategory() {
        return targetCategory;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setSourceFrameworkCode(String sourceFrameworkCode) {
        this.sourceFrameworkCode = sourceFrameworkCode;
    }

    public void setSourceGlCode(String sourceGlCode) {
        this.sourceGlCode = sourceGlCode;
    }

    public void setSourceGlName(String sourceGlName) {
        this.sourceGlName = sourceGlName;
    }

    public void setTargetFrameworkCode(String targetFrameworkCode) {
        this.targetFrameworkCode = targetFrameworkCode;
    }

    public void setTargetReportingCode(String targetReportingCode) {
        this.targetReportingCode = targetReportingCode;
    }

    public void setTargetReportingName(String targetReportingName) {
        this.targetReportingName = targetReportingName;
    }

    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}