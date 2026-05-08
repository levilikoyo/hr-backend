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

    @Column(name="fund_code", nullable = false, unique = true)
    private String fundCode;

    @Column(name="fund_name", nullable = false)
    private String fundName;

    private String fundType;
    private String donor;
    private String currency;
    private Integer budgetYear;
    private String grantAgreementNo;
    private String startDate;
    private String closingDate;
    private Boolean restricted;
    private Boolean blocked;
    private String status;
    private String description;
    private String logoPath;
    private String headerPath;
    private String footerPath;
    private BigDecimal budget;
    private BigDecimal commitments;
    private BigDecimal encumbrances;
    private BigDecimal actuals;
    private BigDecimal actualYtd;
    private BigDecimal amountToDemand;
    private String organization;
    private String createdBy;

    public Long getId() {
        return id;
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

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getCommitments() {
        return commitments;
    }

    public void setCommitments(BigDecimal commitments) {
        this.commitments = commitments;
    }

    public BigDecimal getEncumbrances() {
        return encumbrances;
    }

    public void setEncumbrances(BigDecimal encumbrances) {
        this.encumbrances = encumbrances;
    }

    public BigDecimal getActuals() {
        return actuals;
    }

    public void setActuals(BigDecimal actuals) {
        this.actuals = actuals;
    }

    public BigDecimal getActualYtd() {
        return actualYtd;
    }

    public void setActualYtd(BigDecimal actualYtd) {
        this.actualYtd = actualYtd;
    }

    public BigDecimal getAmountToDemand() {
        return amountToDemand;
    }

    public void setAmountToDemand(BigDecimal amountToDemand) {
        this.amountToDemand = amountToDemand;
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
}