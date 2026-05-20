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
        name = "actual_general_journal_batches",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {
                "organization",
                "framework_code",
                "batch_name"
            })
        }
)
public class ActualGeneralJournalBatchModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "framework_code", nullable = false)
    private String frameworkCode;

    @Column(name = "batch_name", nullable = false)
    private String batchName;

    private String description;

    @Column(name = "no_series")
    private String noSeries;

    @Column(name = "balance_account_type")
    private String balanceAccountType;

    @Column(name = "balance_account_no")
    private String balanceAccountNo;

    @Column(name = "balance_account_name")
    private String balanceAccountName;

    @Column(name = "control_fund_no")
    private String controlFundNo;

    @Column(name = "control_fund_name")
    private String controlFundName;

    private String status = "Active";

    @Column(name = "created_date")
    private String createdDate;

    public ActualGeneralJournalBatchModel() {
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

    public String getBatchName() {
        return batchName;
    }

    public String getDescription() {
        return description;
    }

    public String getNoSeries() {
        return noSeries;
    }

    public String getBalanceAccountType() {
        return balanceAccountType;
    }

    public String getBalanceAccountNo() {
        return balanceAccountNo;
    }

    public String getBalanceAccountName() {
        return balanceAccountName;
    }

    public String getControlFundNo() {
        return controlFundNo;
    }

    public String getControlFundName() {
        return controlFundName;
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

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNoSeries(String noSeries) {
        this.noSeries = noSeries;
    }

    public void setBalanceAccountType(String balanceAccountType) {
        this.balanceAccountType = balanceAccountType;
    }

    public void setBalanceAccountNo(String balanceAccountNo) {
        this.balanceAccountNo = balanceAccountNo;
    }

    public void setBalanceAccountName(String balanceAccountName) {
        this.balanceAccountName = balanceAccountName;
    }

    public void setControlFundNo(String controlFundNo) {
        this.controlFundNo = controlFundNo;
    }

    public void setControlFundName(String controlFundName) {
        this.controlFundName = controlFundName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
