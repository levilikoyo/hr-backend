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
import com.hr.backend.fin_model.CurrencyModel;
import com.hr.backend.fin_repository.ExchangeRateRepository;
import com.hr.backend.fin_repository.CurrencyRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private CurrencyRepository currencyRepository;

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

            cleanExchangeRate(exchangeRate);

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
            syncCurrencyNearestRate(saved.getOrganization(), saved.getCurrencyCode());
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

            cleanExchangeRate(updatedData);

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

            ExchangeRateModel saved = exchangeRateRepository.save(exchangeRate);
            syncCurrencyNearestRate(saved.getOrganization(), saved.getCurrencyCode());
            return ResponseEntity.ok(saved);

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
            syncCurrencyNearestRate(organization, currencyCode);
            return ResponseEntity.ok("Exchange rate deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exchange rate delete failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/organization/{organization}/currency/{currencyCode}")
public List<ExchangeRateModel> getByOrganizationAndCurrencyCode(
        @PathVariable String organization,
        @PathVariable String currencyCode) {

    return exchangeRateRepository.findByOrganizationAndCurrencyCode(
            organization,
            currencyCode
    );
}

@GetMapping("/organization/{organization}/currency/{currencyCode}/nearest")
public ResponseEntity<?> getNearestExchangeRate(
        @PathVariable String organization,
        @PathVariable String currencyCode,
        @RequestParam(required = false) String date) {
    String targetDate = clean(date).isEmpty() ? LocalDate.now().toString() : clean(date);
    return nearestRate(organization, currencyCode, targetDate)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

    @GetMapping("/test")
    public String test() {
        return "Exchange Rate API is working";
    }

    private void cleanExchangeRate(ExchangeRateModel exchangeRate) {
        exchangeRate.setOrganization(clean(exchangeRate.getOrganization()));
        exchangeRate.setCurrencyCode(clean(exchangeRate.getCurrencyCode()));
        exchangeRate.setExchangeCurrencyDate(clean(exchangeRate.getExchangeCurrencyDate()));
    }

    private void syncCurrencyNearestRate(String organization, String currencyCode) {
        String cleanOrganization = clean(organization);
        String cleanCurrencyCode = clean(currencyCode);
        if (cleanOrganization.isEmpty() || cleanCurrencyCode.isEmpty()) {
            return;
        }

        Optional<CurrencyModel> currencyOptional = currencyRepository
                .findByCurencyCodeAndOrganization(cleanCurrencyCode, cleanOrganization);
        if (currencyOptional.isEmpty()) {
            return;
        }

        CurrencyModel currency = currencyOptional.get();
        Optional<ExchangeRateModel> rateOptional = nearestRate(
                cleanOrganization,
                cleanCurrencyCode,
                LocalDate.now().toString()
        );

        if (rateOptional.isEmpty()) {
            currency.setExchangeRateDate("");
            currency.setExchangeRate("");
        } else {
            ExchangeRateModel rate = rateOptional.get();
            currency.setExchangeRateDate(rate.getExchangeCurrencyDate());
            currency.setExchangeRate(rateValue(rate));
        }

        currencyRepository.save(currency);
    }

    private Optional<ExchangeRateModel> nearestRate(String organization, String currencyCode, String targetDate) {
        String cleanOrganization = clean(organization);
        String cleanCurrencyCode = clean(currencyCode);
        String cleanTargetDate = clean(targetDate);

        Optional<ExchangeRateModel> onOrBefore = exchangeRateRepository
                .findTopByOrganizationAndCurrencyCodeAndExchangeCurrencyDateLessThanEqualOrderByExchangeCurrencyDateDesc(
                        cleanOrganization,
                        cleanCurrencyCode,
                        cleanTargetDate
                );
        if (onOrBefore.isPresent()) {
            return onOrBefore;
        }

        return exchangeRateRepository
                .findTopByOrganizationAndCurrencyCodeAndExchangeCurrencyDateGreaterThanOrderByExchangeCurrencyDateAsc(
                        cleanOrganization,
                        cleanCurrencyCode,
                        cleanTargetDate
                );
    }

    private String rateValue(ExchangeRateModel rate) {
        BigDecimal unity = rate.getActualExchangeRateUnity();
        BigDecimal amount = rate.getActualExchangeRateAmount();
        if (unity == null || BigDecimal.ZERO.compareTo(unity) == 0) {
            return amount == null ? "0.00" : amount.stripTrailingZeros().toPlainString();
        }
        if (amount == null) {
            return "0.00";
        }
        return amount.divide(unity, 8, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    private String clean(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
