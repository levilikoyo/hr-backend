/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */


import com.hr.backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employee-documents")
public class EmployeeDocumentController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = storageService.uploadFile(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType()
            );
            return url;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Employee document API is working";
    }
}