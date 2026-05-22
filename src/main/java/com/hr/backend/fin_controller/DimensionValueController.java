/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.DimensionValueModel;
import com.hr.backend.fin_repository.DimensionValueRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dimension-values")
@CrossOrigin(origins = "*")
public class DimensionValueController {

    @Autowired
    private DimensionValueRepository valueRepository;

    @PostMapping
    public ResponseEntity<?> saveValue(@RequestBody DimensionValueModel value) {
        try {
            if (isEmpty(value.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(value.getDimensionCode())) {
                return ResponseEntity.badRequest().body("Dimension code is required");
            }

            if (isEmpty(value.getValueCode())) {
                return ResponseEntity.badRequest().body("Value code is required");
            }

            boolean exists = valueRepository.existsByOrganizationAndDimensionCodeAndValueCode(
                    value.getOrganization(),
                    value.getDimensionCode(),
                    value.getValueCode()
            );

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Dimension value already exists");
            }

            if (value.getBlocked() == null) {
                value.setBlocked(false);
            }

            if (isEmpty(value.getStatus())) {
                value.setStatus("Active");
            }

            if (isEmpty(value.getCreatedDate())) {
                value.setCreatedDate(todayDate());
            }

            return ResponseEntity.ok(valueRepository.save(value));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Save dimension value failed: " + e.getMessage());
        }
    }

    @PutMapping("/value-info")
    public ResponseEntity<?> updateValue(@RequestBody DimensionValueModel updatedData) {
        try {
            if (isEmpty(updatedData.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(updatedData.getDimensionCode())) {
                return ResponseEntity.badRequest().body("Dimension code is required");
            }

            if (isEmpty(updatedData.getValueCode())) {
                return ResponseEntity.badRequest().body("Value code is required");
            }

            DimensionValueModel value = valueRepository
                    .findByOrganizationAndDimensionCodeAndValueCode(
                            updatedData.getOrganization(),
                            updatedData.getDimensionCode(),
                            updatedData.getValueCode()
                    )
                    .orElseThrow(() -> new RuntimeException("Dimension value not found"));

            value.setDimensionName(updatedData.getDimensionName());
            value.setValueName(updatedData.getValueName());
            value.setDescription(updatedData.getDescription());

            if (updatedData.getBlocked() != null) {
                value.setBlocked(updatedData.getBlocked());
            }

            if (!isEmpty(updatedData.getStatus())) {
                value.setStatus(updatedData.getStatus());
            }

            return ResponseEntity.ok(valueRepository.save(value));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Update dimension value failed: " + e.getMessage());
        }
    }

    @GetMapping("/organization/{organization}")
    public List<DimensionValueModel> getByOrganization(
            @PathVariable String organization) {

        return valueRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/dimension/{dimensionCode}")
    public List<DimensionValueModel> getByDimension(
            @PathVariable String organization,
            @PathVariable String dimensionCode) {

        return valueRepository.findByOrganizationAndDimensionCode(
                organization,
                dimensionCode
        );
    }

    @GetMapping("/organization/{organization}/dimension/{dimensionCode}/active")
    public List<DimensionValueModel> getActiveByDimension(
            @PathVariable String organization,
            @PathVariable String dimensionCode) {

        return valueRepository.findByOrganizationAndDimensionCodeAndBlockedFalse(
                organization,
                dimensionCode
        );
    }

    @GetMapping("/organization/{organization}/dimension/{dimensionCode}/value/{valueCode}")
    public ResponseEntity<?> getOneValue(
            @PathVariable String organization,
            @PathVariable String dimensionCode,
            @PathVariable String valueCode) {

        return valueRepository
                .findByOrganizationAndDimensionCodeAndValueCode(
                        organization,
                        dimensionCode,
                        valueCode
                )
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/test")
    public String test() {
        return "Dimension Value API is working";
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String todayDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
