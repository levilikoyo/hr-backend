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
        name = "vendors",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organization", "vendor_code"})
        }
)
public class VendorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "vendor_code", nullable = false)
    private String vendorCode;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "vendor_address")
    private String vendorAddress;

    @Column(name = "vendor_city")
    private String vendorCity;

    @Column(name = "vendor_country")
    private String vendorCountry;

    @Column(name = "tax_no")
    private String taxNo;

    @Column(name = "vendor_phone")
    private String vendorPhone;

    @Column(name = "vendor_mail")
    private String vendorMail;

    private String invoicing;

    @Column(name = "linked_gl_account")
    private String linkedGlAccount;

    private String currency;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "starting_date")
    private String startingDate;

    @Column(name = "closing_date")
    private String closingDate;

    private Boolean blocked = false;

    private String status;

    @Column(length = 1000)
    private String description;

    public VendorModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public String getVendorCity() {
        return vendorCity;
    }

    public String getVendorCountry() {
        return vendorCountry;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public String getVendorMail() {
        return vendorMail;
    }

    public String getInvoicing() {
        return invoicing;
    }

    public String getLinkedGlAccount() {
        return linkedGlAccount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public void setVendorCity(String vendorCity) {
        this.vendorCity = vendorCity;
    }

    public void setVendorCountry(String vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public void setVendorMail(String vendorMail) {
        this.vendorMail = vendorMail;
    }

    public void setInvoicing(String invoicing) {
        this.invoicing = invoicing;
    }

    public void setLinkedGlAccount(String linkedGlAccount) {
        this.linkedGlAccount = linkedGlAccount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
