package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(
        name = "purchase_orders",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organization", "po_no"})
        }
)
public class PurchaseOrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "po_no")
    private String poNo;

    @Column(name = "vendor_code")
    private String vendorCode;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "order_date")
    private String orderDate;

    @Column(name = "expected_receipt_date")
    private String expectedReceiptDate;

    private String buyer;
    private String status;

    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal commitment = BigDecimal.ZERO;
    private BigDecimal received = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "posted_by")
    private String postedBy;

    @Column(name = "posted_date")
    private String postedDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getPoNo() { return poNo; }
    public void setPoNo(String poNo) { this.poNo = poNo; }
    public String getVendorCode() { return vendorCode; }
    public void setVendorCode(String vendorCode) { this.vendorCode = vendorCode; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getExpectedReceiptDate() { return expectedReceiptDate; }
    public void setExpectedReceiptDate(String expectedReceiptDate) { this.expectedReceiptDate = expectedReceiptDate; }
    public String getBuyer() { return buyer; }
    public void setBuyer(String buyer) { this.buyer = buyer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
    public BigDecimal getCommitment() { return commitment; }
    public void setCommitment(BigDecimal commitment) { this.commitment = commitment; }
    public BigDecimal getReceived() { return received; }
    public void setReceived(BigDecimal received) { this.received = received; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }
    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }
}
