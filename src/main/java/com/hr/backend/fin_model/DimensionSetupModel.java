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
        name = "dimension_setups",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organization", "dimension_code"})
        }
)
public class DimensionSetupModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "dimension_code", nullable = false)
    private String dimensionCode;

    @Column(name = "dimension_name")
    private String dimensionName;

    private String description;

    private Boolean blocked = false;

    @Column(name = "required_dimension")
    private Boolean required = false;

    @Column(name = "show_in_actual")
    private Boolean showInActual = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    private String status = "Active";

    @Column(name = "created_date")
    private String createdDate;

    public DimensionSetupModel() {
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

    public String getDescription() {
        return description;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public Boolean getRequired() {
        return required;
    }

    public Boolean getShowInActual() {
        return showInActual;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public void setShowInActual(Boolean showInActual) {
        this.showInActual = showInActual;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}