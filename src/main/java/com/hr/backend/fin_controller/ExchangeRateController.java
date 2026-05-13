/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.ExchangeRateModel;
import com.hr.backend.fin_repository.ExchangeRateRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchange-rates")
@CrossOrigin(origins = "*")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @PostMapping
    public ResponseEntity<?> saveExchangeRate(@RequestBody ExchangeRateModel exchangeRate) {
        try {
            if (exchangeRate.getOrganization() == null || exchangeRate.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (exchangeRate.getCurrencyCode() == null || exchangeRate.getCurrencyCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Currency code is required");
            }

            if (exchangeRate.getExchangeCurrencyDate() == null || exchangeRate.getExchangeCurrencyDate().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Exchange date is required");
            }

            boolean exists = exchangeRateRepository
                    .existsByCurrencyCodeAndExchangeCurrencyDateAndOrganization(
                            exchangeRate.getCurrencyCode(),
                            exchangeRate.getExchangeCurrencyDate(),
                            exchangeRate.getOrganization()
                    );

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Exchange rate already exists for this currency and date");
            }

            ExchangeRateModel saved = exchangeRateRepository.save(exchangeRate);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exchange rate save failed: " + e.getMessage());
        }
    }

    @GetMapping
    public List<ExchangeRateModel> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<ExchangeRateModel> getByOrganization(@PathVariable String organization) {
        return exchangeRateRepository.findByOrganization(organization);
    }

    @PutMapping("/exchange-rate-info")
    public ResponseEntity<?> updateExchangeRate(@RequestBody ExchangeRateModel updatedData) {
        try {
            if (updatedData.getOrganization() == null || updatedData.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (updatedData.getCurrencyCode() == null || updatedData.getCurrencyCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Currency code is required");
            }

            if (updatedData.getExchangeCurrencyDate() == null || updatedData.getExchangeCurrencyDate().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Exchange date is required");
            }

            ExchangeRateModel exchangeRate = exchangeRateRepository
                    .findByCurrencyCodeAndExchangeCurrencyDateAndOrganization(
                            updatedData.getCurrencyCode(),
                            updatedData.getExchangeCurrencyDate(),
                            updatedData.getOrganization()
                    )
                    .orElseThrow(() -> new RuntimeException("Exchange rate not found"));

            exchangeRate.setCurrencySymbole(updatedData.getCurrencySymbole());
            exchangeRate.setCurrencyName(updatedData.getCurrencyName());
            exchangeRate.setActualExchangeRateUnity(updatedData.getActualExchangeRateUnity());
            exchangeRate.setActualExchangeRateAmount(updatedData.getActualExchangeRateAmount());
            exchangeRate.setBudgetExchangeRateUnity(updatedData.getBudgetExchangeRateUnity());
            exchangeRate.setBudgetExchangeRateAmount(updatedData.getBudgetExchangeRateAmount());

            return ResponseEntity.ok(exchangeRateRepository.save(exchangeRate));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exchange rate update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/organization/{organization}/currency/{currencyCode}/date/{exchangeCurrencyDate}")
    public ResponseEntity<?> deleteExchangeRate(
            @PathVariable String organization,
            @PathVariable String currencyCode,
            @PathVariable String exchangeCurrencyDate) {
        try {
            ExchangeRateModel exchangeRate = exchangeRateRepository
                    .findByCurrencyCodeAndExchangeCurrencyDateAndOrganization(
                            currencyCode,
                            exchangeCurrencyDate,
                            organization
                    )
                    .orElseThrow(() -> new RuntimeException("Exchange rate not found"));

            exchangeRateRepository.delete(exchangeRate);
            return ResponseEntity.ok("Exchange rate deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exchange rate delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Exchange Rate API is working";
    }
}
