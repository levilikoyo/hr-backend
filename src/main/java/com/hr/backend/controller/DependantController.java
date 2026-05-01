/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */

import com.hr.backend.model.Dependant;
import com.hr.backend.repository.DependantRepository;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dependants")
@CrossOrigin
public class DependantController {

    private final DependantRepository repository;

    public DependantController(DependantRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Dependant saveDependant(@RequestBody Dependant dependant) {
        return repository.save(dependant);
    }

    @GetMapping
    public List<Dependant> getAllDependants() {
        return repository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<Dependant> getByOrganization(@PathVariable String organization) {
        return repository.findByOrganizationIgnoreCase(organization);
    }

    @GetMapping("/employee/{employeeId}/organization/{organization}")
    public List<Dependant> getByEmployeeAndOrganization(
            @PathVariable String employeeId,
            @PathVariable String organization) {
        return repository.findByEmployeeIdAndOrganizationIgnoreCase(employeeId, organization);
    }

    @PutMapping("/{id}")
    public Dependant updateDependant(@PathVariable Integer id, @RequestBody Dependant updated) {
        Dependant dep = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dependant not found"));

        dep.setOrganization(updated.getOrganization());
        dep.setEmployeeId(updated.getEmployeeId());
        dep.setEmployeeName(updated.getEmployeeName());
        dep.setDependantName(updated.getDependantName());
        dep.setDob(updated.getDob());
        dep.setGender(updated.getGender());
        dep.setPhone(updated.getPhone());
        dep.setMail(updated.getMail());
        dep.setAddress(updated.getAddress());
        dep.setRelationship(updated.getRelationship());

        return repository.save(dep);
    }

    @DeleteMapping("/{id}")
    public String deleteDependant(@PathVariable Integer id) {
        repository.deleteById(id);
        return "Dependant deleted successfully";
    }
}
