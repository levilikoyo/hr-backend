/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 *
 * @author apple
 */
@Entity
public class Ajouter_employee {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;
    private String organization;
    private String names;
    private String sir_name;
    private String adress;
    private String roll;
    
    private String dob;
    private String pob;
    private String phone;
    
    private String mail;
    private String blood;
    private String gender;
    private String mariage;
    
    private String nation;
    private String educ_level;
    private String educ_faculty;
    private String job_title;
    
    private String persName;
    private String persphone;
    private String persemail;
    private String persrela;
    private String persadress;

    private String hiredDate;
    private String status;
    private String endingDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSir_name() {
        return sir_name;
    }

    public void setSir_name(String sir_name) {
        this.sir_name = sir_name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMariage() {
        return mariage;
    }

    public void setMariage(String mariage) {
        this.mariage = mariage;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getEduc_level() {
        return educ_level;
    }

    public void setEduc_level(String educ_level) {
        this.educ_level = educ_level;
    }

    public String getEduc_faculty() {
        return educ_faculty;
    }

    public void setEduc_faculty(String educ_faculty) {
        this.educ_faculty = educ_faculty;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getPersName() {
        return persName;
    }

    public void setPersName(String persName) {
        this.persName = persName;
    }

    public String getPersphone() {
        return persphone;
    }

    public void setPersphone(String persphone) {
        this.persphone = persphone;
    }

    public String getPersemail() {
        return persemail;
    }

    public void setPersemail(String persemail) {
        this.persemail = persemail;
    }

    public String getPersrela() {
        return persrela;
    }

    public void setPersrela(String persrela) {
        this.persrela = persrela;
    }

    public String getPersadress() {
        return persadress;
    }

    public void setPersadress(String persadress) {
        this.persadress = persadress;
    }

    public String getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(String hiredDate) {
        this.hiredDate = hiredDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    
    
    
}
