/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.GeneralLedgerEntryModel;
import com.hr.backend.fin_repository.GeneralLedgerEntryRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/general-ledger-entries")
@CrossOrigin(origins = "*")
public class GeneralLedgerEntryController {

    @Autowired
    private GeneralLedgerEntryRepository ledgerRepository;

    @PostMapping
    public ResponseEntity<?> saveEntry(@RequestBody GeneralLedgerEntryModel entry) {
        try {
            if (isEmpty(entry.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(entry.getTransactionType())) {
                entry.setTransactionType("ACTUAL");
            }

            if (isEmpty(entry.getPostingDate())) {
                return ResponseEntity.badRequest().body("Posting date is required");
            }

            if (isEmpty(entry.getDocumentNo())) {
                return ResponseEntity.badRequest().body("Document number is required");
            }

            if (isEmpty(entry.getAccountNo())) {
                return ResponseEntity.badRequest().body("Account number is required");
            }

            if (entry.getPosted() == null) {
                entry.setPosted(false);
            }

            if (entry.getReversed() == null) {
                entry.setReversed(false);
            }

            if (isEmpty(entry.getCreatedDate())) {
                entry.setCreatedDate(todayDate());
            }

            return ResponseEntity.ok(ledgerRepository.save(entry));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Save general ledger entry failed: " + e.getMessage());
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<?> saveEntries(@RequestBody List<GeneralLedgerEntryModel> entries) {
        try {
            if (entries == null || entries.isEmpty()) {
                return ResponseEntity.badRequest().body("No general ledger entries to save");
            }

            for (GeneralLedgerEntryModel entry : entries) {
                if (isEmpty(entry.getOrganization())) {
                    return ResponseEntity.badRequest().body("Organization is required on all lines");
                }

                if (isEmpty(entry.getTransactionType())) {
                    entry.setTransactionType("ACTUAL");
                }

                if (entry.getPosted() == null) {
                    entry.setPosted(false);
                }

                if (entry.getReversed() == null) {
                    entry.setReversed(false);
                }

                if (isEmpty(entry.getCreatedDate())) {
                    entry.setCreatedDate(todayDate());
                }
            }

            return ResponseEntity.ok(ledgerRepository.saveAll(entries));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Save general ledger batch failed: " + e.getMessage());
        }
    }

    @GetMapping("/organization/{organization}")
    public List<GeneralLedgerEntryModel> getByOrganization(
            @PathVariable String organization) {
        return ledgerRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/transaction-type/{transactionType}")
    public List<GeneralLedgerEntryModel> getByTransactionType(
            @PathVariable String organization,
            @PathVariable String transactionType) {

        return ledgerRepository.findByOrganizationAndTransactionType(
                organization,
                transactionType
        );
    }

    @GetMapping("/organization/{organization}/framework/{frameworkCode}")
    public List<GeneralLedgerEntryModel> getByFramework(
            @PathVariable String organization,
            @PathVariable String frameworkCode) {

        return ledgerRepository.findByOrganizationAndFrameworkCode(
                organization,
                frameworkCode
        );
    }

    @GetMapping("/organization/{organization}/document/{documentNo}")
    public List<GeneralLedgerEntryModel> getByDocument(
            @PathVariable String organization,
            @PathVariable String documentNo) {

        return ledgerRepository.findByOrganizationAndDocumentNo(
                organization,
                documentNo
        );
    }

    @GetMapping("/organization/{organization}/source/{sourceType}/{sourceDocumentNo}")
    public List<GeneralLedgerEntryModel> getBySource(
            @PathVariable String organization,
            @PathVariable String sourceType,
            @PathVariable String sourceDocumentNo) {

        return ledgerRepository.findByOrganizationAndSourceTypeAndSourceDocumentNo(
                organization,
                sourceType,
                sourceDocumentNo
        );
    }

    @GetMapping("/exists/organization/{organization}/source/{sourceType}/{sourceDocumentNo}/line/{sourceLineNo}")
    public boolean existsBySourceLine(
            @PathVariable String organization,
            @PathVariable String sourceType,
            @PathVariable String sourceDocumentNo,
            @PathVariable String sourceLineNo) {

        return ledgerRepository.existsByOrganizationAndSourceTypeAndSourceDocumentNoAndSourceLineNo(
                organization,
                sourceType,
                sourceDocumentNo,
                sourceLineNo
        );
    }

    @GetMapping("/test")
    public String test() {
        return "General Ledger Entry API is working";
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String todayDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}