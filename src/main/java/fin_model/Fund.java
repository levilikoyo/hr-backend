/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fin_model;

/**
 *
 * @author apple
 */

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "funds")
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="fund_code", nullable = false, unique = true, length = 50)
    private String fundCode;

    @Column(name="fund_name", nullable = false)
    private String fundName;

    @Column(name="fund_type")
    private String fundType;

    @Column(name="donor")
    private String donor;

    @Column(name="currency")
    private String currency;

    @Column(name="budget_year")
    private Integer budgetYear;

    @Column(name="grant_agreement_no")
    private String grantAgreementNo;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="closing_date")
    private LocalDate closingDate;

    @Column(name="restricted")
    private Boolean restricted = true;

    @Column(name="blocked")
    private Boolean blocked = false;

    @Column(name="status")
    private String status = "ACTIVE";

    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @Column(name="logo_path")
    private String logoPath;

    @Column(name="header_path")
    private String headerPath;

    @Column(name="footer_path")
    private String footerPath;

    @Column(name="budget", precision = 18, scale = 2)
    private BigDecimal budget = BigDecimal.ZERO;

    @Column(name="commitments", precision = 18, scale = 2)
    private BigDecimal commitments = BigDecimal.ZERO;

    @Column(name="encumbrances", precision = 18, scale = 2)
    private BigDecimal encumbrances = BigDecimal.ZERO;

    @Column(name="actuals", precision = 18, scale = 2)
    private BigDecimal actuals = BigDecimal.ZERO;

    @Column(name="actual_ytd", precision = 18, scale = 2)
    private BigDecimal actualYtd = BigDecimal.ZERO;

    @Column(name="amount_to_demand", precision = 18, scale = 2)
    private BigDecimal amountToDemand = BigDecimal.ZERO;

    @Column(name="organization")
    private String organization;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="created_date")
    private LocalDateTime createdDate;

    @Column(name="updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void onCreate() {
        createdDate = LocalDateTime.now();

        if (restricted == null) restricted = true;
        if (blocked == null) blocked = false;
        if (status == null || status.trim().isEmpty()) status = "ACTIVE";

        if (budget == null) budget = BigDecimal.ZERO;
        if (commitments == null) commitments = BigDecimal.ZERO;
        if (encumbrances == null) encumbrances = BigDecimal.ZERO;
        if (actuals == null) actuals = BigDecimal.ZERO;
        if (actualYtd == null) actualYtd = BigDecimal.ZERO;
        if (amountToDemand == null) amountToDemand = BigDecimal.ZERO;
    }

    @PreUpdate
    public void onUpdate() {
        updatedDate = LocalDateTime.now();
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
