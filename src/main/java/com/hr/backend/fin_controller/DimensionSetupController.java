/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.DimensionSetupModel;
import com.hr.backend.fin_repository.DimensionSetupRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dimension-setups")
@CrossOrigin(origins = "*")
public class DimensionSetupController {

    @Autowired
    private DimensionSetupRepository dimensionRepository;

    @PostMapping
    public ResponseEntity<?> saveDimension(@RequestBody DimensionSetupModel dimension) {
        try {
            if (isEmpty(dimension.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(dimension.getDimensionCode())) {
                return ResponseEntity.badRequest().body("Dimension code is required");
            }

            boolean exists = dimensionRepository.existsByOrganizationAndDimensionCode(
                    dimension.getOrganization(),
                    dimension.getDimensionCode()
            );

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Dimension already exists");
            }

            if (dimension.getBlocked() == null) {
                dimension.setBlocked(false);
            }

            if (dimension.getRequired() == null) {
                dimension.setRequired(false);
            }

            if (dimension.getShowInActual() == null) {
                dimension.setShowInActual(true);
            }

            if (dimension.getDisplayOrder() == null) {
                dimension.setDisplayOrder(0);
            }

            if (isEmpty(dimension.getStatus())) {
                dimension.setStatus("Active");
            }

            if (isEmpty(dimension.getCreatedDate())) {
                dimension.setCreatedDate(todayDate());
            }

            return ResponseEntity.ok(dimensionRepository.save(dimension));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Save dimension failed: " + e.getMessage());
        }
    }

    @PutMapping("/dimension-info")
    public ResponseEntity<?> updateDimension(@RequestBody DimensionSetupModel updatedData) {
        try {
            if (isEmpty(updatedData.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(updatedData.getDimensionCode())) {
                return ResponseEntity.badRequest().body("Dimension code is required");
            }

            DimensionSetupModel dimension = dimensionRepository
                    .findByOrganizationAndDimensionCode(
                            updatedData.getOrganization(),
                            updatedData.getDimensionCode()
                    )
                    .orElseThrow(() -> new RuntimeException("Dimension not found"));

            dimension.setDimensionName(updatedData.getDimensionName());
            dimension.setDescription(updatedData.getDescription());

            if (updatedData.getBlocked() != null) {
                dimension.setBlocked(updatedData.getBlocked());
            }

            if (updatedData.getRequired() != null) {
                dimension.setRequired(updatedData.getRequired());
            }

            if (updatedData.getShowInActual() != null) {
                dimension.setShowInActual(updatedData.getShowInActual());
            }

            if (updatedData.getDisplayOrder() != null) {
                dimension.setDisplayOrder(updatedData.getDisplayOrder());
            }

            if (!isEmpty(updatedData.getStatus())) {
                dimension.setStatus(updatedData.getStatus());
            }

            return ResponseEntity.ok(dimensionRepository.save(dimension));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Update dimension failed: " + e.getMessage());
        }
    }

    @GetMapping("/organization/{organization}")
    public List<DimensionSetupModel> getByOrganization(
            @PathVariable String organization) {

        return dimensionRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/active")
    public List<DimensionSetupModel> getActiveByOrganization(
            @PathVariable String organization) {

        return dimensionRepository.findByOrganizationAndBlockedFalse(organization);
    }

    @GetMapping("/organization/{organization}/actual-columns")
    public List<DimensionSetupModel> getActualColumns(
            @PathVariable String organization) {

        return dimensionRepository
                .findByOrganizationAndBlockedFalseAndShowInActualTrueOrderByDisplayOrderAsc(
                        organization
                );
    }

    @GetMapping("/organization/{organization}/code/{dimensionCode}")
    public ResponseEntity<?> getOneDimension(
            @PathVariable String organization,
            @PathVariable String dimensionCode) {

        return dimensionRepository
                .findByOrganizationAndDimensionCode(organization, dimensionCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/test")
    public String test() {
        return "Dimension Setup API is working";
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String todayDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
 
}
