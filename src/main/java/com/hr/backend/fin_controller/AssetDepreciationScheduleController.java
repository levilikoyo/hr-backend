/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.AssetDepreciationScheduleModel;
import com.hr.backend.fin_repository.AssetDepreciationScheduleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset-depreciation-schedules")
@CrossOrigin(origins = "*")
public class AssetDepreciationScheduleController {

    @Autowired
    private AssetDepreciationScheduleRepository scheduleRepository;

    @PostMapping
    public ResponseEntity<?> saveSchedule(@RequestBody AssetDepreciationScheduleModel schedule) {
        try {
            if (schedule.getOrganization() == null || schedule.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (schedule.getAssetCode() == null || schedule.getAssetCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Asset code is required");
            }

            if (schedule.getBookCode() == null || schedule.getBookCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Book code is required");
            }

            if (schedule.getDepreciationPeriod() == null || schedule.getDepreciationPeriod().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Depreciation period is required");
            }

            boolean exists = scheduleRepository
                    .existsByAssetCodeAndBookCodeAndDepreciationPeriodAndOrganization(
                            schedule.getAssetCode(),
                            schedule.getBookCode(),
                            schedule.getDepreciationPeriod(),
                            schedule.getOrganization()
                    );

            if (exists) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Depreciation schedule already exists");
            }

            if (schedule.getPosted() == null) {
                schedule.setPosted(false);
            }

            return ResponseEntity.ok(scheduleRepository.save(schedule));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Depreciation schedule save failed: " + e.getMessage());
        }
    }

    @PutMapping("/schedule-info")
    public ResponseEntity<?> updateSchedule(@RequestBody AssetDepreciationScheduleModel updatedData) {
        try {
            AssetDepreciationScheduleModel schedule = scheduleRepository
                    .findByAssetCodeAndBookCodeAndDepreciationPeriodAndOrganization(
                            updatedData.getAssetCode(),
                            updatedData.getBookCode(),
                            updatedData.getDepreciationPeriod(),
                            updatedData.getOrganization()
                    )
                    .orElseThrow(() -> new RuntimeException("Depreciation schedule not found"));

            schedule.setPeriodStartDate(updatedData.getPeriodStartDate());
            schedule.setPeriodEndDate(updatedData.getPeriodEndDate());
            schedule.setDepreciationAmount(updatedData.getDepreciationAmount());
            schedule.setAccumulatedDepreciation(updatedData.getAccumulatedDepreciation());
            schedule.setNetBookValue(updatedData.getNetBookValue());
            schedule.setPosted(updatedData.getPosted() != null ? updatedData.getPosted() : false);
            schedule.setPostingDocumentNo(updatedData.getPostingDocumentNo());
            schedule.setPostingDate(updatedData.getPostingDate());

            return ResponseEntity.ok(scheduleRepository.save(schedule));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Depreciation schedule update failed: " + e.getMessage());
        }
    }

    @GetMapping("/organization/{organization}")
    public List<AssetDepreciationScheduleModel> getByOrganization(
            @PathVariable String organization) {
        return scheduleRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/asset/{assetCode}/book/{bookCode}")
    public List<AssetDepreciationScheduleModel> getByAssetAndBook(
            @PathVariable String organization,
            @PathVariable String assetCode,
            @PathVariable String bookCode) {

        return scheduleRepository.findByAssetCodeAndBookCodeAndOrganization(
                assetCode,
                bookCode,
                organization
        );
    }

    @GetMapping("/test")
    public String test() {
        return "Asset Depreciation Schedule API is working";
    }
}
