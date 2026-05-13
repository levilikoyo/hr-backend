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
import java.math.BigDecimal;

@Entity
@Table(
        name = "exchange_rates",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {
                "organization",
                "currency_code",
                "exchange_currency_date"
            })
        }
)
public class ExchangeRateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "currency_symbole")
    private String currencySymbole;

    @Column(name = "currency_name")
    private String currencyName;

    @Column(name = "exchange_currency_date", nullable = false)
    private String exchangeCurrencyDate;

    @Column(name = "actual_exchange_rate_unity")
    private BigDecimal actualExchangeRateUnity;

    @Column(name = "actual_exchange_rate_amount")
    private BigDecimal actualExchangeRateAmount;

    @Column(name = "budget_exchange_rate_unity")
    private BigDecimal budgetExchangeRateUnity;

    @Column(name = "budget_exchange_rate_amount")
    private BigDecimal budgetExchangeRateAmount;

    public ExchangeRateModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencySymbole() {
        return currencySymbole;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getExchangeCurrencyDate() {
        return exchangeCurrencyDate;
    }

    public BigDecimal getActualExchangeRateUnity() {
        return actualExchangeRateUnity;
    }

    public BigDecimal getActualExchangeRateAmount() {
        return actualExchangeRateAmount;
    }

    public BigDecimal getBudgetExchangeRateUnity() {
        return budgetExchangeRateUnity;
    }

    public BigDecimal getBudgetExchangeRateAmount() {
        return budgetExchangeRateAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setCurrencySymbole(String currencySymbole) {
        this.currencySymbole = currencySymbole;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public void setExchangeCurrencyDate(String exchangeCurrencyDate) {
        this.exchangeCurrencyDate = exchangeCurrencyDate;
    }

    public void setActualExchangeRateUnity(BigDecimal actualExchangeRateUnity) {
        this.actualExchangeRateUnity = actualExchangeRateUnity;
    }

    public void setActualExchangeRateAmount(BigDecimal actualExchangeRateAmount) {
        this.actualExchangeRateAmount = actualExchangeRateAmount;
    }

    public void setBudgetExchangeRateUnity(BigDecimal budgetExchangeRateUnity) {
        this.budgetExchangeRateUnity = budgetExchangeRateUnity;
    }

    public void setBudgetExchangeRateAmount(BigDecimal budgetExchangeRateAmount) {
        this.budgetExchangeRateAmount = budgetExchangeRateAmount;
    }
}
