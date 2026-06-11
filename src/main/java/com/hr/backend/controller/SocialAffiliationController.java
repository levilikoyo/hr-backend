/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */

import com.hr.backend.model.SocialAffiliation;
import com.hr.backend.repository.SocialAffiliationRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/social-affiliation")
@CrossOrigin
public class SocialAffiliationController {

    private final SocialAffiliationRepository repository;

    public SocialAffiliationController(SocialAffiliationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public SocialAffiliation save(@RequestBody SocialAffiliation socialAffiliation) {
        requireText(socialAffiliation.getOrganisation(), "Organisation is required");
        requireText(socialAffiliation.getEmpCode(), "Employee code is required");
        socialAffiliation.setOrganisation(cleanText(socialAffiliation.getOrganisation()));
        socialAffiliation.setEmpCode(cleanCode(socialAffiliation.getEmpCode()));
        return repository.save(socialAffiliation);
    }

    @GetMapping
    public List<SocialAffiliation> getAll() {
        return repository.findAll();
    }

    @GetMapping("/organisation/{organisation}")
    public List<SocialAffiliation> getByOrganisation(@PathVariable String organisation) {
        return repository.findByOrganisationIgnoreCase(cleanText(organisation));
    }

    @GetMapping("/organization/{organization}")
    public List<SocialAffiliation> getByOrganization(@PathVariable String organization) {
        return repository.findByOrganisationIgnoreCase(cleanText(organization));
    }

    @GetMapping("/employee/{empCode}/organisation/{organisation}")
    public SocialAffiliation getByEmployeeAndOrganisation(
            @PathVariable String empCode,
            @PathVariable String organisation) {

        return repository.findByEmpCodeAndOrganisationIgnoreCase(cleanCode(empCode), cleanText(organisation))
                .orElseThrow(() -> notFound("Social affiliation not found"));
    }

    @GetMapping("/employee/{empCode}/organization/{organization}")
    public SocialAffiliation getByEmployeeAndOrganization(
            @PathVariable String empCode,
            @PathVariable String organization) {

        return repository.findByEmpCodeAndOrganisationIgnoreCase(cleanCode(empCode), cleanText(organization))
                .orElseThrow(() -> notFound("Social affiliation not found"));
    }

    @PutMapping("/{id}")
    public SocialAffiliation update(@PathVariable Integer id, @RequestBody SocialAffiliation updated) {
        SocialAffiliation s = repository.findById(id)
                .orElseThrow(() -> notFound("Social affiliation not found"));

        requireText(updated.getOrganisation(), "Organisation is required");
        requireText(updated.getEmpCode(), "Employee code is required");
        s.setOrganisation(cleanText(updated.getOrganisation()));
        s.setHiringDate(updated.getHiringDate());
        s.setEmpCode(cleanCode(updated.getEmpCode()));
        s.setEmpName(updated.getEmpName());
        s.setSex(updated.getSex());
        s.setFatherName(updated.getFatherName());
        s.setMotherName(updated.getMotherName());
        s.setDob(updated.getDob());
        s.setLocality(updated.getLocality());
        s.setCommunity(updated.getCommunity());
        s.setTerritory(updated.getTerritory());
        s.setProvince(updated.getProvince());
        s.setCountry(updated.getCountry());
        s.setJobTitle(updated.getJobTitle());
        s.setProfCategory(updated.getProfCategory());
        s.setInssuanceAffNo(updated.getInssuanceAffNo());
        s.setIssuanceDate(updated.getIssuanceDate());
        s.setIssuancePlace(updated.getIssuancePlace());
        s.setIdentityId(updated.getIdentityId());
        s.setNationalId(updated.getNationalId());
        s.setMaritalStatus(updated.getMaritalStatus());
        s.setPartnerName(updated.getPartnerName());
        s.setNbrChildren(updated.getNbrChildren());
        s.setEmployerNo(updated.getEmployerNo());
        s.setEmployerName(updated.getEmployerName());

        return repository.save(s);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw notFound("Social affiliation not found");
        }
        repository.deleteById(id);
        return "Social affiliation deleted successfully";
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
