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
        name = "asset_books",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organization", "asset_code", "book_code"})
        }
)
public class AssetBookModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "asset_code", nullable = false)
    private String assetCode;

    @Column(name = "book_code", nullable = false)
    private String bookCode = "MAIN";

    @Column(name = "acquisition_date")
    private String acquisitionDate;

    @Column(name = "acquisition_cost")
    private BigDecimal acquisitionCost = BigDecimal.ZERO;

    @Column(name = "useful_life_months")
    private Integer usefulLifeMonths = 0;

    @Column(name = "salvage_value")
    private BigDecimal salvageValue = BigDecimal.ZERO;

    @Column(name = "accumulated_depreciation")
    private BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

    @Column(name = "net_book_value")
    private BigDecimal netBookValue = BigDecimal.ZERO;

    @Column(name = "depreciation_method")
    private String depreciationMethod;

    @Column(name = "depreciation_starting_date")
    private String depreciationStartingDate;

    @Column(name = "status")
    private String status = "Active";

    public AssetBookModel() {
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

    public String getAcquisitionDate() {
        return acquisitionDate;
    }

    public BigDecimal getAcquisitionCost() {
        return acquisitionCost;
    }

    public Integer getUsefulLifeMonths() {
        return usefulLifeMonths;
    }

    public BigDecimal getSalvageValue() {
        return salvageValue;
    }

    public BigDecimal getAccumulatedDepreciation() {
        return accumulatedDepreciation;
    }

    public BigDecimal getNetBookValue() {
        return netBookValue;
    }

    public String getDepreciationMethod() {
        return depreciationMethod;
    }

    public String getDepreciationStartingDate() {
        return depreciationStartingDate;
    }

    public String getStatus() {
        return status;
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

    public void setAcquisitionDate(String acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public void setAcquisitionCost(BigDecimal acquisitionCost) {
        this.acquisitionCost = acquisitionCost;
    }

    public void setUsefulLifeMonths(Integer usefulLifeMonths) {
        this.usefulLifeMonths = usefulLifeMonths;
    }

    public void setSalvageValue(BigDecimal salvageValue) {
        this.salvageValue = salvageValue;
    }

    public void setAccumulatedDepreciation(BigDecimal accumulatedDepreciation) {
        this.accumulatedDepreciation = accumulatedDepreciation;
    }

    public void setNetBookValue(BigDecimal netBookValue) {
        this.netBookValue = netBookValue;
    }

    public void setDepreciationMethod(String depreciationMethod) {
        this.depreciationMethod = depreciationMethod;
    }

    public void setDepreciationStartingDate(String depreciationStartingDate) {
        this.depreciationStartingDate = depreciationStartingDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}