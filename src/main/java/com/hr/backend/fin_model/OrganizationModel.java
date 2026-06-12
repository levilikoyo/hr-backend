package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
public class OrganizationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_code", nullable = false, unique = true)
    private String code;

    @Column(name = "organization_name", nullable = false)
    private String name;

    @Column(name = "legal_name")
    private String legalName;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "registration_number")
    private String registrationNumber;

    private String phone;
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;
    private String country;

    @Column(name = "base_currency")
    private String baseCurrency;

    @Column(name = "fiscal_year_start")
    private String fiscalYearStart;

    @Column(name = "fiscal_year_end")
    private String fiscalYearEnd;

    private String status;

    @Column(name = "subscription_plan")
    private String subscriptionPlan;

    @Column(name = "billing_period")
    private String billingPeriod;

    @Column(name = "billing_status")
    private String billingStatus;

    @Column(name = "paid_through")
    private LocalDate paidThrough;

    @Column(name = "grace_until")
    private LocalDate graceUntil;

    @Column(name = "payment_provider")
    private String paymentProvider;

    @Column(name = "payment_customer_id")
    private String paymentCustomerId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        normalizeDefaults();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeDefaults();
    }

    private void normalizeDefaults() {
        code = cleanUpper(code);
        name = clean(name);
        if (baseCurrency == null || baseCurrency.trim().isEmpty()) {
            baseCurrency = "USD";
        } else {
            baseCurrency = baseCurrency.trim().toUpperCase();
        }
        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE";
        } else {
            status = status.trim().toUpperCase();
        }
        if (subscriptionPlan == null || subscriptionPlan.trim().isEmpty()) {
            subscriptionPlan = "STANDARD";
        } else {
            subscriptionPlan = subscriptionPlan.trim().toUpperCase();
        }
        if (billingPeriod == null || billingPeriod.trim().isEmpty()) {
            billingPeriod = "MONTHLY";
        } else {
            billingPeriod = billingPeriod.trim().toUpperCase();
        }
        if (billingStatus == null || billingStatus.trim().isEmpty()) {
            billingStatus = "PAID";
        } else {
            billingStatus = billingStatus.trim().toUpperCase();
        }
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private String cleanUpper(String value) {
        String cleaned = clean(value);
        return cleaned == null ? null : cleaned.toUpperCase();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getTaxNumber() { return taxNumber; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public String getFiscalYearStart() { return fiscalYearStart; }
    public void setFiscalYearStart(String fiscalYearStart) { this.fiscalYearStart = fiscalYearStart; }
    public String getFiscalYearEnd() { return fiscalYearEnd; }
    public void setFiscalYearEnd(String fiscalYearEnd) { this.fiscalYearEnd = fiscalYearEnd; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(String subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    public String getBillingStatus() { return billingStatus; }
    public void setBillingStatus(String billingStatus) { this.billingStatus = billingStatus; }
    public LocalDate getPaidThrough() { return paidThrough; }
    public void setPaidThrough(LocalDate paidThrough) { this.paidThrough = paidThrough; }
    public LocalDate getGraceUntil() { return graceUntil; }
    public void setGraceUntil(LocalDate graceUntil) { this.graceUntil = graceUntil; }
    public String getPaymentProvider() { return paymentProvider; }
    public void setPaymentProvider(String paymentProvider) { this.paymentProvider = paymentProvider; }
    public String getPaymentCustomerId() { return paymentCustomerId; }
    public void setPaymentCustomerId(String paymentCustomerId) { this.paymentCustomerId = paymentCustomerId; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
