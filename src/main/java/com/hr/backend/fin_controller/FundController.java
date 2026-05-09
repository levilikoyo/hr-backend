package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.FundModel;
import com.hr.backend.fin_repository.FundRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/funds")
@CrossOrigin(origins = "*")
public class FundController {

    @Autowired
    private FundRepository fundRepository;

    @PostMapping
    public FundModel saveFund(@RequestBody FundModel fund) {
        return fundRepository.save(fund);
    }

    @GetMapping
    public List<FundModel> getAllFunds() {
        return fundRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<FundModel> getFundByOrganization(@PathVariable String organization) {
        return fundRepository.findByOrganization(organization);
    }

    @GetMapping("/search")
    public List<FundModel> searchFunds(@RequestParam String keyword) {
        return fundRepository
                .findByFundCodeContainingIgnoreCaseOrFundNameContainingIgnoreCaseOrDonorContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword
                );
    }

    @GetMapping("/test")
    public String test() {
        return "Fund API is working";
    }
    
    @PutMapping("/funds-info")
public FundModel updateFundInfo(@RequestBody FundModel updatedData) {

    FundModel fund = fundRepository
            .findByFundCodeAndOrganization(
                    updatedData.getFundCode(),
                    updatedData.getOrganization()
            )
            .orElseThrow(() -> new RuntimeException("Fund not found"));

    fund.setCurrency(updatedData.getCurrency());
    fund.setBudgetYear(updatedData.getBudgetYear());
    fund.setGrantAgreementNo(updatedData.getGrantAgreementNo());
    fund.setBlocked(updatedData.getBlocked());
    fund.setStartDate(updatedData.getStartDate());
    fund.setClosingDate(updatedData.getClosingDate());
    fund.setRestricted(updatedData.getRestricted());
    fund.setStatus(updatedData.getStatus());
    fund.setDescription(updatedData.getDescription());
    fund.setLogoPath(updatedData.getLogoPath());
    fund.setHeaderPath(updatedData.getHeaderPath());
    fund.setFooterPath(updatedData.getFooterPath());

    return fundRepository.save(fund);
}
}