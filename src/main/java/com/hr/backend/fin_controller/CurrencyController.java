package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.CurrencyModel;
import com.hr.backend.fin_repository.CurrencyRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currencies")
@CrossOrigin(origins = "*")
public class CurrencyController {

    @Autowired
    private CurrencyRepository currencyRepository;

    @PostMapping
    public CurrencyModel saveCurrency(@RequestBody CurrencyModel currency) {
        return currencyRepository.save(currency);
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
    public CurrencyModel updateCurrencyInfo(@RequestBody CurrencyModel updatedData) {

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

        return currencyRepository.save(currency);
    }
}