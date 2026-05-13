/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.VendorModel;
import com.hr.backend.fin_repository.VendorRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "*")
public class VendorController {

    @Autowired
    private VendorRepository vendorRepository;

    @PostMapping
    public ResponseEntity<?> saveVendor(@RequestBody VendorModel vendor) {
        try {
            if (vendor.getOrganization() == null || vendor.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (vendor.getVendorCode() == null || vendor.getVendorCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Vendor code is required");
            }

            if (vendorRepository.existsByVendorCodeAndOrganization(
                    vendor.getVendorCode(),
                    vendor.getOrganization())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Vendor already exists for this organization");
            }

            if (vendor.getBlocked() == null) {
                vendor.setBlocked(false);
            }

            if (vendor.getStatus() == null || vendor.getStatus().trim().isEmpty()) {
                vendor.setStatus("Active");
            }

            VendorModel saved = vendorRepository.save(vendor);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Vendor save failed: " + e.getMessage());
        }
    }

    @GetMapping
    public List<VendorModel> getAllVendors() {
        return vendorRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<VendorModel> getVendorsByOrganization(@PathVariable String organization) {
        return vendorRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/active")
    public List<VendorModel> getActiveVendors(@PathVariable String organization) {
        return vendorRepository.findByOrganizationAndBlocked(organization, false);
    }

    @GetMapping("/organization/{organization}/blocked")
    public List<VendorModel> getBlockedVendors(@PathVariable String organization) {
        return vendorRepository.findByOrganizationAndBlocked(organization, true);
    }

    @GetMapping("/organization/{organization}/status/{status}")
    public List<VendorModel> getVendorsByStatus(
            @PathVariable String organization,
            @PathVariable String status) {
        return vendorRepository.findByOrganizationAndStatus(organization, status);
    }

    @GetMapping("/organization/{organization}/invoicing/{invoicing}")
    public List<VendorModel> getVendorsByInvoicing(
            @PathVariable String organization,
            @PathVariable String invoicing) {
        return vendorRepository.findByOrganizationAndInvoicing(organization, invoicing);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Long id) {
        return vendorRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Vendor not found"));
    }

    @PutMapping("/vendor-info")
    public ResponseEntity<?> updateVendorInfo(@RequestBody VendorModel updatedData) {
        try {
            if (updatedData.getOrganization() == null || updatedData.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (updatedData.getVendorCode() == null || updatedData.getVendorCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Vendor code is required");
            }

            VendorModel vendor = vendorRepository
                    .findByVendorCodeAndOrganization(
                            updatedData.getVendorCode(),
                            updatedData.getOrganization()
                    )
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            vendor.setVendorName(updatedData.getVendorName());
            vendor.setVendorAddress(updatedData.getVendorAddress());
            vendor.setVendorCity(updatedData.getVendorCity());
            vendor.setVendorCountry(updatedData.getVendorCountry());
            vendor.setTaxNo(updatedData.getTaxNo());

            vendor.setVendorPhone(updatedData.getVendorPhone());
            vendor.setVendorMail(updatedData.getVendorMail());
            vendor.setInvoicing(updatedData.getInvoicing());
            vendor.setCurrency(updatedData.getCurrency());
            vendor.setPaymentTerms(updatedData.getPaymentTerms());
            vendor.setPaymentMethod(updatedData.getPaymentMethod());

            vendor.setStartingDate(updatedData.getStartingDate());
            vendor.setClosingDate(updatedData.getClosingDate());
            vendor.setBlocked(updatedData.getBlocked() != null ? updatedData.getBlocked() : false);
            vendor.setStatus(updatedData.getStatus());
            vendor.setDescription(updatedData.getDescription());

            return ResponseEntity.ok(vendorRepository.save(vendor));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Vendor update failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable Long id) {
        try {
            if (!vendorRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Vendor not found");
            }

            vendorRepository.deleteById(id);
            return ResponseEntity.ok("Vendor deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Vendor delete failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/organization/{organization}/vendor-code/{vendorCode}")
    public ResponseEntity<?> deleteVendorByCode(
            @PathVariable String organization,
            @PathVariable String vendorCode) {
        try {
            VendorModel vendor = vendorRepository
                    .findByVendorCodeAndOrganization(vendorCode, organization)
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            vendorRepository.delete(vendor);
            return ResponseEntity.ok("Vendor deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Vendor delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Vendor API is working";
    }
    
    
}
