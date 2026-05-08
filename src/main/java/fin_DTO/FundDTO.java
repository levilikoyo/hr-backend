/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fin_DTO;

/**
 *
 * @author apple
 */

import fin_model.Fund;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FundDTO {

    private Long id;
    private String fundCode;
    private String fundName;
    private String fundType;
    private String donor;
    private String currency;
    private Integer budgetYear;
    private String grantAgreementNo;
    private LocalDate startDate;
    private LocalDate closingDate;
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
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public FundDTO() {
    }

    public FundDTO(Fund fund) {
        this.id = fund.getId();
        this.fundCode = fund.getFundCode();
        this.fundName = fund.getFundName();
        this.fundType = fund.getFundType();
        this.donor = fund.getDonor();
        this.currency = fund.getCurrency();
        this.budgetYear = fund.getBudgetYear();
        this.grantAgreementNo = fund.getGrantAgreementNo();
        this.startDate = fund.getStartDate();
        this.closingDate = fund.getClosingDate();
        this.restricted = fund.getRestricted();
        this.blocked = fund.getBlocked();
        this.status = fund.getStatus();
        this.description = fund.getDescription();
        this.logoPath = fund.getLogoPath();
        this.headerPath = fund.getHeaderPath();
        this.footerPath = fund.getFooterPath();
        this.budget = fund.getBudget();
        this.commitments = fund.getCommitments();
        this.encumbrances = fund.getEncumbrances();
        this.actuals = fund.getActuals();
        this.actualYtd = fund.getActualYtd();
        this.amountToDemand = fund.getAmountToDemand();
        this.organization = fund.getOrganization();
        this.createdBy = fund.getCreatedBy();
        this.createdDate = fund.getCreatedDate();
        this.updatedDate = fund.getUpdatedDate();
    }

    public Fund toEntity() {
        Fund fund = new Fund();

        fund.setFundCode(this.fundCode);
        fund.setFundName(this.fundName);
        fund.setFundType(this.fundType);
        fund.setDonor(this.donor);
        fund.setCurrency(this.currency);
        fund.setBudgetYear(this.budgetYear);
        fund.setGrantAgreementNo(this.grantAgreementNo);
        fund.setStartDate(this.startDate);
        fund.setClosingDate(this.closingDate);
        fund.setRestricted(this.restricted);
        fund.setBlocked(this.blocked);
        fund.setStatus(this.status);
        fund.setDescription(this.description);
        fund.setLogoPath(this.logoPath);
        fund.setHeaderPath(this.headerPath);
        fund.setFooterPath(this.footerPath);
        fund.setBudget(this.budget);
        fund.setCommitments(this.commitments);
        fund.setEncumbrances(this.encumbrances);
        fund.setActuals(this.actuals);
        fund.setActualYtd(this.actualYtd);
        fund.setAmountToDemand(this.amountToDemand);
        fund.setOrganization(this.organization);
        fund.setCreatedBy(this.createdBy);

        return fund;
    }

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
}
