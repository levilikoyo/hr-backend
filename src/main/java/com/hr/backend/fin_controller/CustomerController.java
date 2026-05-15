/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.CustomerModel;
import com.hr.backend.fin_repository.CustomerRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<?> saveCustomer(@RequestBody CustomerModel customer) {
        try {
            if (customer.getOrganization() == null || customer.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Customer code is required");
            }

            boolean exists = customerRepository.existsByCustomerCodeAndOrganization(
                    customer.getCustomerCode(),
                    customer.getOrganization()
            );

            if (exists) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Customer already exists");
            }

            CustomerModel saved = customerRepository.save(customer);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Customer save failed: " + e.getMessage());
        }
    }

    @GetMapping
    public List<CustomerModel> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/organization/{organization}")
    public List<CustomerModel> getCustomersByOrganization(@PathVariable String organization) {
        return customerRepository.findByOrganization(organization);
    }

    @PutMapping("/customer-info")
public ResponseEntity<?> updateCustomerInfo(@RequestBody CustomerModel updatedData) {
    try {
        if (updatedData.getOrganization() == null || updatedData.getOrganization().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Organization is required");
        }

        if (updatedData.getCustomerCode() == null || updatedData.getCustomerCode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Customer code is required");
        }

        CustomerModel customer = customerRepository
                .findByCustomerCodeAndOrganization(
                        updatedData.getCustomerCode(),
                        updatedData.getOrganization()
                )
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found for code: "
                        + updatedData.getCustomerCode()
                        + " and organization: "
                        + updatedData.getOrganization()
                ));

        // General infos
        customer.setCustomerName(updatedData.getCustomerName());
        customer.setCustomerAddress(updatedData.getCustomerAddress());
        customer.setCustomerCity(updatedData.getCustomerCity());

        // Other infos
        customer.setCustomerPhone(updatedData.getCustomerPhone());
        customer.setCustomerMail(updatedData.getCustomerMail());
        customer.setInvoicing(updatedData.getInvoicing());

        // New fields you requested
        customer.setStartingDate(updatedData.getStartingDate());
        customer.setClosingDate(updatedData.getClosingDate());
        customer.setBlocked(updatedData.getBlocked() != null ? updatedData.getBlocked() : false);
        customer.setDescription(updatedData.getDescription());

        // Optional status
        customer.setStatus(updatedData.getStatus());

        return ResponseEntity.ok(customerRepository.save(customer));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Customer update failed: " + e.getMessage());
    }
}

    @DeleteMapping("/organization/{organization}/code/{customerCode}")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable String organization,
            @PathVariable String customerCode) {
        try {
            CustomerModel customer = customerRepository
                    .findByCustomerCodeAndOrganization(customerCode, organization)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            customerRepository.delete(customer);

            return ResponseEntity.ok("Customer deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Customer delete failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Customer API is working";
    }
}
