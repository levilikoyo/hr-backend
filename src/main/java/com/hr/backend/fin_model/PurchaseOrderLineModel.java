package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_lines")
public class PurchaseOrderLineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "po_no")
    private String poNo;

    @Column(name = "line_no")
    private Integer lineNo;

    @Column(name = "line_type")
    private String lineType;

    @Column(name = "gl_no")
    private String glNo;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "unit_cost")
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "tax_percent")
    private BigDecimal taxPercent = BigDecimal.ZERO;

    @Column(name = "line_amount")
    private BigDecimal lineAmount = BigDecimal.ZERO;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "amount_lcy")
    private BigDecimal amountLcy = BigDecimal.ZERO;

    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "dimension_values", columnDefinition = "TEXT")
    private String dimensionValues;

    private BigDecimal received = BigDecimal.ZERO;
    private BigDecimal invoiced = BigDecimal.ZERO;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getPoNo() { return poNo; }
    public void setPoNo(String poNo) { this.poNo = poNo; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public String getLineType() { return lineType; }
    public void setLineType(String lineType) { this.lineType = lineType; }
    public String getGlNo() { return glNo; }
    public void setGlNo(String glNo) { this.glNo = glNo; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }
    public BigDecimal getTaxPercent() { return taxPercent; }
    public void setTaxPercent(BigDecimal taxPercent) { this.taxPercent = taxPercent; }
    public BigDecimal getLineAmount() { return lineAmount; }
    public void setLineAmount(BigDecimal lineAmount) { this.lineAmount = lineAmount; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public BigDecimal getAmountLcy() { return amountLcy; }
    public void setAmountLcy(BigDecimal amountLcy) { this.amountLcy = amountLcy; }
    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }
    public String getDimensionValues() { return dimensionValues; }
    public void setDimensionValues(String dimensionValues) { this.dimensionValues = dimensionValues; }
    public BigDecimal getReceived() { return received; }
    public void setReceived(BigDecimal received) { this.received = received; }
    public BigDecimal getInvoiced() { return invoiced; }
    public void setInvoiced(BigDecimal invoiced) { this.invoiced = invoiced; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
