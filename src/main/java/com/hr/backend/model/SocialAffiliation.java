/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.model;

/**
 *
 * @author apple
 */
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SocialAffiliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String organisation;
    private String hiringDate;
    private String empCode;
    private String empName;
    private String sex;
    private String fatherName;
    private String motherName;
    private String dob;

    private String locality;
    private String community;
    private String territory;
    private String province;
    private String country;

    private String jobTitle;
    private String profCategory;

    private String inssuanceAffNo;
    private String issuanceDate;
    private String issuancePlace;

    private String identityId;
    private String nationalId;

    private String maritalStatus;
    private String partnerName;
    private String nbrChildren;

    private String employerNo;
    private String employerName;
    private String employeePob;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(String hiringDate) {
        this.hiringDate = hiringDate;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getProfCategory() {
        return profCategory;
    }

    public void setProfCategory(String profCategory) {
        this.profCategory = profCategory;
    }

    public String getInssuanceAffNo() {
        return inssuanceAffNo;
    }

    public void setInssuanceAffNo(String inssuanceAffNo) {
        this.inssuanceAffNo = inssuanceAffNo;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getIssuancePlace() {
        return issuancePlace;
    }

    public void setIssuancePlace(String issuancePlace) {
        this.issuancePlace = issuancePlace;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getNbrChildren() {
        return nbrChildren;
    }

    public void setNbrChildren(String nbrChildren) {
        this.nbrChildren = nbrChildren;
    }

    public String getEmployerNo() {
        return employerNo;
    }

    public void setEmployerNo(String employerNo) {
        this.employerNo = employerNo;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployeePob() {
        return employeePob;
    }

    public void setEmployeePob(String employeePob) {
        this.employeePob = employeePob;
    }
}
