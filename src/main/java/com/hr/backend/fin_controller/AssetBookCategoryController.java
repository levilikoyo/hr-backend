/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.AssetBookCategoryModel;
import com.hr.backend.fin_repository.AssetBookCategoryRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset-book-categories")
@CrossOrigin(origins = "*")
public class AssetBookCategoryController {

    @Autowired
    private AssetBookCategoryRepository assetBookCategoryRepository;

    @PostMapping
    public ResponseEntity<?> saveAssetBookCategory(@RequestBody AssetBookCategoryModel category) {
        try {
            if (category.getOrganization() == null || category.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (category.getBookCode() == null || category.getBookCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Book code is required");
            }

            boolean exists = assetBookCategoryRepository.existsByBookCodeAndOrganization(
                    category.getBookCode(),
                    category.getOrganization()
            );

            if (exists) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Asset book category already exists");
            }

            if (category.getStatus() == null || category.getStatus().trim().isEmpty()) {
                category.setStatus("Active");
            }

            if (category.getCreatedDate() == null || category.getCreatedDate().trim().isEmpty()) {
                category.setCreatedDate(todayDate());
            }

            AssetBookCategoryModel saved = assetBookCategoryRepository.save(category);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Asset book category save failed: " + e.getMessage());
        }
    }

    @GetMapping
    public List<AssetBookCategoryModel> getAllAssetBookCategories() {
        return assetBookCategoryRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<AssetBookCategoryModel> getAssetBookCategoriesByOrganization(
            @PathVariable String organization) {

        return assetBookCategoryRepository.findByOrganization(organization);
    }

    @PutMapping("/category-info")
    public ResponseEntity<?> updateAssetBookCategory(@RequestBody AssetBookCategoryModel updatedData) {
        try {
            if (updatedData.getOrganization() == null || updatedData.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (updatedData.getBookCode() == null || updatedData.getBookCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Book code is required");
            }

            AssetBookCategoryModel category = assetBookCategoryRepository
                    .findByBookCodeAndOrganization(
                            updatedData.getBookCode(),
                            updatedData.getOrganization()
                    )
                    .orElseThrow(() -> new RuntimeException(
                            "Asset book category not found for code: "
                            + updatedData.getBookCode()
                            + " and organization: "
                            + updatedData.getOrganization()
                    ));

            category.setBookName(updatedData.getBookName());
            category.setStatus(
                    updatedData.getStatus() == null || updatedData.getStatus().trim().isEmpty()
                            ? "Active"
                            : updatedData.getStatus()
            );

            return ResponseEntity.ok(assetBookCategoryRepository.save(category));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Asset book category update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/organization/{organization}/book/{bookCode}")
    public ResponseEntity<?> deleteAssetBookCategory(
            @PathVariable String organization,
            @PathVariable String bookCode) {
        try {
            AssetBookCategoryModel category = assetBookCategoryRepository
                    .findByBookCodeAndOrganization(bookCode, organization)
                    .orElseThrow(() -> new RuntimeException("Asset book category not found"));

            assetBookCategoryRepository.delete(category);

            return ResponseEntity.ok("Asset book category deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Asset book category delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Asset Book Category API is working";
    }

    private String todayDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
