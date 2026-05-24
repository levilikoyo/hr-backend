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
        name = "general_ledger_entry_dimensions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_gl_entry_dimension",
                        columnNames = {"organization", "gl_entry_id", "dimension_code"}
                )
        }
)
public class GeneralLedgerEntryDimensionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "gl_entry_id")
    private Long glEntryId;

    @Column(name = "document_no")
    private String documentNo;

    @Column(name = "entry_no")
    private String entryNo;

    @Column(name = "dimension_code")
    private String dimensionCode;

    @Column(name = "dimension_name")
    private String dimensionName;

    @Column(name = "dimension_value_code")
    private String dimensionValueCode;

    @Column(name = "dimension_value_name")
    private String dimensionValueName;

    @Column(name = "created_date")
    private String createdDate;

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public Long getGlEntryId() {
        return glEntryId;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public String getEntryNo() {
        return entryNo;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public String getDimensionValueCode() {
        return dimensionValueCode;
    }

    public String getDimensionValueName() {
        return dimensionValueName;
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

    public void setGlEntryId(Long glEntryId) {
        this.glEntryId = glEntryId;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public void setEntryNo(String entryNo) {
        this.entryNo = entryNo;
    }

    public void setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public void setDimensionValueCode(String dimensionValueCode) {
        this.dimensionValueCode = dimensionValueCode;
    }

    public void setDimensionValueName(String dimensionValueName) {
        this.dimensionValueName = dimensionValueName;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
