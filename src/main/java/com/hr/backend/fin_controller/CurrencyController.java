package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.CurrencyModel;
import com.hr.backend.fin_repository.CurrencyRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currencies")
@CrossOrigin(origins = "*")
public class CurrencyController {

    @Autowired
    private CurrencyRepository currencyRepository;

    @PostMapping
    public ResponseEntity<?> saveCurrency(@RequestBody CurrencyModel currency) {
        try {
            if (currency.getOrganization() == null || currency.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (currency.getCurencyCode() == null || currency.getCurencyCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Currency code is required");
            }

            CurrencyModel saved = currencyRepository.save(currency);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Currency save failed: " + e.getMessage());
        }
    }

    @GetMapping
    public List<CurrencyModel> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<CurrencyModel> getCurrencyByOrganization(@PathVariable String organization) {
        return currencyRepository.findByOrganization(organization);
    }

    @GetMapping("/test")
    public String test() {
        return "Currency API is working";
    }

    @PutMapping("/currency-info")
    public ResponseEntity<?> updateCurrencyInfo(@RequestBody CurrencyModel updatedData) {
        try {
            CurrencyModel currency = currencyRepository
                    .findByCurencyCodeAndOrganization(
                            updatedData.getCurencyCode(),
                            updatedData.getOrganization()
                    )
                    .orElseThrow(() -> new RuntimeException("Currency not found"));

            currency.setCurencyCode(updatedData.getCurencyCode());
            currency.setCurencyName(updatedData.getCurencyName());
            currency.setCurencySymbole(updatedData.getCurencySymbole());
            currency.setUnrealizedGain(updatedData.getUnrealizedGain());
            currency.setUnrealizedLosse(updatedData.getUnrealizedLosse());
            currency.setRealizedGain(updatedData.getRealizedGain());
            currency.setRealizedLosse(updatedData.getRealizedLosse());

            return ResponseEntity.ok(currencyRepository.save(currency));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Currency update failed: " + e.getMessage());
        }
    }
    
   @PutMapping("/lcy-blocked-info")
public ResponseEntity<?> updateCurrencyLCYBlocked(@RequestBody CurrencyModel updatedData) {
    try {
        if (updatedData.getOrganization() == null || updatedData.getOrganization().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Organization is required");
        }

        if (updatedData.getCurencyCode() == null || updatedData.getCurencyCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Currency code is required");
        }

        CurrencyModel currency = currencyRepository
                .findByCurencyCodeAndOrganization(
                        updatedData.getCurencyCode(),
                        updatedData.getOrganization()
                )
                .orElseThrow(() -> new RuntimeException(
                        "Currency not found for code: "
                        + updatedData.getCurencyCode()
                        + " and organization: "
                        + updatedData.getOrganization()
                ));

        currency.setLcy(updatedData.getLcy() != null ? updatedData.getLcy() : false);
        currency.setBlocked(updatedData.getBlocked() != null ? updatedData.getBlocked() : false);

        return ResponseEntity.ok(currencyRepository.save(currency));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Currency LCY/Blocked update failed: " + e.getMessage());
    }
}

   @PutMapping("/rate-dated-info")
public ResponseEntity<?> updateCurrencyRateDate(@RequestBody CurrencyModel updatedData) {
    try {
        if (updatedData.getOrganization() == null || updatedData.getOrganization().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Organization is required");
        }

        if (updatedData.getCurencyCode() == null || updatedData.getCurencyCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Currency code is required");
        }

        CurrencyModel currency = currencyRepository
                .findByCurencyCodeAndOrganization(
                        updatedData.getCurencyCode(),
                        updatedData.getOrganization()
                )
                .orElseThrow(() -> new RuntimeException(
                        "Currency not found for code: "
                        + updatedData.getCurencyCode()
                        + " and organization: "
                        + updatedData.getOrganization()
                ));

        currency.setLcy(updatedData.getLcy() != null ? updatedData.getLcy() : false);
        currency.setBlocked(updatedData.getBlocked() != null ? updatedData.getBlocked() : false);

        return ResponseEntity.ok(currencyRepository.save(currency));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Currency LCY/Blocked update failed: " + e.getMessage());
    }
}
@GetMapping("/lcy-blocked-info-test")
public String testLCYBlockedEndpoint() {
    return "LCY Blocked endpoint is available";
}
}