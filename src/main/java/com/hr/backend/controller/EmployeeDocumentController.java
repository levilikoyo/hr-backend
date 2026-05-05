/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */



import com.hr.backend.model.ArchiveTreeDTO;
import com.hr.backend.model.EmployeeDocument;
import com.hr.backend.service.StorageService;
import com.hr.backend.repository.EmployeeDocumentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/employee-documents")
public class EmployeeDocumentController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private EmployeeDocumentRepository repository;

    // 🔹 Test endpoint
    @GetMapping("/test")
    public String test() {
        return "Employee document API is working";
    }

    // 🔹 Upload endpoint
    @PostMapping("/upload")
    public EmployeeDocument upload(
            @RequestParam("employeeCode") String employeeCode,
            @RequestParam("employeeNames") String employeeNames,
            @RequestParam("category") String category,
            @RequestParam("documentName") String documentName,
            @RequestParam("file") MultipartFile file
    ) {
        try {
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
            doc.setUploadedAt(LocalDateTime.now());

            return repository.save(doc);

        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/filter")
public List<EmployeeDocument> getDocuments(
        @RequestParam("employeeCode") String employeeCode,
        @RequestParam("category") String category,
        @RequestParam("organization") String organization
) {
    return repository.findByEmployeeCodeAndCategoryAndOrganisation(
            employeeCode,
            category,
            organization
    );
}
@GetMapping("/tree")
public List<ArchiveTreeDTO> getArchiveTree() {
    return repository.getArchiveTreeData();
}
}