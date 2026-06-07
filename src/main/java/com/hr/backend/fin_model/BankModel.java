package com.hr.backend.fin_model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "banks",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organization", "bank_code"})
        }
)
public class BankModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_address")
    private String bankAddress;

    @Column(name = "bank_city")
    private String bankCity;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "gl_account_balance")
    private String glAccountBalance;

    @Column(name = "gl_fund")
    private String glFund;

    @Column(name = "blocked")
    private Boolean blocked = false;

    @Column(name = "starting_date")
    private String startingDate;

    @Column(name = "closing_date")
    private String closingDate;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public String getBankCity() {
        return bankCity;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getGlAccountBalance() {
        return glAccountBalance;
    }

    public String getGlFund() {
        return glFund;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setGlAccountBalance(String glAccountBalance) {
        this.glAccountBalance = glAccountBalance;
    }

    public void setGlFund(String glFund) {
        this.glFund = glFund;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
