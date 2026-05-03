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
import com.hr.backend.repository.EmployeeDocumentRepository;
import com.hr.backend.service.EmployeeDocumentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employee-documents")
@CrossOrigin("*")
public class EmployeeDocumentController {

    private final EmployeeDocumentService service;
    private final EmployeeDocumentRepository repository;

    public EmployeeDocumentController(EmployeeDocumentService service,
                                      EmployeeDocumentRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("/upload")
    public EmployeeDocument uploadDocument(
            @RequestParam("employeeCode") String employeeCode,
            @RequestParam("employeeNames") String employeeNames,
            @RequestParam("category") String category,
            @RequestParam("documentName") String documentName,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        return service.saveDocument(
                employeeCode,
                employeeNames,
                category,
                documentName,
                file
        );
    }

    @GetMapping("/{employeeCode}")
    public List<EmployeeDocument> getDocumentsByEmployee(
            @PathVariable String employeeCode
    ) {
        return repository.findByEmployeeCode(employeeCode);
    }

    @GetMapping("/{employeeCode}/{category}")
    public List<EmployeeDocument> getDocumentsByCategory(
            @PathVariable String employeeCode,
            @PathVariable String category
    ) {
        return repository.findByEmployeeCodeAndCategory(employeeCode, category);
    }
}
