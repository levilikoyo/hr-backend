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
}