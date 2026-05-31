/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_model;

/**
 *
 * @author apple
 */

public class MobileLoginResponse {

    private Long id;
    private String organization;
    private String fullName;
    private String email;
    private String phone;
    private String role;

    public MobileLoginResponse() {
    }

    public MobileLoginResponse(
            Long id,
            String organization,
            String fullName,
            String email,
            String phone,
            String role
    ) {
        this.id = id;
        this.organization = organization;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getOrganization() {
        return organization;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }
}
