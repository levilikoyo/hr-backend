package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.CurrencyModel;
import com.hr.backend.fin_repository.CurrencyRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
    public List<CurrencyModel> getAllFunds() {
        return currencyRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<CurrencyModel> getFundByOrganization(@PathVariable String organization) {
        return currencyRepository.findByOrganization(organization);
    }

   

    @GetMapping("/test")
    public String test() {
        return "Fund API is working";
    }
    
    @PutMapping("/funds-info")
public CurrencyModel updateFundInfo(@RequestBody CurrencyModel updatedData) {

    CurrencyModel currency = currencyRepository
            .findByCurrencyCodeAndOrganization(
                    updatedData.getCurencyCode(),
                    updatedData.getOrganization()
            )
            .orElseThrow(() -> new RuntimeException("Fund not found"));

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