package com.hr.backend.fin_model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "gl_accounts",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_org_framework_gl_code",
            columnNames = {"organization", "framework_code", "gl_code"}
        )
    }
)
public class GLAccountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "framework_code", nullable = false)
    private String frameworkCode;

    @Column(name = "gl_group_code")
    private String glGroupCode;

    @Column(name = "gl_group_name")
    private String glGroupName;

    @Column(name = "gl_code", nullable = false)
    private String glCode;

    @Column(name = "gl_name", nullable = false)
    private String glName;

    @Column(name = "income_balance")
    private String incomeBalance;

    @Column(name = "account_category")
    private String accountCategory;

    @Column(name = "account_type")
    private String accountType;

    private Boolean blocked = false;

    private BigDecimal budget = BigDecimal.ZERO;
    private BigDecimal commitments = BigDecimal.ZERO;
    private BigDecimal encumbrances = BigDecimal.ZERO;
    private BigDecimal actual = BigDecimal.ZERO;

    @Column(name = "actual_ytd")
    private BigDecimal actualYtd = BigDecimal.ZERO;

    @Column(name = "amount_to_demand")
    private BigDecimal amountToDemand = BigDecimal.ZERO;

    private BigDecimal available = BigDecimal.ZERO;

    @Column(name = "lcy_balance")
    private BigDecimal lcyBalance = BigDecimal.ZERO;

    @Column(name = "currency_net_change")
    private BigDecimal currencyNetChange = BigDecimal.ZERO;

    @Column(name = "currency_balance")
    private BigDecimal currencyBalance = BigDecimal.ZERO;

    public GLAccountModel() {
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

    public String getGlGroupCode() {
        return glGroupCode;
    }

    public String getGlGroupName() {
        return glGroupName;
    }

    public String getGlCode() {
        return glCode;
    }

    public String getGlName() {
        return glName;
    }

    public String getIncomeBalance() {
        return incomeBalance;
    }

    public String getAccountCategory() {
        return accountCategory;
    }

    public String getAccountType() {
        return accountType;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public BigDecimal getCommitments() {
        return commitments;
    }

    public BigDecimal getEncumbrances() {
        return encumbrances;
    }

    public BigDecimal getActual() {
        return actual;
    }

    public BigDecimal getActualYtd() {
        return actualYtd;
    }

    public BigDecimal getAmountToDemand() {
        return amountToDemand;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public BigDecimal getLcyBalance() {
        return lcyBalance;
    }

    public BigDecimal getCurrencyNetChange() {
        return currencyNetChange;
    }

    public BigDecimal getCurrencyBalance() {
        return currencyBalance;
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

    public void setGlGroupCode(String glGroupCode) {
        this.glGroupCode = glGroupCode;
    }

    public void setGlGroupName(String glGroupName) {
        this.glGroupName = glGroupName;
    }

    public void setGlCode(String glCode) {
        this.glCode = glCode;
    }

    public void setGlName(String glName) {
        this.glName = glName;
    }

    public void setIncomeBalance(String incomeBalance) {
        this.incomeBalance = incomeBalance;
    }

    public void setAccountCategory(String accountCategory) {
        this.accountCategory = accountCategory;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public void setCommitments(BigDecimal commitments) {
        this.commitments = commitments;
    }

    public void setEncumbrances(BigDecimal encumbrances) {
        this.encumbrances = encumbrances;
    }

    public void setActual(BigDecimal actual) {
        this.actual = actual;
    }

    public void setActualYtd(BigDecimal actualYtd) {
        this.actualYtd = actualYtd;
    }

    public void setAmountToDemand(BigDecimal amountToDemand) {
        this.amountToDemand = amountToDemand;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public void setLcyBalance(BigDecimal lcyBalance) {
        this.lcyBalance = lcyBalance;
    }

    public void setCurrencyNetChange(BigDecimal currencyNetChange) {
        this.currencyNetChange = currencyNetChange;
    }

    public void setCurrencyBalance(BigDecimal currencyBalance) {
        this.currencyBalance = currencyBalance;
    }
}