package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.UserModulePermissionModel;
import com.hr.backend.fin_repository.UserModulePermissionRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-module-permissions")
@CrossOrigin(origins = "*")
public class UserModulePermissionController {

    private final UserModulePermissionRepository repository;

    public UserModulePermissionController(UserModulePermissionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<UserModulePermissionModel>> getByUser(@PathVariable String username) {
        return ResponseEntity.ok(repository.findByUsernameIgnoreCaseOrderByMenuCodeAscModuleCodeAsc(cleanLower(username)));
    }

    @GetMapping("/user/{username}/organization/{organizationCode}")
    public ResponseEntity<List<UserModulePermissionModel>> getByUserAndOrganization(
            @PathVariable String username,
            @PathVariable String organizationCode
    ) {
        return ResponseEntity.ok(repository.findByUsernameIgnoreCaseAndOrganizationCodeIgnoreCaseOrderByMenuCodeAscModuleCodeAsc(
                cleanLower(username),
                cleanUpper(organizationCode)
        ));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody UserModulePermissionModel permission) {
        String validationError = validate(permission);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validationError));
        }
        Optional<UserModulePermissionModel> existing =
                repository.findByUsernameIgnoreCaseAndOrganizationCodeIgnoreCaseAndModuleCodeIgnoreCase(
                        permission.getUsername(),
                        permission.getOrganizationCode(),
                        permission.getModuleCode()
                );
        if (existing.isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "Module permission already exists for this user and organization"));
        }
        return ResponseEntity.ok(repository.save(permission));
    }

    @PutMapping("/user/{username}/organization/{organizationCode}/module/{moduleCode}")
    public ResponseEntity<?> update(
            @PathVariable String username,
            @PathVariable String organizationCode,
            @PathVariable String moduleCode,
            @RequestBody UserModulePermissionModel incoming
    ) {
        Optional<UserModulePermissionModel> existing =
                repository.findByUsernameIgnoreCaseAndOrganizationCodeIgnoreCaseAndModuleCodeIgnoreCase(
                        cleanLower(username),
                        cleanUpper(organizationCode),
                        cleanUpper(moduleCode)
                );
        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Module permission not found"));
        }
        UserModulePermissionModel permission = existing.get();
        permission.setMenuCode(incoming.getMenuCode());
        permission.setModuleName(incoming.getModuleName());
        permission.setAllowed(Boolean.TRUE.equals(incoming.getAllowed()));
        permission.setStatus(Boolean.TRUE.equals(incoming.getAllowed()) ? "ACTIVE" : "INACTIVE");
        permission.setUpdatedBy(incoming.getUpdatedBy());
        return ResponseEntity.ok(repository.save(permission));
    }

    private String validate(UserModulePermissionModel permission) {
        if (permission == null) {
            return "Permission body is required";
        }
        if (isBlank(permission.getUsername())) {
            return "Username is required";
        }
        if (isBlank(permission.getOrganizationCode())) {
            return "Organization is required";
        }
        if (isBlank(permission.getMenuCode())) {
            return "Menu is required";
        }
        if (isBlank(permission.getModuleCode())) {
            return "Module is required";
        }
        return null;
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private String cleanLower(String value) { return value == null ? "" : value.trim().toLowerCase(); }
    private String cleanUpper(String value) { return value == null ? "" : value.trim().toUpperCase(); }
}
