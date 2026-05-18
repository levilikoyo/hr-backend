package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.AccountingFrameworkModel;
import com.hr.backend.fin_repository.AccountingFrameworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounting-frameworks")
@CrossOrigin(origins = "*")
public class AccountingFrameworkController {

    @Autowired
    private AccountingFrameworkRepository accountingFrameworkRepository;

    @GetMapping("/test")
    public String test() {
        return "Accounting Framework API is working successfully!";
    }

    @PostMapping
    public AccountingFrameworkModel saveAccountingFramework(
            @RequestBody AccountingFrameworkModel framework
    ) {
        if (framework.getOrganization() == null || framework.getOrganization().trim().isEmpty()) {
            throw new RuntimeException("Organization is required");
        }

        if (framework.getFrameworkCode() == null || framework.getFrameworkCode().trim().isEmpty()) {
            throw new RuntimeException("Framework code is required");
        }

        boolean exists = accountingFrameworkRepository.existsByOrganizationAndFrameworkCode(
                framework.getOrganization(),
                framework.getFrameworkCode()
        );

        if (exists) {
            throw new RuntimeException(
                    "Accounting framework already exists for this organization and framework code"
            );
        }

        return accountingFrameworkRepository.save(framework);
    }

    @GetMapping
    public List<AccountingFrameworkModel> getAllAccountingFrameworks() {
        return accountingFrameworkRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<AccountingFrameworkModel> getAccountingFrameworksByOrganization(
            @PathVariable String organization
    ) {
        return accountingFrameworkRepository.findByOrganization(organization);
    }

    @GetMapping("/{id}")
    public AccountingFrameworkModel getAccountingFrameworkById(@PathVariable Long id) {
        return accountingFrameworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Accounting framework not found"));
    }

    @PutMapping("/{id}")
    public AccountingFrameworkModel updateAccountingFramework(
            @PathVariable Long id,
            @RequestBody AccountingFrameworkModel frameworkDetails
    ) {
        AccountingFrameworkModel framework = accountingFrameworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Accounting framework not found"));

        accountingFrameworkRepository
                .findByOrganizationAndFrameworkCode(
                        frameworkDetails.getOrganization(),
                        frameworkDetails.getFrameworkCode()
                )
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new RuntimeException(
                                "Another accounting framework already exists with this organization and framework code"
                        );
                    }
                });

        framework.setOrganization(frameworkDetails.getOrganization());
        framework.setFrameworkCode(frameworkDetails.getFrameworkCode());
        framework.setFrameworkName(frameworkDetails.getFrameworkName());
        framework.setCountry(frameworkDetails.getCountry());
        framework.setDescription(frameworkDetails.getDescription());
        framework.setStatus(frameworkDetails.getStatus());
        framework.setCreatedDate(frameworkDetails.getCreatedDate());

        return accountingFrameworkRepository.save(framework);
    }

    @DeleteMapping("/{id}")
    public String deleteAccountingFramework(@PathVariable Long id) {
        AccountingFrameworkModel framework = accountingFrameworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Accounting framework not found"));

        accountingFrameworkRepository.delete(framework);

        return "Accounting framework deleted successfully";
    }
}