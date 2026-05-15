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
        name = "fixed_assets",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organization", "asset_code"})
        }
)
public class FixedAssetModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "asset_code", nullable = false)
    private String assetCode;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "asset_description")
    private String assetDescription;

    @Column(name = "asset_sub_class")
    private String assetSubClass;

    @Column(name = "serial_no")
    private String serialNo;

    @Column(name = "asset_tag_num")
    private String assetTagNum;

    @Column(name = "responsible_employee")
    private String responsibleEmployee;

    @Column(name = "depreciation_method")
    private String depreciationMethod;

    @Column(name = "depreciation_starting_date")
    private String depreciationStartingDate;

    @Column(name = "book_value")
    private BigDecimal bookValue;

    @Column(name = "status")
    private String status;

    public FixedAssetModel() {
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

    public String getAssetName() {
        return assetName;
    }

    public String getAssetDescription() {
        return assetDescription;
    }

    public String getAssetSubClass() {
        return assetSubClass;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public String getAssetTagNum() {
        return assetTagNum;
    }

    public String getResponsibleEmployee() {
        return responsibleEmployee;
    }

    public String getDepreciationMethod() {
        return depreciationMethod;
    }

    public String getDepreciationStartingDate() {
        return depreciationStartingDate;
    }

    public BigDecimal getBookValue() {
        return bookValue;
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

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setAssetDescription(String assetDescription) {
        this.assetDescription = assetDescription;
    }

    public void setAssetSubClass(String assetSubClass) {
        this.assetSubClass = assetSubClass;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public void setAssetTagNum(String assetTagNum) {
        this.assetTagNum = assetTagNum;
    }

    public void setResponsibleEmployee(String responsibleEmployee) {
        this.responsibleEmployee = responsibleEmployee;
    }

    public void setDepreciationMethod(String depreciationMethod) {
        this.depreciationMethod = depreciationMethod;
    }

    public void setDepreciationStartingDate(String depreciationStartingDate) {
        this.depreciationStartingDate = depreciationStartingDate;
    }

    public void setBookValue(BigDecimal bookValue) {
        this.bookValue = bookValue;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
