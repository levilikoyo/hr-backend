/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */
import com.hr.backend.fin_model.AssetBookModel;
import com.hr.backend.fin_repository.AssetBookRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset-books")
@CrossOrigin(origins = "*")
public class AssetBookController {

    @Autowired
    private AssetBookRepository assetBookRepository;

    @PostMapping
    public ResponseEntity<?> saveAssetBook(@RequestBody AssetBookModel assetBook) {
        try {
            if (assetBook.getOrganization() == null || assetBook.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (assetBook.getAssetCode() == null || assetBook.getAssetCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Asset code is required");
            }

            if (assetBook.getBookCode() == null || assetBook.getBookCode().trim().isEmpty()) {
                assetBook.setBookCode("MAIN");
            }

            boolean exists = assetBookRepository.existsByAssetCodeAndBookCodeAndOrganization(
                    assetBook.getAssetCode(),
                    assetBook.getBookCode(),
                    assetBook.getOrganization()
            );

            if (exists) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Asset book already exists");
            }

            calculateNetBookValue(assetBook);

            AssetBookModel saved = assetBookRepository.save(assetBook);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Asset book save failed: " + e.getMessage());
        }
    }

    @GetMapping
    public List<AssetBookModel> getAllAssetBooks() {
        return assetBookRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<AssetBookModel> getAssetBooksByOrganization(@PathVariable String organization) {
        return assetBookRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/asset/{assetCode}")
    public List<AssetBookModel> getAssetBooksByAsset(
            @PathVariable String organization,
            @PathVariable String assetCode) {

        return assetBookRepository.findByAssetCodeAndOrganization(assetCode, organization);
    }

    @PutMapping("/asset-book-info")
    public ResponseEntity<?> updateAssetBook(@RequestBody AssetBookModel updatedData) {
        try {
            if (updatedData.getOrganization() == null || updatedData.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (updatedData.getAssetCode() == null || updatedData.getAssetCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Asset code is required");
            }

            if (updatedData.getBookCode() == null || updatedData.getBookCode().trim().isEmpty()) {
                updatedData.setBookCode("MAIN");
            }

            AssetBookModel book = assetBookRepository
                    .findByAssetCodeAndBookCodeAndOrganization(
                            updatedData.getAssetCode(),
                            updatedData.getBookCode(),
                            updatedData.getOrganization()
                    )
                    .orElseThrow(() -> new RuntimeException("Asset book not found"));

            book.setAcquisitionDate(updatedData.getAcquisitionDate());
            book.setAcquisitionCost(updatedData.getAcquisitionCost());
            book.setUsefulLifeMonths(updatedData.getUsefulLifeMonths());
            book.setSalvageValue(updatedData.getSalvageValue());
            book.setAccumulatedDepreciation(updatedData.getAccumulatedDepreciation());
            book.setDepreciationMethod(updatedData.getDepreciationMethod());
            book.setDepreciationStartingDate(updatedData.getDepreciationStartingDate());
            book.setStatus(updatedData.getStatus());

            calculateNetBookValue(book);

            return ResponseEntity.ok(assetBookRepository.save(book));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Asset book update failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Asset Book API is working";
    }

    private void calculateNetBookValue(AssetBookModel book) {
        BigDecimal cost = book.getAcquisitionCost() == null
                ? BigDecimal.ZERO
                : book.getAcquisitionCost();

        BigDecimal accumulatedDep = book.getAccumulatedDepreciation() == null
                ? BigDecimal.ZERO
                : book.getAccumulatedDepreciation();

        book.setNetBookValue(cost.subtract(accumulatedDep));
    }
}