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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        requireText(dependant.getOrganization(), "Organization is required");
        requireText(dependant.getEmployeeId(), "Employee ID is required");
        dependant.setOrganization(cleanText(dependant.getOrganization()));
        dependant.setEmployeeId(cleanCode(dependant.getEmployeeId()));
        return repository.save(dependant);
    }

    @GetMapping
    public List<Dependant> getAllDependants() {
        return repository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<Dependant> getByOrganization(@PathVariable String organization) {
        return repository.findByOrganizationIgnoreCase(cleanText(organization));
    }

    @GetMapping("/employee/{employeeId}/organization/{organization}")
    public List<Dependant> getByEmployeeAndOrganization(
            @PathVariable String employeeId,
            @PathVariable String organization) {
        return repository.findByEmployeeIdAndOrganizationIgnoreCase(cleanCode(employeeId), cleanText(organization));
    }

    @PutMapping("/{id}")
    public Dependant updateDependant(@PathVariable Integer id, @RequestBody Dependant updated) {
        Dependant dep = repository.findById(id)
                .orElseThrow(() -> notFound("Dependant not found"));

        requireText(updated.getOrganization(), "Organization is required");
        requireText(updated.getEmployeeId(), "Employee ID is required");
        dep.setOrganization(cleanText(updated.getOrganization()));
        dep.setEmployeeId(cleanCode(updated.getEmployeeId()));
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
        if (!repository.existsById(id)) {
            throw notFound("Dependant not found");
        }
        repository.deleteById(id);
        return "Dependant deleted successfully";
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
