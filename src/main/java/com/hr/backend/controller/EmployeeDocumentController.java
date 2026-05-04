/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeDocumentController {

    @GetMapping("/test")
    public String test() {
        return "Test endpoint is working";
    }

    @GetMapping("/api/employee-documents/test")
    public String employeeDocumentTest() {
        return "Employee document API is working";
    }
}