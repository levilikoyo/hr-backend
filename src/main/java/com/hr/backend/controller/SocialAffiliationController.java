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
import org.springframework.web.bind.annotation.*;

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
        return repository.save(socialAffiliation);
    }

    @GetMapping
    public List<SocialAffiliation> getAll() {
        return repository.findAll();
    }

    @GetMapping("/organisation/{organisation}")
    public List<SocialAffiliation> getByOrganisation(@PathVariable String organisation) {
        return repository.findByOrganisationIgnoreCase(organisation);
    }

    @GetMapping("/employee/{empCode}/organisation/{organisation}")
    public SocialAffiliation getByEmployeeAndOrganisation(
            @PathVariable String empCode,
            @PathVariable String organisation) {

        return repository.findByEmpCodeAndOrganisationIgnoreCase(empCode, organisation)
                .orElseThrow(() -> new RuntimeException("Social affiliation not found"));
    }

    @PutMapping("/{id}")
    public SocialAffiliation update(@PathVariable Integer id, @RequestBody SocialAffiliation updated) {
        SocialAffiliation s = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social affiliation not found"));

        s.setOrganisation(updated.getOrganisation());
        s.setHiringDate(updated.getHiringDate());
        s.setEmpCode(updated.getEmpCode());
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
        repository.deleteById(id);
        return "Social affiliation deleted successfully";
    }
}