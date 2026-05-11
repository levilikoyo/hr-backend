package com.hr.backend.fin_model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "currencies",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization", "curency_code"})
    }
)
public class CurrencyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "curency_code", nullable = false)
    private String curencyCode;

    @Column(name = "curency_name")
    private String curencyName;

    @Column(name = "curency_symbole")
    private String curencySymbole;

    @Column(name = "unrealized_gain")
    private String unrealizedGain;

    @Column(name = "unrealized_losse")
    private String unrealizedLosse;

    @Column(name = "realized_gain")
    private String realizedGain;

    @Column(name = "realized_losse")
    private String realizedLosse;

    public CurrencyModel() {
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getCurencyCode() {
        return curencyCode;
    }

    public String getCurencyName() {
        return curencyName;
    }

    public String getCurencySymbole() {
        return curencySymbole;
    }

    public String getUnrealizedGain() {
        return unrealizedGain;
    }

    public String getUnrealizedLosse() {
        return unrealizedLosse;
    }

    public String getRealizedGain() {
        return realizedGain;
    }

    public String getRealizedLosse() {
        return realizedLosse;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setCurencyCode(String curencyCode) {
        this.curencyCode = curencyCode;
    }

    public void setCurencyName(String curencyName) {
        this.curencyName = curencyName;
    }

    public void setCurencySymbole(String curencySymbole) {
        this.curencySymbole = curencySymbole;
    }

    public void setUnrealizedGain(String unrealizedGain) {
        this.unrealizedGain = unrealizedGain;
    }

    public void setUnrealizedLosse(String unrealizedLosse) {
        this.unrealizedLosse = unrealizedLosse;
    }

    public void setRealizedGain(String realizedGain) {
        this.realizedGain = realizedGain;
    }

    public void setRealizedLosse(String realizedLosse) {
        this.realizedLosse = realizedLosse;
    }
}