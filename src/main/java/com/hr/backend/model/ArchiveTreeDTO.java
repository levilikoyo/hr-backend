/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.model;

/**
 *
 * @author apple
 */

public class ArchiveTreeDTO {
    public String organization;
    public String employeeCode;
    public String employeeNames;
    public String category;

    public ArchiveTreeDTO(String organization, String employeeCode, String employeeNames, String category) {
        this.organization = organization;
        this.employeeCode = employeeCode;
        this.employeeNames = employeeNames;
        this.category = category;
    }
}