package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.SystemUserModel;
import com.hr.backend.fin_model.UserOrganizationAccessModel;
import com.hr.backend.fin_repository.SystemUserRepository;
import com.hr.backend.fin_repository.UserOrganizationAccessRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping("/api/system-users")
@CrossOrigin(origins = "*")
public class SystemUserController {

    private final SystemUserRepository userRepository;
    private final UserOrganizationAccessRepository accessRepository;

    public SystemUserController(
            SystemUserRepository userRepository,
            UserOrganizationAccessRepository accessRepository
    ) {
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
    }

    @GetMapping
    public ResponseEntity<List<SystemUserModel>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAllByOrderByFullNameAsc());
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        return userRepository.findByUsernameIgnoreCase(cleanLower(username))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "User not found")));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody SystemUserModel user) {
        String validationError = validateUser(user, true);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validationError));
        }
        normalizeUser(user);
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            return ResponseEntity.status(409).body(Map.of("message", "Username already exists"));
        }
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            return ResponseEntity.status(409).body(Map.of("message", "Email already exists"));
        }
        user.setPasswordHash(hashPassword(user.getPasswordHash()));
        try {
            return ResponseEntity.ok(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "User data is too long or violates a database rule."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = cleanLower(request == null ? "" : request.get("username"));
        String password = cleanText(request == null ? "" : request.get("password"));
        if (isBlank(username) || isBlank(password)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required"));
        }

        Optional<SystemUserModel> optionalUser = userRepository.findByUsernameIgnoreCase(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password"));
        }

        SystemUserModel user = optionalUser.get();
        if (!hashPassword(password).equals(cleanText(user.getPasswordHash()))) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password"));
        }
        if (Boolean.TRUE.equals(user.getBlocked()) || "BLOCKED".equalsIgnoreCase(cleanText(user.getStatus()))) {
            return ResponseEntity.status(403).body(Map.of("message", "This user is blocked. Contact the administrator."));
        }
        if (!isBlank(user.getStatus()) && !"ACTIVE".equalsIgnoreCase(cleanText(user.getStatus()))) {
            return ResponseEntity.status(403).body(Map.of("message", "This user is not active. Contact the administrator."));
        }

        List<UserOrganizationAccessModel> accessRows =
                accessRepository.findByUsernameIgnoreCaseAndStatusIgnoreCaseOrderByDefaultOrganizationDescOrganizationCodeAsc(
                        username,
                        "ACTIVE"
                );
        List<String> organizations = new ArrayList<>();
        String defaultOrganization = "";
        for (UserOrganizationAccessModel access : accessRows) {
            String code = cleanUpper(access.getOrganizationCode());
            if (code.isEmpty()) {
                continue;
            }
            if (!organizations.contains(code)) {
                organizations.add(code);
            }
            if (defaultOrganization.isEmpty() || Boolean.TRUE.equals(access.getDefaultOrganization())) {
                defaultOrganization = code;
            }
        }

        return ResponseEntity.ok(Map.of(
                "username", cleanLower(user.getUsername()),
                "fullName", cleanText(user.getFullName()),
                "email", cleanLower(user.getEmail()),
                "phone", cleanText(user.getPhone()),
                "userType", cleanUpper(user.getUserType()),
                "globalRole", cleanUpper(user.getGlobalRole()),
                "status", cleanUpper(user.getStatus()),
                "defaultOrganization", defaultOrganization,
                "organizations", organizations
        ));
    }

    @PostMapping("/credentials")
    public ResponseEntity<?> updateCredentials(@RequestBody Map<String, String> request) {
        String currentUsername = cleanLower(request == null ? "" : request.get("currentUsername"));
        String newUsername = cleanLower(request == null ? "" : request.get("newUsername"));
        String password = cleanText(request == null ? "" : request.get("password"));
        String updatedBy = cleanText(request == null ? "" : request.get("updatedBy"));
        if (isBlank(currentUsername)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Current username is required"));
        }
        if (isBlank(newUsername) && isBlank(password)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Enter a new username or password"));
        }

        Optional<SystemUserModel> optionalUser = userRepository.findByUsernameIgnoreCase(currentUsername);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        SystemUserModel user = optionalUser.get();
        String finalUsername = isBlank(newUsername) ? currentUsername : newUsername;
        if (!finalUsername.equalsIgnoreCase(currentUsername)
                && userRepository.existsByUsernameIgnoreCase(finalUsername)) {
            return ResponseEntity.status(409).body(Map.of("message", "Username already exists"));
        }

        user.setUsername(finalUsername);
        if (!isBlank(password)) {
            user.setPasswordHash(hashPassword(password));
        }
        user.setUpdatedBy(updatedBy);

        List<UserOrganizationAccessModel> accessRows =
                accessRepository.findByUsernameIgnoreCaseOrderByDefaultOrganizationDescOrganizationCodeAsc(currentUsername);
        for (UserOrganizationAccessModel access : accessRows) {
            access.setUsername(finalUsername);
        }

        try {
            userRepository.save(user);
            accessRepository.saveAll(accessRows);
            return ResponseEntity.ok(Map.of(
                    "message", "Credentials updated successfully",
                    "username", finalUsername
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Credential data is too long or violates a database rule."));
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(
            @PathVariable String username,
            @RequestBody SystemUserModel incoming
    ) {
        Optional<SystemUserModel> optionalUser = userRepository.findByUsernameIgnoreCase(cleanLower(username));
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        SystemUserModel existing = optionalUser.get();
        existing.setFullName(cleanText(incoming.getFullName()));
        existing.setPhone(cleanText(incoming.getPhone()));
        existing.setUserType(cleanUpper(incoming.getUserType()));
        existing.setGlobalRole(cleanUpper(incoming.getGlobalRole()));
        existing.setBlocked(Boolean.TRUE.equals(incoming.getBlocked()));
        existing.setStatus(Boolean.TRUE.equals(incoming.getBlocked()) ? "BLOCKED" : cleanUpper(incoming.getStatus()));
        existing.setUpdatedBy(cleanText(incoming.getUpdatedBy()));
        if (!isBlank(incoming.getPasswordHash())) {
            existing.setPasswordHash(hashPassword(incoming.getPasswordHash()));
        }
        try {
            return ResponseEntity.ok(userRepository.save(existing));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "User data is too long or violates a database rule."));
        }
    }

    private String validateUser(SystemUserModel user, boolean passwordRequired) {
        if (user == null) {
            return "User body is required";
        }
        if (isBlank(user.getFullName())) {
            return "Full name is required";
        }
        if (isBlank(user.getUsername())) {
            return "Username is required";
        }
        if (isBlank(user.getEmail())) {
            return "Email is required";
        }
        if (passwordRequired && isBlank(user.getPasswordHash())) {
            return "Password is required";
        }
        if (cleanText(user.getFullName()).length() > 180) {
            return "Full name must be 180 characters or less";
        }
        if (cleanLower(user.getUsername()).length() > 120) {
            return "Username must be 120 characters or less";
        }
        if (cleanLower(user.getEmail()).length() > 320) {
            return "Email must be 320 characters or less";
        }
        if (!isBlank(user.getPhone()) && cleanText(user.getPhone()).length() > 60) {
            return "Phone must be 60 characters or less";
        }
        return null;
    }

    private void normalizeUser(SystemUserModel user) {
        user.setUsername(cleanLower(user.getUsername()));
        user.setEmail(cleanLower(user.getEmail()));
        user.setFullName(cleanText(user.getFullName()));
        user.setUserType(cleanUpper(user.getUserType()));
        user.setGlobalRole(cleanUpper(user.getGlobalRole()));
        user.setBlocked(Boolean.TRUE.equals(user.getBlocked()));
        user.setStatus(Boolean.TRUE.equals(user.getBlocked()) ? "BLOCKED" : cleanUpper(user.getStatus()));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(cleanText(password).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return cleanText(password);
        }
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private String cleanText(String value) { return value == null ? "" : value.trim(); }
    private String cleanLower(String value) { return cleanText(value).toLowerCase(); }
    private String cleanUpper(String value) { return cleanText(value).toUpperCase(); }
}
