/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.mobile_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.mobile_repository.DepartmentRepository;
import com.hr.backend.mobile_model.DepartmentModel;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Department API is working");
    }

    @GetMapping
    public ResponseEntity<List<DepartmentModel>> getAllDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @GetMapping("/organization/{organization}")
    public ResponseEntity<List<DepartmentModel>> getDepartmentsByOrganization(
            @PathVariable String organization
    ) {
        return ResponseEntity.ok(
                departmentRepository.findByOrganizationOrderByDepartmentCodeAsc(
                        cleanOrganization(organization)
                )
        );
    }

    @GetMapping("/organization/{organization}/active")
    public ResponseEntity<List<DepartmentModel>> getActiveDepartmentsByOrganization(
            @PathVariable String organization
    ) {
        return ResponseEntity.ok(
                departmentRepository.findByOrganizationAndStatusOrderByDepartmentCodeAsc(
                        cleanOrganization(organization),
                        "ACTIVE"
                )
        );
    }

    @GetMapping("/organization/{organization}/unblocked")
    public ResponseEntity<List<DepartmentModel>> getUnblockedDepartmentsByOrganization(
            @PathVariable String organization
    ) {
        return ResponseEntity.ok(
                departmentRepository.findByOrganizationAndBlockedFalseOrderByDepartmentCodeAsc(
                        cleanOrganization(organization)
                )
        );
    }

    @GetMapping("/organization/{organization}/code/{departmentCode}")
    public ResponseEntity<?> getDepartmentByCode(
            @PathVariable String organization,
            @PathVariable String departmentCode
    ) {
        Optional<DepartmentModel> optionalDepartment =
                departmentRepository.findByOrganizationAndDepartmentCode(
                        cleanOrganization(organization),
                        cleanCode(departmentCode)
                );

        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "Department not found")
            );
        }

        return ResponseEntity.ok(optionalDepartment.get());
    }

    @PostMapping
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentModel department) {
        String validationError = validateDepartment(department);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", validationError)
            );
        }

        department.setOrganization(cleanOrganization(department.getOrganization()));
        department.setDepartmentCode(cleanCode(department.getDepartmentCode()));
        department.setDepartmentName(cleanText(department.getDepartmentName()));

        if (department.getStatus() == null || department.getStatus().trim().isEmpty()) {
            department.setStatus("ACTIVE");
        } else {
            department.setStatus(cleanCode(department.getStatus()));
        }

        if (department.getBlocked() == null) {
            department.setBlocked(false);
        }

        boolean exists = departmentRepository.existsByOrganizationAndDepartmentCode(
                department.getOrganization(),
                department.getDepartmentCode()
        );

        if (exists) {
            return ResponseEntity.status(409).body(
                    Map.of("message", "Department code already exists for this organization")
            );
        }

        DepartmentModel saved = departmentRepository.save(department);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartmentById(
            @PathVariable Long id,
            @RequestBody DepartmentModel incoming
    ) {
        Optional<DepartmentModel> optionalDepartment = departmentRepository.findById(id);

        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "Department not found")
            );
        }

        DepartmentModel existing = optionalDepartment.get();

        if (incoming.getOrganization() != null && !incoming.getOrganization().trim().isEmpty()) {
            existing.setOrganization(cleanOrganization(incoming.getOrganization()));
        }

        if (incoming.getDepartmentCode() != null && !incoming.getDepartmentCode().trim().isEmpty()) {
            existing.setDepartmentCode(cleanCode(incoming.getDepartmentCode()));
        }

        if (incoming.getDepartmentName() != null && !incoming.getDepartmentName().trim().isEmpty()) {
            existing.setDepartmentName(cleanText(incoming.getDepartmentName()));
        }

        existing.setDescription(incoming.getDescription());

        if (incoming.getBlocked() != null) {
            existing.setBlocked(incoming.getBlocked());
        }

        if (incoming.getStatus() != null && !incoming.getStatus().trim().isEmpty()) {
            existing.setStatus(cleanCode(incoming.getStatus()));
        }

        if (incoming.getUpdatedBy() != null) {
            existing.setUpdatedBy(incoming.getUpdatedBy());
        }

        DepartmentModel saved = departmentRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/organization/{organization}/code/{departmentCode}")
    public ResponseEntity<?> updateDepartmentByCode(
            @PathVariable String organization,
            @PathVariable String departmentCode,
            @RequestBody DepartmentModel incoming
    ) {
        Optional<DepartmentModel> optionalDepartment =
                departmentRepository.findByOrganizationAndDepartmentCode(
                        cleanOrganization(organization),
                        cleanCode(departmentCode)
                );

        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "Department not found")
            );
        }

        DepartmentModel existing = optionalDepartment.get();

        if (incoming.getDepartmentName() != null && !incoming.getDepartmentName().trim().isEmpty()) {
            existing.setDepartmentName(cleanText(incoming.getDepartmentName()));
        }

        existing.setDescription(incoming.getDescription());

        if (incoming.getBlocked() != null) {
            existing.setBlocked(incoming.getBlocked());
        }

        if (incoming.getStatus() != null && !incoming.getStatus().trim().isEmpty()) {
            existing.setStatus(cleanCode(incoming.getStatus()));
        }

        if (incoming.getUpdatedBy() != null) {
            existing.setUpdatedBy(incoming.getUpdatedBy());
        }

        DepartmentModel saved = departmentRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/organization/{organization}/code/{departmentCode}/block")
    public ResponseEntity<?> blockDepartment(
            @PathVariable String organization,
            @PathVariable String departmentCode
    ) {
        Optional<DepartmentModel> optionalDepartment =
                departmentRepository.findByOrganizationAndDepartmentCode(
                        cleanOrganization(organization),
                        cleanCode(departmentCode)
                );

        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "Department not found")
            );
        }

        DepartmentModel department = optionalDepartment.get();
        department.setBlocked(true);
        department.setStatus("BLOCKED");

        return ResponseEntity.ok(departmentRepository.save(department));
    }

    @PatchMapping("/organization/{organization}/code/{departmentCode}/unblock")
    public ResponseEntity<?> unblockDepartment(
            @PathVariable String organization,
            @PathVariable String departmentCode
    ) {
        Optional<DepartmentModel> optionalDepartment =
                departmentRepository.findByOrganizationAndDepartmentCode(
                        cleanOrganization(organization),
                        cleanCode(departmentCode)
                );

        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "Department not found")
            );
        }

        DepartmentModel department = optionalDepartment.get();
        department.setBlocked(false);
        department.setStatus("ACTIVE");

        return ResponseEntity.ok(departmentRepository.save(department));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartmentById(@PathVariable Long id) {
        if (!departmentRepository.existsById(id)) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "Department not found")
            );
        }

        departmentRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Department deleted successfully")
        );
    }

    @Transactional
    @DeleteMapping("/organization/{organization}/code/{departmentCode}")
    public ResponseEntity<?> deleteDepartmentByCode(
            @PathVariable String organization,
            @PathVariable String departmentCode
    ) {
        String cleanOrganization = cleanOrganization(organization);
        String cleanDepartmentCode = cleanCode(departmentCode);

        boolean exists = departmentRepository.existsByOrganizationAndDepartmentCode(
                cleanOrganization,
                cleanDepartmentCode
        );

        if (!exists) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "Department not found")
            );
        }

        departmentRepository.deleteByOrganizationAndDepartmentCode(
                cleanOrganization,
                cleanDepartmentCode
        );

        return ResponseEntity.ok(
                Map.of("message", "Department deleted successfully")
        );
    }

    private String validateDepartment(DepartmentModel department) {
        if (department == null) {
            return "Department body is required";
        }

        if (department.getOrganization() == null || department.getOrganization().trim().isEmpty()) {
            return "Organization is required";
        }

        if (department.getDepartmentCode() == null || department.getDepartmentCode().trim().isEmpty()) {
            return "Department code is required";
        }

        if (department.getDepartmentName() == null || department.getDepartmentName().trim().isEmpty()) {
            return "Department name is required";
        }

        return null;
    }

    private String cleanOrganization(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanCode(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }
}