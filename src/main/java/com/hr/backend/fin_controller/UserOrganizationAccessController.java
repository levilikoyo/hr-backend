package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.UserOrganizationAccessModel;
import com.hr.backend.fin_repository.OrganizationRepository;
import com.hr.backend.fin_repository.SystemUserRepository;
import com.hr.backend.fin_repository.UserOrganizationAccessRepository;
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
@RequestMapping("/api/user-organization-access")
@CrossOrigin(origins = "*")
public class UserOrganizationAccessController {

    private final UserOrganizationAccessRepository accessRepository;
    private final SystemUserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public UserOrganizationAccessController(
            UserOrganizationAccessRepository accessRepository,
            SystemUserRepository userRepository,
            OrganizationRepository organizationRepository
    ) {
        this.accessRepository = accessRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<UserOrganizationAccessModel>> getAccessForUser(@PathVariable String username) {
        return ResponseEntity.ok(
                accessRepository.findByUsernameIgnoreCaseOrderByDefaultOrganizationDescOrganizationCodeAsc(cleanLower(username))
        );
    }

    @PostMapping
    public ResponseEntity<?> grantAccess(@RequestBody UserOrganizationAccessModel access) {
        String validationError = validateAccess(access);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validationError));
        }
        normalizeAccess(access);
        if (accessRepository.existsByUsernameIgnoreCaseAndOrganizationCodeIgnoreCase(
                access.getUsername(),
                access.getOrganizationCode()
        )) {
            return ResponseEntity.status(409).body(Map.of("message", "User already has access to this organization"));
        }
        return ResponseEntity.ok(accessRepository.save(access));
    }

    @PutMapping("/user/{username}/organization/{organizationCode}")
    public ResponseEntity<?> updateAccess(
            @PathVariable String username,
            @PathVariable String organizationCode,
            @RequestBody UserOrganizationAccessModel incoming
    ) {
        Optional<UserOrganizationAccessModel> optionalAccess =
                accessRepository.findByUsernameIgnoreCaseAndOrganizationCodeIgnoreCase(
                        cleanLower(username),
                        cleanUpper(organizationCode)
                );
        if (optionalAccess.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Organization access not found"));
        }
        UserOrganizationAccessModel existing = optionalAccess.get();
        existing.setRoleInOrganization(cleanUpper(incoming.getRoleInOrganization()));
        existing.setDefaultOrganization(Boolean.TRUE.equals(incoming.getDefaultOrganization()));
        existing.setStatus(cleanUpper(incoming.getStatus()));
        return ResponseEntity.ok(accessRepository.save(existing));
    }

    private String validateAccess(UserOrganizationAccessModel access) {
        if (access == null) {
            return "Access body is required";
        }
        if (isBlank(access.getUsername())) {
            return "Username is required";
        }
        if (isBlank(access.getOrganizationCode())) {
            return "Organization code is required";
        }
        if (!userRepository.existsByUsernameIgnoreCase(cleanLower(access.getUsername()))) {
            return "User does not exist";
        }
        if (!organizationRepository.existsByCodeIgnoreCase(cleanUpper(access.getOrganizationCode()))) {
            return "Organization does not exist";
        }
        return null;
    }

    private void normalizeAccess(UserOrganizationAccessModel access) {
        access.setUsername(cleanLower(access.getUsername()));
        access.setOrganizationCode(cleanUpper(access.getOrganizationCode()));
        access.setRoleInOrganization(cleanUpper(access.getRoleInOrganization()));
        access.setStatus(cleanUpper(access.getStatus()));
        access.setDefaultOrganization(Boolean.TRUE.equals(access.getDefaultOrganization()));
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private String cleanText(String value) { return value == null ? "" : value.trim(); }
    private String cleanLower(String value) { return cleanText(value).toLowerCase(); }
    private String cleanUpper(String value) { return cleanText(value).toUpperCase(); }
}
