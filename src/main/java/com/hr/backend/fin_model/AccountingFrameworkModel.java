package com.hr.backend.fin_model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "accounting_frameworks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_organization_framework_code",
                        columnNames = {"organization", "frameworkCode"}
                )
        }
)
public class AccountingFrameworkModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    private String frameworkCode;

    private String frameworkName;

    private String country;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status;

    private String createdDate;

    public AccountingFrameworkModel() {
    }

    public AccountingFrameworkModel(Long id, String organization, String frameworkCode,
                                    String frameworkName, String country, String description,
                                    String status, String createdDate) {
        this.id = id;
        this.organization = organization;
        this.frameworkCode = frameworkCode;
        this.frameworkName = frameworkName;
        this.country = country;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getFrameworkCode() {
        return frameworkCode;
    }

    public String getFrameworkName() {
        return frameworkName;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
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

    public void setFrameworkCode(String frameworkCode) {
        this.frameworkCode = frameworkCode;
    }

    public void setFrameworkName(String frameworkName) {
        this.frameworkName = frameworkName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}