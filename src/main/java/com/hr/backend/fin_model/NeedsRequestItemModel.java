/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_model;

/**
 *
 * @author apple
 */


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "needs_request_items")
public class NeedsRequestItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "needs_request_id", nullable = false)
    @JsonBackReference
    private NeedsRequestModel needsRequest;

    @Column(name = "item_name")
    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "item_category")
    private String itemCategory;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "total_amount")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "budget_plan")
    private String budgetPlan;

    @Column(name = "gl_account_no")
    private String glAccountNo;

    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "dimension_values", columnDefinition = "JSON")
    private String dimensionValues;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public NeedsRequestItemModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public NeedsRequestModel getNeedsRequest() {
        return needsRequest;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getBudgetPlan() {
        return budgetPlan;
    }

    public String getGlAccountNo() {
        return glAccountNo;
    }

    public String getFundCode() {
        return fundCode;
    }

    public String getDimensionValues() {
        return dimensionValues;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setNeedsRequest(NeedsRequestModel needsRequest) {
        this.needsRequest = needsRequest;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setBudgetPlan(String budgetPlan) {
        this.budgetPlan = budgetPlan;
    }

    public void setGlAccountNo(String glAccountNo) {
        this.glAccountNo = glAccountNo;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public void setDimensionValues(String dimensionValues) {
        this.dimensionValues = dimensionValues;
    }
}