/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_model;

/**
 *
 * @author apple
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "funds")
public class CurrencyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="curency_code", nullable = false, unique = true)
    private String curencyCode;

    @Column(name="curency_name", nullable = false)
    private String curencyName;

    private String curencySymbole;
    private String unrealizedGain;
    private String unrealizedLosse;
    private String realizedGain;
    private String realizedLosse;
    private String organization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurencyCode() {
        return curencyCode;
    }

    public void setCurencyCode(String curencyCode) {
        this.curencyCode = curencyCode;
    }

    public String getCurencyName() {
        return curencyName;
    }

    public void setCurencyName(String curencyName) {
        this.curencyName = curencyName;
    }

    public String getCurencySymbole() {
        return curencySymbole;
    }

    public void setCurencySymbole(String curencySymbole) {
        this.curencySymbole = curencySymbole;
    }

    public String getUnrealizedGain() {
        return unrealizedGain;
    }

    public void setUnrealizedGain(String unrealizedGain) {
        this.unrealizedGain = unrealizedGain;
    }

    public String getUnrealizedLosse() {
        return unrealizedLosse;
    }

    public void setUnrealizedLosse(String unrealizedLosse) {
        this.unrealizedLosse = unrealizedLosse;
    }

    public String getRealizedGain() {
        return realizedGain;
    }

    public void setRealizedGain(String realizedGain) {
        this.realizedGain = realizedGain;
    }

    public String getRealizedLosse() {
        return realizedLosse;
    }

    public void setRealizedLosse(String realizedLosse) {
        this.realizedLosse = realizedLosse;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

  
}