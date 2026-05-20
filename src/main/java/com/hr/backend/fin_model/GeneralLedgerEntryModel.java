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
@Table(name = "general_ledger_entries")
public class GeneralLedgerEntryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "framework_code")
    private String frameworkCode;

    @Column(name = "transaction_type")
    private String transactionType; // ACTUAL, BUDGET, FORECAST, COMMITMENT

    @Column(name = "document_no")
    private String documentNo;

    @Column(name = "posting_date")
    private String postingDate;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "external_document_no")
    private String externalDocumentNo;

    @Column(name = "entry_no")
    private String entryNo;

    private String place;

    @Column(name = "account_category")
    private String accountCategory;

    @Column(name = "account_sub_category")
    private String accountSubCategory;

    @Column(name = "account_class")
    private String accountClass;

    @Column(name = "account_group")
    private String accountGroup;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "normal_balance")
    private String normalBalance;

    @Column(name = "statement_type")
    private String statementType;

    @Column(name = "reporting_category")
    private String reportingCategory;

    private String description;

    @Column(name = "debit_amount")
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(name = "credit_amount")
    private BigDecimal creditAmount = BigDecimal.ZERO;

    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "amount_lcy")
    private BigDecimal amountLcy = BigDecimal.ZERO;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "lcy_currency_code")
    private String lcyCurrencyCode;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate = BigDecimal.ZERO;

    @Column(name = "exchange_rate_date")
    private String exchangeRateDate;

    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "fund_name")
    private String fundName;

    @Column(name = "donor_line_code")
    private String donorLineCode;

    @Column(name = "donor_line_name")
    private String donorLineName;

    @Column(name = "cost_center_code")
    private String costCenterCode;

    @Column(name = "cost_center_name")
    private String costCenterName;

    @Column(name = "bp_mapping_code")
    private String bpMappingCode;

    @Column(name = "bp_mapping_name")
    private String bpMappingName;

    @Column(name = "dimension_speed_code")
    private String dimensionSpeedCode;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "fixed_asset_no")
    private String fixedAssetNo;

    @Column(name = "fixed_asset_name")
    private String fixedAssetName;

    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "activity_code")
    private String activityCode;

    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "department_code")
    private String departmentCode;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "location_code")
    private String locationCode;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "source_document_no")
    private String sourceDocumentNo;

    @Column(name = "source_line_no")
    private String sourceLineNo;

    private Boolean reversed = false;

    private Boolean posted = false;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private String createdDate;
    
    @Column(name = "journal_batch_name")
private String journalBatchName;

    public GeneralLedgerEntryModel() {
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getFrameworkCode() {
        return frameworkCode;
    }

    public void setFrameworkCode(String frameworkCode) {
        this.frameworkCode = frameworkCode;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getExternalDocumentNo() {
        return externalDocumentNo;
    }

    public void setExternalDocumentNo(String externalDocumentNo) {
        this.externalDocumentNo = externalDocumentNo;
    }

    public String getEntryNo() {
        return entryNo;
    }

    public void setEntryNo(String entryNo) {
        this.entryNo = entryNo;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAccountCategory() {
        return accountCategory;
    }

    public void setAccountCategory(String accountCategory) {
        this.accountCategory = accountCategory;
    }

    public String getAccountSubCategory() {
        return accountSubCategory;
    }

    public void setAccountSubCategory(String accountSubCategory) {
        this.accountSubCategory = accountSubCategory;
    }

    public String getAccountClass() {
        return accountClass;
    }

    public void setAccountClass(String accountClass) {
        this.accountClass = accountClass;
    }

    public String getAccountGroup() {
        return accountGroup;
    }

    public void setAccountGroup(String accountGroup) {
        this.accountGroup = accountGroup;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getNormalBalance() {
        return normalBalance;
    }

    public void setNormalBalance(String normalBalance) {
        this.normalBalance = normalBalance;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public String getReportingCategory() {
        return reportingCategory;
    }

    public void setReportingCategory(String reportingCategory) {
        this.reportingCategory = reportingCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountLcy() {
        return amountLcy;
    }

    public void setAmountLcy(BigDecimal amountLcy) {
        this.amountLcy = amountLcy;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getLcyCurrencyCode() {
        return lcyCurrencyCode;
    }

    public void setLcyCurrencyCode(String lcyCurrencyCode) {
        this.lcyCurrencyCode = lcyCurrencyCode;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getExchangeRateDate() {
        return exchangeRateDate;
    }

    public void setExchangeRateDate(String exchangeRateDate) {
        this.exchangeRateDate = exchangeRateDate;
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

    public String getDonorLineCode() {
        return donorLineCode;
    }

    public void setDonorLineCode(String donorLineCode) {
        this.donorLineCode = donorLineCode;
    }

    public String getDonorLineName() {
        return donorLineName;
    }

    public void setDonorLineName(String donorLineName) {
        this.donorLineName = donorLineName;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public String getBpMappingCode() {
        return bpMappingCode;
    }

    public void setBpMappingCode(String bpMappingCode) {
        this.bpMappingCode = bpMappingCode;
    }

    public String getBpMappingName() {
        return bpMappingName;
    }

    public void setBpMappingName(String bpMappingName) {
        this.bpMappingName = bpMappingName;
    }

    public String getDimensionSpeedCode() {
        return dimensionSpeedCode;
    }

    public void setDimensionSpeedCode(String dimensionSpeedCode) {
        this.dimensionSpeedCode = dimensionSpeedCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getFixedAssetNo() {
        return fixedAssetNo;
    }

    public void setFixedAssetNo(String fixedAssetNo) {
        this.fixedAssetNo = fixedAssetNo;
    }

    public String getFixedAssetName() {
        return fixedAssetName;
    }

    public void setFixedAssetName(String fixedAssetName) {
        this.fixedAssetName = fixedAssetName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceDocumentNo() {
        return sourceDocumentNo;
    }

    public void setSourceDocumentNo(String sourceDocumentNo) {
        this.sourceDocumentNo = sourceDocumentNo;
    }

    public String getSourceLineNo() {
        return sourceLineNo;
    }

    public void setSourceLineNo(String sourceLineNo) {
        this.sourceLineNo = sourceLineNo;
    }

    public Boolean getReversed() {
        return reversed;
    }

    public void setReversed(Boolean reversed) {
        this.reversed = reversed;
    }

    public Boolean getPosted() {
        return posted;
    }

    public void setPosted(Boolean posted) {
        this.posted = posted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public String getJournalBatchName() {
    return journalBatchName;
}

public void setJournalBatchName(String journalBatchName) {
    this.journalBatchName = journalBatchName;
}
}
