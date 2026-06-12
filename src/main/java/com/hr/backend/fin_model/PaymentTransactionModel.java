package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_code", nullable = false)
    private String organizationCode;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "billing_period")
    private String billingPeriod;

    @Column(name = "billing_status")
    private String billingStatus;

    private BigDecimal amount;
    private String currency;
    private String provider;

    @Column(name = "provider_reference")
    private String providerReference;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "paid_from")
    private LocalDate paidFrom;

    @Column(name = "paid_through")
    private LocalDate paidThrough;

    @Column(name = "grace_until")
    private LocalDate graceUntil;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        organizationCode = cleanUpper(organizationCode);
        organizationName = clean(organizationName);
        billingPeriod = cleanUpper(billingPeriod);
        billingStatus = cleanUpper(billingStatus);
        currency = cleanUpper(currency);
        provider = cleanUpper(provider);
        providerReference = clean(providerReference);
        notes = clean(notes);
        createdBy = clean(createdBy);
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }
        if (billingPeriod == null || billingPeriod.isEmpty()) {
            billingPeriod = "MONTHLY";
        }
        if (billingStatus == null || billingStatus.isEmpty()) {
            billingStatus = "PAID";
        }
        if (currency == null || currency.isEmpty()) {
            currency = "USD";
        }
        if (provider == null || provider.isEmpty()) {
            provider = "MANUAL";
        }
    }

    private String clean(String value) { return value == null ? "" : value.trim(); }
    private String cleanUpper(String value) { return clean(value).toUpperCase(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrganizationCode() { return organizationCode; }
    public void setOrganizationCode(String organizationCode) { this.organizationCode = organizationCode; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    public String getBillingStatus() { return billingStatus; }
    public void setBillingStatus(String billingStatus) { this.billingStatus = billingStatus; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getProviderReference() { return providerReference; }
    public void setProviderReference(String providerReference) { this.providerReference = providerReference; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
