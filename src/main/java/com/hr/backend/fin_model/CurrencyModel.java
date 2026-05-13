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

    @Column(name = "lcy")
    private Boolean lcy = false;

    @Column(name = "blocked")
    private Boolean blocked = false;
    
    @Column(name = "exchangeratedate")
    private String exchangeratedate;

    @Column(name = "exchangerate")
    private String exchangerate;

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

    public Boolean getLcy() {
        return lcy;
    }

    public Boolean getBlocked() {
        return blocked;
    }
    
     public String getExchangeRateDate() {
        return exchangeratedate;
    }

    public String getExchangeRate() {
        return exchangerate;
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

    public void setLcy(Boolean lcy) {
        this.lcy = lcy;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
    
      public void setExchangeRateDate(String exchangeratedate) {
        this.exchangeratedate = exchangeratedate;
    }

    public void setExchangeRate(String exchangerate) {
        this.exchangerate = exchangerate;
    }
}