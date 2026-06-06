package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.FundModel;
import com.hr.backend.fin_repository.FundRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import jakarta.transaction.Transactional;
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
        if (fund.getOrganization() == null || fund.getOrganization().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Organization is required");
        }
        if (fund.getFundCode() == null || fund.getFundCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Fund code is required");
        }

        fund.setOrganization(fund.getOrganization().trim());
        fund.setFundCode(fund.getFundCode().trim());

        if (fundRepository.existsByFundCodeAndOrganization(fund.getFundCode(), fund.getOrganization())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Fund code already exists for this organization");
        }

        if (fund.getCurrency() == null || fund.getCurrency().trim().isEmpty()) {
            fund.setCurrency("USD");
        }

        fund.setCurencyCode(fund.getCurrency());

        if ("USD".equalsIgnoreCase(fund.getCurrency())) {
            fund.setCurencyName("US Dollar");
            fund.setCurencySymbole("$");
        } else if ("EUR".equalsIgnoreCase(fund.getCurrency())) {
            fund.setCurencyName("Euro");
            fund.setCurencySymbole("€");
        } else if ("FBU".equalsIgnoreCase(fund.getCurrency())) {
            fund.setCurencyName("Burundi Franc");
            fund.setCurencySymbole("FBu");
        } else {
            fund.setCurencyName(fund.getCurrency());
            fund.setCurencySymbole(fund.getCurrency());
        }

        FundModel saved = fundRepository.save(fund);
        return ResponseEntity.ok(saved);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    if (updatedData.getFundName() != null) {
        fund.setFundName(updatedData.getFundName());
    }
    if (updatedData.getFundType() != null) {
        fund.setFundType(updatedData.getFundType());
    }
    if (updatedData.getDonor() != null) {
        fund.setDonor(updatedData.getDonor());
    }
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

@Transactional
@DeleteMapping("/organization/{organization}/code/{fundCode}")
public ResponseEntity<?> deleteFundByCode(
        @PathVariable String organization,
        @PathVariable String fundCode
) {
    String cleanOrganization = clean(organization);
    String cleanFundCode = clean(fundCode);

    boolean exists = fundRepository.existsByFundCodeAndOrganization(
            cleanFundCode,
            cleanOrganization
    );

    if (!exists) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Fund not found");
    }

    fundRepository.deleteByFundCodeAndOrganization(cleanFundCode, cleanOrganization);
    return ResponseEntity.ok("Fund deleted successfully");
}

private String clean(String value) {
    return value == null ? "" : value.trim();
}
}
