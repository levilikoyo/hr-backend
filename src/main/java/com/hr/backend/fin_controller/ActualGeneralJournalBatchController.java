/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.ActualGeneralJournalBatchModel;
import com.hr.backend.fin_repository.ActualGeneralJournalBatchRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actual-general-journal-batches")
@CrossOrigin(origins = "*")
public class ActualGeneralJournalBatchController {

    @Autowired
    private ActualGeneralJournalBatchRepository batchRepository;

    @PostMapping
    public ResponseEntity<?> saveBatch(@RequestBody ActualGeneralJournalBatchModel batch) {
        try {
            if (isEmpty(batch.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(batch.getFrameworkCode())) {
                return ResponseEntity.badRequest().body("Framework code is required");
            }

            if (isEmpty(batch.getBatchName())) {
                return ResponseEntity.badRequest().body("Batch name is required");
            }

            boolean exists = batchRepository.existsByOrganizationAndFrameworkCodeAndBatchName(
                    batch.getOrganization(),
                    batch.getFrameworkCode(),
                    batch.getBatchName()
            );

            if (exists) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Actual general journal batch already exists");
            }

            if (isEmpty(batch.getStatus())) {
                batch.setStatus("Active");
            }

            if (isEmpty(batch.getCreatedDate())) {
                batch.setCreatedDate(todayDate());
            }

            return ResponseEntity.ok(batchRepository.save(batch));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Save actual general journal batch failed: " + e.getMessage());
        }
    }

    @PutMapping("/batch-info")
    public ResponseEntity<?> updateBatch(@RequestBody ActualGeneralJournalBatchModel updatedData) {
        try {
            if (isEmpty(updatedData.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(updatedData.getFrameworkCode())) {
                return ResponseEntity.badRequest().body("Framework code is required");
            }

            if (isEmpty(updatedData.getBatchName())) {
                return ResponseEntity.badRequest().body("Batch name is required");
            }

            ActualGeneralJournalBatchModel batch = batchRepository
                    .findByOrganizationAndFrameworkCodeAndBatchName(
                            updatedData.getOrganization(),
                            updatedData.getFrameworkCode(),
                            updatedData.getBatchName()
                    )
                    .orElseThrow(() -> new RuntimeException("Actual general journal batch not found"));

            batch.setDescription(updatedData.getDescription());
            batch.setNoSeries(updatedData.getNoSeries());

            batch.setBalanceAccountType(updatedData.getBalanceAccountType());
            batch.setBalanceAccountNo(updatedData.getBalanceAccountNo());
            batch.setBalanceAccountName(updatedData.getBalanceAccountName());

            batch.setControlFundNo(updatedData.getControlFundNo());
            batch.setControlFundName(updatedData.getControlFundName());

            if (!isEmpty(updatedData.getStatus())) {
                batch.setStatus(updatedData.getStatus());
            }

            return ResponseEntity.ok(batchRepository.save(batch));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update actual general journal batch failed: " + e.getMessage());
        }
    }

    @GetMapping("/organization/{organization}")
    public List<ActualGeneralJournalBatchModel> getByOrganization(
            @PathVariable String organization) {

        return batchRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/framework/{frameworkCode}")
    public List<ActualGeneralJournalBatchModel> getByFramework(
            @PathVariable String organization,
            @PathVariable String frameworkCode) {

        return batchRepository.findByOrganizationAndFrameworkCode(
                organization,
                frameworkCode
        );
    }

    @GetMapping("/organization/{organization}/framework/{frameworkCode}/batch/{batchName}")
    public ResponseEntity<?> getOneBatch(
            @PathVariable String organization,
            @PathVariable String frameworkCode,
            @PathVariable String batchName) {

        return batchRepository
                .findByOrganizationAndFrameworkCodeAndBatchName(
                        organization,
                        frameworkCode,
                        batchName
                )
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    


    @GetMapping("/test")
    public String test() {
        return "Actual General Journal Batch API is working";
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String todayDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    
    @GetMapping("/organization/{organization}/framework/{frameworkCode}/transaction-type/{transactionType}")
public List<ActualGeneralJournalBatchModel> getByOrganizationFrameworkAndTransactionType(
        @PathVariable String organization,
        @PathVariable String frameworkCode,
        @PathVariable String transactionType) {

    return batchRepository.findByOrganizationAndFrameworkCodeAndTransactionType(
            organization,
            frameworkCode,
            transactionType
    );
}
}
