package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.FundModel;
import com.hr.backend.fin_repository.FundRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/funds")
@CrossOrigin(origins = "*")
public class FundController {

    @Autowired
    private FundRepository fundRepository;

   @PostMapping
public ResponseEntity<?> saveFund(@RequestBody FundModel fund) {
    try {
        System.out.println("===== FUND RECEIVED =====");
        System.out.println("Fund Code: " + fund.getFundCode());
        System.out.println("Fund Name: " + fund.getFundName());
        System.out.println("Fund Type: " + fund.getFundType());
        System.out.println("Donor: " + fund.getDonor());
        System.out.println("Currency: " + fund.getCurrency());
        System.out.println("Budget Year: " + fund.getBudgetYear());
        System.out.println("Organization: " + fund.getOrganization());
        System.out.println("Created By: " + fund.getCreatedBy());

        FundModel saved = fundRepository.save(fund);

        return ResponseEntity.ok(saved);

    } catch (Exception e) {
        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Backend error: " + e.getClass().getName() + " - " + e.getMessage());
    }
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