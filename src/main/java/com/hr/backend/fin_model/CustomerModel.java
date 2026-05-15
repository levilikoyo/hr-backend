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
        name = "customers",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organization", "customer_code"})
        }
)
public class CustomerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "customer_code", nullable = false)
    private String customerCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_address")
    private String customerAddress;

    @Column(name = "customer_city")
    private String customerCity;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_mail")
    private String customerMail;

    @Column(name = "invoicing")
    private String invoicing;

    @Column(name = "blocked")
    private Boolean blocked = false;

    @Column(name = "starting_date")
    private String startingDate;

    @Column(name = "closing_date")
    private String closingDate;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    public CustomerModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerMail() {
        return customerMail;
    }

    public String getInvoicing() {
        return invoicing;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public String getClosingDate() {
        return closingDate;
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

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerMail(String customerMail) {
        this.customerMail = customerMail;
    }

    public void setInvoicing(String invoicing) {
        this.invoicing = invoicing;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}