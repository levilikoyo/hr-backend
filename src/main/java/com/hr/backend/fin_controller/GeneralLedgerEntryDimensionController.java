/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.GeneralLedgerEntryDimensionModel;
import com.hr.backend.fin_repository.GeneralLedgerEntryDimensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/general-ledger-entry-dimensions")
@CrossOrigin(origins = "*")
public class GeneralLedgerEntryDimensionController {

    @Autowired
    private GeneralLedgerEntryDimensionRepository repository;

    @PostMapping
    public GeneralLedgerEntryDimensionModel save(
            @RequestBody GeneralLedgerEntryDimensionModel dimension
    ) {
        return repository.save(dimension);
    }

    @PutMapping("/dimension-info")
    public GeneralLedgerEntryDimensionModel update(
            @RequestBody GeneralLedgerEntryDimensionModel dimension
    ) {
        GeneralLedgerEntryDimensionModel existing =
                repository.findByOrganizationAndGlEntryIdAndDimensionCode(
                        dimension.getOrganization(),
                        dimension.getGlEntryId(),
                        dimension.getDimensionCode()
                ).orElse(null);

        if (existing != null) {
            dimension.setId(existing.getId());
        }

        return repository.save(dimension);
    }

    @PostMapping("/save-all")
    public List<GeneralLedgerEntryDimensionModel> saveAll(
            @RequestBody List<GeneralLedgerEntryDimensionModel> dimensions
    ) {
        return repository.saveAll(dimensions);
    }

    @GetMapping("/organization/{organization}")
    public List<GeneralLedgerEntryDimensionModel> getByOrganization(
            @PathVariable String organization
    ) {
        return repository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/gl-entry/{glEntryId}")
    public List<GeneralLedgerEntryDimensionModel> getByGlEntryId(
            @PathVariable String organization,
            @PathVariable Long glEntryId
    ) {
        return repository.findByOrganizationAndGlEntryId(organization, glEntryId);
    }

    @GetMapping("/organization/{organization}/document/{documentNo}")
    public List<GeneralLedgerEntryDimensionModel> getByDocumentNo(
            @PathVariable String organization,
            @PathVariable String documentNo
    ) {
        return repository.findByOrganizationAndDocumentNo(organization, documentNo);
    }

   

    @GetMapping("/test")
    public String test() {
        return "General Ledger Entry Dimensions API is working.";
    }
    
@DeleteMapping("/organization/{organization}/gl-entry/{glEntryId}")
public String deleteByGlEntryId(
        @PathVariable String organization,
        @PathVariable Long glEntryId
) {
    try {
        int deletedRows =
                repository.deleteDimensionsByGlEntryId(
                        organization.trim(),
                        glEntryId
                );

        System.out.println("Deleted dimension rows: " + deletedRows);

        return "SUCCESS";

    } catch (Exception e) {
        e.printStackTrace();
        return "ERROR: " + e.getMessage();
    }
}
}
