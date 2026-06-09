package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.SystemUserModel;
import com.hr.backend.fin_repository.SystemUserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
@RequestMapping("/api/system-users")
@CrossOrigin(origins = "*")
public class SystemUserController {

    private final SystemUserRepository userRepository;

    public SystemUserController(SystemUserRepository userRepository) {
        this.userRepository = userRepository;
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
        return ResponseEntity.ok(userRepository.save(user));
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
        return ResponseEntity.ok(userRepository.save(existing));
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
