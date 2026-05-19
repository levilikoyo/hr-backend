/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.AccountReportingMappingModel;
import com.hr.backend.fin_repository.AccountReportingMappingRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account-reporting-mappings")
@CrossOrigin(origins = "*")
public class AccountReportingMappingController {

    @Autowired
    private AccountReportingMappingRepository mappingRepository;

    @PostMapping
    public ResponseEntity<?> saveMapping(@RequestBody AccountReportingMappingModel mapping) {
        try {
            if (isEmpty(mapping.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(mapping.getSourceFrameworkCode())) {
                return ResponseEntity.badRequest().body("Source framework code is required");
            }

            if (isEmpty(mapping.getSourceGlCode())) {
                return ResponseEntity.badRequest().body("Source G/L code is required");
            }

            if (isEmpty(mapping.getTargetFrameworkCode())) {
                return ResponseEntity.badRequest().body("Target framework code is required");
            }

            boolean exists =
                    mappingRepository.existsByOrganizationAndSourceFrameworkCodeAndSourceGlCodeAndTargetFrameworkCode(
                            mapping.getOrganization(),
                            mapping.getSourceFrameworkCode(),
                            mapping.getSourceGlCode(),
                            mapping.getTargetFrameworkCode()
                    );

            if (exists) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Account reporting mapping already exists");
            }

            if (isEmpty(mapping.getStatus())) {
                mapping.setStatus("Active");
            }

            if (isEmpty(mapping.getCreatedDate())) {
                mapping.setCreatedDate(todayDate());
            }

            return ResponseEntity.ok(mappingRepository.save(mapping));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Save account reporting mapping failed: " + e.getMessage());
        }
    }

    @PutMapping("/mapping-info")
    public ResponseEntity<?> updateMapping(@RequestBody AccountReportingMappingModel updatedData) {
        try {
            if (isEmpty(updatedData.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(updatedData.getSourceFrameworkCode())) {
                return ResponseEntity.badRequest().body("Source framework code is required");
            }

            if (isEmpty(updatedData.getSourceGlCode())) {
                return ResponseEntity.badRequest().body("Source G/L code is required");
            }

            if (isEmpty(updatedData.getTargetFrameworkCode())) {
                return ResponseEntity.badRequest().body("Target framework code is required");
            }

            AccountReportingMappingModel mapping =
                    mappingRepository
                            .findByOrganizationAndSourceFrameworkCodeAndSourceGlCodeAndTargetFrameworkCode(
                                    updatedData.getOrganization(),
                                    updatedData.getSourceFrameworkCode(),
                                    updatedData.getSourceGlCode(),
                                    updatedData.getTargetFrameworkCode()
                            )
                            .orElseThrow(() -> new RuntimeException("Account reporting mapping not found"));

            mapping.setSourceGlName(updatedData.getSourceGlName());
            mapping.setTargetReportingCode(updatedData.getTargetReportingCode());
            mapping.setTargetReportingName(updatedData.getTargetReportingName());
            mapping.setTargetCategory(updatedData.getTargetCategory());

            if (!isEmpty(updatedData.getStatus())) {
                mapping.setStatus(updatedData.getStatus());
            }

            return ResponseEntity.ok(mappingRepository.save(mapping));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update account reporting mapping failed: " + e.getMessage());
        }
    }

    @GetMapping("/organization/{organization}")
    public List<AccountReportingMappingModel> getByOrganization(
            @PathVariable String organization) {

        return mappingRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/source-framework/{sourceFrameworkCode}")
    public List<AccountReportingMappingModel> getBySourceFramework(
            @PathVariable String organization,
            @PathVariable String sourceFrameworkCode) {

        return mappingRepository.findByOrganizationAndSourceFrameworkCode(
                organization,
                sourceFrameworkCode
        );
    }

    @GetMapping("/organization/{organization}/target-framework/{targetFrameworkCode}")
    public List<AccountReportingMappingModel> getByTargetFramework(
            @PathVariable String organization,
            @PathVariable String targetFrameworkCode) {

        return mappingRepository.findByOrganizationAndTargetFrameworkCode(
                organization,
                targetFrameworkCode
        );
    }

    @GetMapping("/organization/{organization}/source-framework/{sourceFrameworkCode}/source-gl/{sourceGlCode}/target-framework/{targetFrameworkCode}")
    public ResponseEntity<?> getOneMapping(
            @PathVariable String organization,
            @PathVariable String sourceFrameworkCode,
            @PathVariable String sourceGlCode,
            @PathVariable String targetFrameworkCode) {

        return mappingRepository
                .findByOrganizationAndSourceFrameworkCodeAndSourceGlCodeAndTargetFrameworkCode(
                        organization,
                        sourceFrameworkCode,
                        sourceGlCode,
                        targetFrameworkCode
                )
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/test")
    public String test() {
        return "Account Reporting Mapping API is working";
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String todayDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
