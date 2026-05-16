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
        name = "asset_depreciation_schedule",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {
                "organization",
                "asset_code",
                "book_code",
                "depreciation_period"
            })
        }
)
public class AssetDepreciationScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "asset_code", nullable = false)
    private String assetCode;

    @Column(name = "book_code", nullable = false)
    private String bookCode;

    @Column(name = "depreciation_period", nullable = false)
    private String depreciationPeriod; // example: 2026-01

    @Column(name = "period_start_date")
    private String periodStartDate;

    @Column(name = "period_end_date")
    private String periodEndDate;

    @Column(name = "depreciation_amount")
    private BigDecimal depreciationAmount = BigDecimal.ZERO;

    @Column(name = "accumulated_depreciation")
    private BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

    @Column(name = "net_book_value")
    private BigDecimal netBookValue = BigDecimal.ZERO;

    @Column(name = "posted")
    private Boolean posted = false;

    @Column(name = "posting_document_no")
    private String postingDocumentNo;

    @Column(name = "posting_date")
    private String postingDate;

    public AssetDepreciationScheduleModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getBookCode() {
        return bookCode;
    }

    public String getDepreciationPeriod() {
        return depreciationPeriod;
    }

    public String getPeriodStartDate() {
        return periodStartDate;
    }

    public String getPeriodEndDate() {
        return periodEndDate;
    }

    public BigDecimal getDepreciationAmount() {
        return depreciationAmount;
    }

    public BigDecimal getAccumulatedDepreciation() {
        return accumulatedDepreciation;
    }

    public BigDecimal getNetBookValue() {
        return netBookValue;
    }

    public Boolean getPosted() {
        return posted;
    }

    public String getPostingDocumentNo() {
        return postingDocumentNo;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public void setDepreciationPeriod(String depreciationPeriod) {
        this.depreciationPeriod = depreciationPeriod;
    }

    public void setPeriodStartDate(String periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public void setPeriodEndDate(String periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public void setDepreciationAmount(BigDecimal depreciationAmount) {
        this.depreciationAmount = depreciationAmount;
    }

    public void setAccumulatedDepreciation(BigDecimal accumulatedDepreciation) {
        this.accumulatedDepreciation = accumulatedDepreciation;
    }

    public void setNetBookValue(BigDecimal netBookValue) {
        this.netBookValue = netBookValue;
    }

    public void setPosted(Boolean posted) {
        this.posted = posted;
    }

    public void setPostingDocumentNo(String postingDocumentNo) {
        this.postingDocumentNo = postingDocumentNo;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }
}
