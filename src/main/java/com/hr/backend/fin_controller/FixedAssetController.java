/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.FixedAssetModel;
import com.hr.backend.fin_repository.FixedAssetRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fixed-assets")
@CrossOrigin(origins = "*")
public class FixedAssetController {

    @Autowired
    private FixedAssetRepository fixedAssetRepository;

    @PostMapping
    public ResponseEntity<?> saveFixedAsset(@RequestBody FixedAssetModel fixedAsset) {
        try {
            if (fixedAsset.getOrganization() == null || fixedAsset.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (fixedAsset.getAssetCode() == null || fixedAsset.getAssetCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Asset code is required");
            }

            boolean exists = fixedAssetRepository.existsByAssetCodeAndOrganization(
                    fixedAsset.getAssetCode(),
                    fixedAsset.getOrganization()
            );

            if (exists) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Duplicate fixed asset code for this organization: " + fixedAsset.getAssetCode());
            }

            FixedAssetModel saved = fixedAssetRepository.save(fixedAsset);
            return ResponseEntity.ok(saved);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Duplicate fixed asset code for this organization: " + fixedAsset.getAssetCode());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fixed asset save failed: " + e.getMessage());
        }
    }

    @GetMapping
    public List<FixedAssetModel> getAllFixedAssets() {
        return fixedAssetRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<FixedAssetModel> getFixedAssetsByOrganization(@PathVariable String organization) {
        return fixedAssetRepository.findByOrganization(organization);
    }

    @PutMapping("/asset-info")
    public ResponseEntity<?> updateFixedAssetInfo(@RequestBody FixedAssetModel updatedData) {
        try {
            if (updatedData.getOrganization() == null || updatedData.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (updatedData.getAssetCode() == null || updatedData.getAssetCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Asset code is required");
            }

            FixedAssetModel asset = fixedAssetRepository
                    .findByAssetCodeAndOrganization(
                            updatedData.getAssetCode(),
                            updatedData.getOrganization()
                    )
                    .orElseThrow(() -> new RuntimeException(
                            "Fixed asset not found for code: "
                            + updatedData.getAssetCode()
                            + " and organization: "
                            + updatedData.getOrganization()
                    ));

            asset.setAssetName(updatedData.getAssetName());
            asset.setAssetDescription(updatedData.getAssetDescription());
            asset.setAssetSubClass(updatedData.getAssetSubClass());
            asset.setSerialNo(updatedData.getSerialNo());
            asset.setAssetTagNum(updatedData.getAssetTagNum());
            asset.setResponsibleEmployee(updatedData.getResponsibleEmployee());
            asset.setDepreciationMethod(updatedData.getDepreciationMethod());
            asset.setDepreciationStartingDate(updatedData.getDepreciationStartingDate());
            asset.setBookValue(updatedData.getBookValue());
            asset.setStatus(updatedData.getStatus());

            return ResponseEntity.ok(fixedAssetRepository.save(asset));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fixed asset update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/organization/{organization}/code/{assetCode}")
    public ResponseEntity<?> deleteFixedAsset(
            @PathVariable String organization,
            @PathVariable String assetCode) {
        try {
            FixedAssetModel asset = fixedAssetRepository
                    .findByAssetCodeAndOrganization(assetCode, organization)
                    .orElseThrow(() -> new RuntimeException("Fixed asset not found"));

            fixedAssetRepository.delete(asset);

            return ResponseEntity.ok("Fixed asset deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fixed asset delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Fixed Asset API is working";
    }
}
