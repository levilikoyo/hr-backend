/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */


import com.hr.backend.model.EmployeeDocument;
import com.hr.backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.hr.backend.repository.EmployeeDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/api/employee-documents")
public class EmployeeDocumentController {
@Autowired
private EmployeeDocumentRepository repository;

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
    
    @PostMapping("/upload")
public EmployeeDocument upload(
        @RequestParam("employeeCode") String employeeCode,
        @RequestParam("employeeNames") String employeeNames,
        @RequestParam("category") String category,
        @RequestParam("documentName") String documentName,
        @RequestParam("file") MultipartFile file
) throws Exception {

    String fileUrl = storageService.uploadFile(
            file.getBytes(),
            employeeCode + "/" + category + "/" + file.getOriginalFilename(),
            file.getContentType()
    );

    EmployeeDocument doc = new EmployeeDocument();
    doc.setEmployeeCode(employeeCode);
    doc.setEmployeeNames(employeeNames);
    doc.setCategory(category);
    doc.setDocumentName(documentName);
    doc.setOriginalFileName(file.getOriginalFilename());
    doc.setFileUrl(fileUrl);
    doc.setContentType(file.getContentType());
    doc.setUploadedAt(java.time.LocalDateTime.now());

    return repository.save(doc);
}
}