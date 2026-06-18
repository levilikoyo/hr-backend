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
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

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
        @RequestParam(value = "folderPath", required = false) String folderPath,
        @RequestParam("file") MultipartFile file
) throws Exception {
    requireText(employeeCode, "Employee code is required");
    requireText(employeeNames, "Employee names are required");
    requireText(organization, "Organization is required");
    requireText(category, "Category is required");
    requireText(documentName, "Document name is required");
    if (file == null || file.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
    }

    String cleanFolderPath = cleanPath(folderPath);
    String storageFolder = cleanFolderPath.isEmpty()
            ? cleanCode(employeeCode) + "/" + cleanText(category)
            : "rh-archives/" + cleanText(organization) + "/" + cleanFolderPath;

    String fileUrl = storageService.uploadFile(
            file.getBytes(),
            storageFolder + "/" + file.getOriginalFilename(),
            file.getContentType()
    );

    EmployeeDocument doc = new EmployeeDocument();
    doc.setEmployeeCode(cleanCode(employeeCode));
    doc.setEmployeeNames(cleanText(employeeNames));
    doc.setOrganization(cleanText(organization));
    doc.setCategory(cleanText(category));
    doc.setDocumentName(cleanText(documentName));
    doc.setOriginalFileName(file.getOriginalFilename());
    doc.setFileUrl(fileUrl);
    doc.setContentType(file.getContentType());
    doc.setFolderPath(cleanFolderPath);
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
            cleanCode(employeeCode),
            cleanText(category),
            cleanText(organization)
    );
}
@GetMapping("/tree")
public List<ArchiveTreeDTO> getArchiveTree() {
    return repository.getArchiveTreeData();
}

@GetMapping("/organization/{organization}/folder")
public List<EmployeeDocument> getByOrganizationFolder(
        @PathVariable String organization,
        @RequestParam(value = "folderPath", required = false) String folderPath
) {
    return repository.findByOrganizationAndFolderPath(cleanText(organization), cleanPath(folderPath));
}

@GetMapping("/employee/{employeeCode}")
public List<EmployeeDocument> getByEmployee(@PathVariable String employeeCode) {
    return repository.findByEmployeeCode(cleanCode(employeeCode));
}

@GetMapping("/employee/{employeeCode}/category/{category}")
public List<EmployeeDocument> getByEmployeeAndCategory(
        @PathVariable String employeeCode,
        @PathVariable String category) {
    return repository.findByEmployeeCodeAndCategory(cleanCode(employeeCode), cleanText(category));
}

@GetMapping("/download/{id}")
public ResponseEntity<byte[]> download(@PathVariable Long id) {
    try {
        EmployeeDocument doc = repository.findById(id)
                .orElseThrow(() -> notFound("Document not found"));

        byte[] fileBytes = storageService.downloadFile(doc.getFileUrl());

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + doc.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(fileBytes);

    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Download failed: " + e.getMessage());
    }
}
@DeleteMapping("/{id}")
public ResponseEntity<Map<String, String>> deleteDocument(@PathVariable Long id) {
    EmployeeDocument doc = repository.findById(id)
            .orElseThrow(() -> notFound("Document not found"));

    storageService.deleteFile(doc.getFileUrl());

    repository.deleteById(id);

    return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
}

@PatchMapping("/{id}/rename")
public EmployeeDocument renameDocument(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload
) {
    EmployeeDocument doc = repository.findById(id)
            .orElseThrow(() -> notFound("Document not found"));
    String documentName = cleanText(payload == null ? "" : payload.get("documentName"));
    requireText(documentName, "Document name is required");
    doc.setDocumentName(documentName);
    return repository.save(doc);
}

private void requireText(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}

private ResponseStatusException notFound(String message) {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
}

private String cleanText(String value) {
    return value == null ? "" : value.trim();
}

private String cleanCode(String value) {
    return cleanText(value).toUpperCase();
}

private String cleanPath(String value) {
    return cleanText(value).replace("\\", "/").replaceAll("/+", "/").replaceAll("^/|/$", "");
}
}
