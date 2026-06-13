package com.hr.backend.fin_model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MpesaPaymentRequest {

    private String organizationCode;
    private String organizationName;
    private String billingPeriod;
    private BigDecimal amount;
    private String currency;
    private String phoneNumber;
    private LocalDate paymentDate;
    private LocalDate paidFrom;
    private LocalDate paidThrough;
    private LocalDate graceUntil;
    private String notes;
    private String createdBy;

    public String getOrganizationCode() { return organizationCode; }
    public void setOrganizationCode(String organizationCode) { this.organizationCode = organizationCode; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public LocalDate getPaidFrom() { return paidFrom; }
    public void setPaidFrom(LocalDate paidFrom) { this.paidFrom = paidFrom; }
    public LocalDate getPaidThrough() { return paidThrough; }
    public void setPaidThrough(LocalDate paidThrough) { this.paidThrough = paidThrough; }
    public LocalDate getGraceUntil() { return graceUntil; }
    public void setGraceUntil(LocalDate graceUntil) { this.graceUntil = graceUntil; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
