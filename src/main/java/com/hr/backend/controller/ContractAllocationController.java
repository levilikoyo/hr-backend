/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */

import com.hr.backend.model.ContractAllocation;
import com.hr.backend.repository.ContractAllocationRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/contract-allocation")
@CrossOrigin
public class ContractAllocationController {

    private final ContractAllocationRepository repository;

    public ContractAllocationController(ContractAllocationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ContractAllocation saveAllocation(@RequestBody ContractAllocation allocation) {
        requireText(allocation.getOrganization(), "Organization is required");
        requireText(allocation.getEmployeeRoll(), "Employee roll is required");
        requireText(allocation.getContractCode(), "Contract code is required");
        allocation.setOrganization(cleanText(allocation.getOrganization()));
        allocation.setEmployeeRoll(cleanCode(allocation.getEmployeeRoll()));
        allocation.setContractCode(cleanCode(allocation.getContractCode()));
        if (allocation.getCreatedDate() == null || allocation.getCreatedDate().trim().isEmpty()) {
            allocation.setCreatedDate(LocalDate.now().toString());
        }
        allocation.setUpdatedDate(LocalDate.now().toString());
        return repository.save(allocation);
    }

    @GetMapping
    public List<ContractAllocation> getAllAllocations() {
        return repository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<ContractAllocation> getByOrganization(@PathVariable String organization) {
        return repository.findByOrganizationIgnoreCase(cleanText(organization));
    }

    @GetMapping("/employee/{employeeRoll}/organization/{organization}")
    public List<ContractAllocation> getByEmployeeRollAndOrganization(@PathVariable String employeeRoll,
                                                                     @PathVariable String organization) {
        return repository.findByEmployeeRollAndOrganizationIgnoreCase(cleanCode(employeeRoll), cleanText(organization));
    }

    @GetMapping("/contract/{contractCode}/organization/{organization}")
    public List<ContractAllocation> getByContractCodeAndOrganization(@PathVariable String contractCode,
                                                                     @PathVariable String organization) {
        return repository.findByContractCodeAndOrganizationIgnoreCase(cleanCode(contractCode), cleanText(organization));
    }

    @PutMapping("/{id}")
    public ContractAllocation updateAllocation(@PathVariable Integer id, @RequestBody ContractAllocation updated) {
        ContractAllocation allocation = repository.findById(id)
                .orElseThrow(() -> notFound("Allocation not found"));

        requireText(updated.getOrganization(), "Organization is required");
        requireText(updated.getEmployeeRoll(), "Employee roll is required");
        requireText(updated.getContractCode(), "Contract code is required");
        allocation.setOrganization(cleanText(updated.getOrganization()));
        allocation.setEmployeeId(updated.getEmployeeId());
        allocation.setEmployeeRoll(cleanCode(updated.getEmployeeRoll()));
        allocation.setEmployeeName(updated.getEmployeeName());
        allocation.setContractId(updated.getContractId());
        allocation.setContractCode(cleanCode(updated.getContractCode()));
        allocation.setContractName(updated.getContractName());
        allocation.setAllocationDate(updated.getAllocationDate());
        allocation.setEffectiveStartDate(updated.getEffectiveStartDate());
        allocation.setEffectiveEndDate(updated.getEffectiveEndDate());
        allocation.setSalaryOverride(updated.getSalaryOverride());
        allocation.setStatus(updated.getStatus());
        allocation.setNotes(updated.getNotes());
        allocation.setUpdatedDate(LocalDate.now().toString());

        return repository.save(allocation);
    }

    @DeleteMapping("/{id}")
    public String deleteAllocation(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw notFound("Allocation not found");
        }
        repository.deleteById(id);
        return "Allocation deleted successfully";
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
