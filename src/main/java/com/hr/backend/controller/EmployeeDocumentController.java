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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
        @RequestParam("organization") String organization,
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
    doc.setOrganization(organization); // ✅ important
    doc.setCategory(category);
    doc.setDocumentName(documentName);
    doc.setOriginalFileName(file.getOriginalFilename());
    doc.setFileUrl(fileUrl);
    doc.setContentType(file.getContentType());
    doc.setUploadedAt(java.time.LocalDateTime.now());

    return repository.save(doc);

    }
    
    @GetMapping("/filter")
public List<EmployeeDocument> getDocuments(
        @RequestParam("employeeCode") String employeeCode,
        @RequestParam("category") String category,
        @RequestParam("organization") String organization
) {
    return repository.findByEmployeeCodeAndCategoryAndOrganization(
            employeeCode,
            category,
            organization
    );
}
@GetMapping("/tree")
public List<ArchiveTreeDTO> getArchiveTree() {
    return repository.getArchiveTreeData();
}

@GetMapping("/download/{id}")
public ResponseEntity<byte[]> download(@PathVariable Long id) {
    try {
        EmployeeDocument doc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        byte[] fileBytes = storageService.downloadFile(doc.getFileUrl());

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + doc.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(fileBytes);

    } catch (Exception e) {
        throw new RuntimeException("Download failed: " + e.getMessage());
    }
}
@DeleteMapping("/{id}")
public String deleteDocument(@PathVariable Long id) {
    EmployeeDocument doc = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));

    storageService.deleteFile(doc.getFileUrl());

    repository.deleteById(id);

    return "Document deleted successfully";
}
}