package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.BankModel;
import com.hr.backend.fin_repository.BankRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banks")
@CrossOrigin(origins = "*")
public class BankController {

    @Autowired
    private BankRepository bankRepository;

    @PostMapping
    public ResponseEntity<?> saveBank(@RequestBody BankModel bank) {
        try {
            validateRequired(bank);

            boolean exists = bankRepository.existsByBankCodeAndOrganization(
                    bank.getBankCode(),
                    bank.getOrganization()
            );

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Duplicate bank code for this organization: " + bank.getBankCode());
            }

            return ResponseEntity.ok(bankRepository.save(bank));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Duplicate bank code for this organization: " + bank.getBankCode());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Bank save failed: " + e.getMessage());
        }
    }

    @GetMapping
    public List<BankModel> getAllBanks() {
        return bankRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<BankModel> getBanksByOrganization(@PathVariable String organization) {
        return bankRepository.findByOrganization(organization);
    }

    @PutMapping("/bank-info")
    public ResponseEntity<?> updateBankInfo(@RequestBody BankModel updatedData) {
        try {
            validateRequired(updatedData);

            BankModel bank = bankRepository
                    .findByBankCodeAndOrganization(updatedData.getBankCode(), updatedData.getOrganization())
                    .orElseThrow(() -> new RuntimeException("Bank not found for code: " + updatedData.getBankCode()));

            bank.setBankName(updatedData.getBankName());
            bank.setBankAddress(updatedData.getBankAddress());
            bank.setBankCity(updatedData.getBankCity());
            bank.setCurrencyCode(updatedData.getCurrencyCode());
            bank.setGlAccountBalance(updatedData.getGlAccountBalance());
            bank.setGlFund(updatedData.getGlFund());
            bank.setBlocked(updatedData.getBlocked() != null ? updatedData.getBlocked() : false);
            bank.setStartingDate(updatedData.getStartingDate());
            bank.setClosingDate(updatedData.getClosingDate());
            bank.setStatus(updatedData.getStatus());
            bank.setDescription(updatedData.getDescription());

            return ResponseEntity.ok(bankRepository.save(bank));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Bank update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/organization/{organization}/code/{bankCode}")
    public ResponseEntity<?> deleteBank(
            @PathVariable String organization,
            @PathVariable String bankCode) {
        try {
            BankModel bank = bankRepository
                    .findByBankCodeAndOrganization(bankCode, organization)
                    .orElseThrow(() -> new RuntimeException("Bank not found"));

            bankRepository.delete(bank);
            return ResponseEntity.ok("Bank deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Bank delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Bank API is working";
    }

    private void validateRequired(BankModel bank) {
        if (bank.getOrganization() == null || bank.getOrganization().trim().isEmpty()) {
            throw new IllegalArgumentException("Organization is required");
        }
        if (bank.getBankCode() == null || bank.getBankCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Bank code is required");
        }
    }
}
