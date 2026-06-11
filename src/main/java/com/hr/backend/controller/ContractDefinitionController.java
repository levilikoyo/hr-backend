/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */


import com.hr.backend.model.ContractDefinition;
import com.hr.backend.repository.ContractDefinitionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin
public class ContractDefinitionController {

    private final ContractDefinitionRepository repository;

    public ContractDefinitionController(ContractDefinitionRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ContractDefinition saveContract(@RequestBody ContractDefinition contract) {
        requireText(contract.getOrganization(), "Organization is required");
        requireText(contract.getContractCode(), "Contract code is required");
        contract.setOrganization(cleanText(contract.getOrganization()));
        contract.setContractCode(cleanCode(contract.getContractCode()));
        if (contract.getCreatedDate() == null || contract.getCreatedDate().trim().isEmpty()) {
            contract.setCreatedDate(LocalDate.now().toString());
        }
        contract.setUpdatedDate(LocalDate.now().toString());
        return repository.save(contract);
    }

    @GetMapping
    public List<ContractDefinition> getAllContracts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ContractDefinition getContractById(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> notFound("Contract not found"));
    }

    @GetMapping("/code/{contractCode}")
    public ContractDefinition getContractByCode(@PathVariable String contractCode) {
        return repository.findByContractCode(cleanCode(contractCode))
                .orElseThrow(() -> notFound("Contract not found"));
    }

    @GetMapping("/code/{contractCode}/organization/{organization}")
    public ContractDefinition getContractByCodeAndOrganization(
            @PathVariable String contractCode,
            @PathVariable String organization) {
        return repository.findByContractCodeAndOrganization(cleanCode(contractCode), cleanText(organization))
                .orElseThrow(() -> notFound("Contract not found"));
    }

    @PutMapping("/{id}")
    public ContractDefinition updateContract(@PathVariable Integer id, @RequestBody ContractDefinition updated) {
        ContractDefinition contract = repository.findById(id)
                .orElseThrow(() -> notFound("Contract not found"));

        requireText(updated.getOrganization(), "Organization is required");
        requireText(updated.getContractCode(), "Contract code is required");
        contract.setOrganization(cleanText(updated.getOrganization()));
        contract.setContractCode(cleanCode(updated.getContractCode()));
        contract.setContractName(updated.getContractName());
        contract.setContractType(updated.getContractType());
        contract.setContractDescription(updated.getContractDescription());
        contract.setJobTitle(updated.getJobTitle());
        contract.setDepartment(updated.getDepartment());
        contract.setSalary(updated.getSalary());
        contract.setCurrency(updated.getCurrency());
        contract.setStartDate(updated.getStartDate());
        contract.setEndDate(updated.getEndDate());
        contract.setDurationValue(updated.getDurationValue());
        contract.setDurationUnit(updated.getDurationUnit());
        contract.setProbationPeriod(updated.getProbationPeriod());
        contract.setRenewalAllowed(updated.getRenewalAllowed());
        contract.setNoticePeriod(updated.getNoticePeriod());
        contract.setWorkingHoursPerDay(updated.getWorkingHoursPerDay());
        contract.setWorkingDaysPerWeek(updated.getWorkingDaysPerWeek());
        contract.setWorkingDaysPerMonth(updated.getWorkingDaysPerMonth());
        contract.setContractStatus(updated.getContractStatus());
        contract.setBenefits(updated.getBenefits());
        contract.setOvertimeAllowed(updated.getOvertimeAllowed());
        contract.setLeaveEntitlement(updated.getLeaveEntitlement());
        contract.setNotes(updated.getNotes());
        contract.setUpdatedDate(LocalDate.now().toString());

        return repository.save(contract);
    }

    @DeleteMapping("/{id}")
    public String deleteContract(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw notFound("Contract not found");
        }
        repository.deleteById(id);
        return "Contract deleted successfully";
    }
    @GetMapping("/organization/{organization}")
public List<ContractDefinition> getContractsByOrganization(@PathVariable String organization) {
    return repository.findByOrganization(cleanText(organization));
}

@PutMapping("/contract-info")
public ContractDefinition updateContractBycodeOrg(@RequestBody ContractDefinition updatedData) {
   requireText(updatedData.getOrganization(), "Organization is required");
   requireText(updatedData.getContractCode(), "Contract code is required");
   ContractDefinition contract = repository
           
           .findByContractCodeAndOrganization(cleanCode(updatedData.getContractCode()), cleanText(updatedData.getOrganization()))
            .orElseThrow(() -> notFound("Contract not found"));

   contract.setContractStatus(updatedData.getContractStatus());
   contract.setBenefits(updatedData.getBenefits());
   contract.setOvertimeAllowed(updatedData.getOvertimeAllowed());
   contract.setLeaveEntitlement(updatedData.getLeaveEntitlement());
   contract.setUpdatedDate(LocalDate.now().toString());


    return repository.save(contract);
}

    private void requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanCode(String value) {
        return cleanText(value).toUpperCase();
    }
}
