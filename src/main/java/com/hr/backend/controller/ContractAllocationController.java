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
import org.springframework.web.bind.annotation.*;

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
        return repository.findByOrganizationIgnoreCase(organization);
    }

    @GetMapping("/employee/{employeeRoll}/organization/{organization}")
    public List<ContractAllocation> getByEmployeeRollAndOrganization(@PathVariable String employeeRoll,
                                                                     @PathVariable String organization) {
        return repository.findByEmployeeRollAndOrganizationIgnoreCase(employeeRoll, organization);
    }

    @PutMapping("/{id}")
    public ContractAllocation updateAllocation(@PathVariable Integer id, @RequestBody ContractAllocation updated) {
        ContractAllocation allocation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Allocation not found"));

        allocation.setOrganization(updated.getOrganization());
        allocation.setEmployeeId(updated.getEmployeeId());
        allocation.setEmployeeRoll(updated.getEmployeeRoll());
        allocation.setEmployeeName(updated.getEmployeeName());
        allocation.setContractId(updated.getContractId());
        allocation.setContractCode(updated.getContractCode());
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
        repository.deleteById(id);
        return "Allocation deleted successfully";
    }
}
