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
        name = "dimension_values",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {
                    "organization",
                    "dimension_code",
                    "value_code"
            })
        }
)
public class DimensionValueModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "dimension_code", nullable = false)
    private String dimensionCode;

    @Column(name = "dimension_name")
    private String dimensionName;

    @Column(name = "value_code", nullable = false)
    private String valueCode;

    @Column(name = "value_name")
    private String valueName;

    private String description;

    private Boolean blocked = false;

    private String status = "Active";

    @Column(name = "created_date")
    private String createdDate;

    public DimensionValueModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public String getValueCode() {
        return valueCode;
    }

    public String getValueName() {
        return valueName;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getBlocked() {
        return blocked;
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

    public void setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
