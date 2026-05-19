/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_model;

/**
 *
 * @author apple
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.math.BigDecimal;

@Entity
@Table(name = "funds")
public class FundModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "fund_name")
    private String fundName;

    @Column(name = "fund_type")
    private String fundType;

    @Column(name = "donor")
    private String donor;

    @Column(name = "currency")
    private String currency;

    @Column(name = "budget_year")
    private Integer budgetYear;

    @Column(name = "grant_agreement_no")
    private String grantAgreementNo;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "closing_date")
    private String closingDate;

    @Column(name = "restricted")
    private Boolean restricted;

    @Column(name = "blocked")
    private Boolean blocked;

    @Column(name = "status")
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_path", columnDefinition = "TEXT")
    private String logoPath;

    @Column(name = "header_path", columnDefinition = "TEXT")
    private String headerPath;

    @Column(name = "footer_path", columnDefinition = "TEXT")
    private String footerPath;

    @Column(name = "organization")
    private String organization;

    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "curency_code")
private String curencyCode;

@Column(name = "curency_name")
private String curencyName;

@Column(name = "curency_symbole")
private String curencySymbole;

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getFundType() {
        return fundType;
    }

    public void setFundType(String fundType) {
        this.fundType = fundType;
    }

    public String getDonor() {
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getBudgetYear() {
        return budgetYear;
    }

    public void setBudgetYear(Integer budgetYear) {
        this.budgetYear = budgetYear;
    }

    public String getGrantAgreementNo() {
        return grantAgreementNo;
    }

    public void setGrantAgreementNo(String grantAgreementNo) {
        this.grantAgreementNo = grantAgreementNo;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getHeaderPath() {
        return headerPath;
    }

    public void setHeaderPath(String headerPath) {
        this.headerPath = headerPath;
    }

    public String getFooterPath() {
        return footerPath;
    }

    public void setFooterPath(String footerPath) {
        this.footerPath = footerPath;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCurencyCode() {
    return curencyCode;
}

public void setCurencyCode(String curencyCode) {
    this.curencyCode = curencyCode;
}

public String getCurencyName() {
    return curencyName;
}

public void setCurencyName(String curencyName) {
    this.curencyName = curencyName;
}

public String getCurencySymbole() {
    return curencySymbole;
}

public void setCurencySymbole(String curencySymbole) {
    this.curencySymbole = curencySymbole;
}
}
