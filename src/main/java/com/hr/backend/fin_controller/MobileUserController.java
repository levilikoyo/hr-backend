package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.MobileUserModel;
import com.hr.backend.fin_repository.MobileUserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mobile-users")
@CrossOrigin(origins = "*")
public class MobileUserController {

    private final MobileUserRepository mobileUserRepository;

    public MobileUserController(MobileUserRepository mobileUserRepository) {
        this.mobileUserRepository = mobileUserRepository;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Mobile User API is working");
    }

    @GetMapping
    public ResponseEntity<List<MobileUserModel>> getAllMobileUsers() {
        return ResponseEntity.ok(mobileUserRepository.findAll());
    }

    @GetMapping("/organization/{organization}")
    public ResponseEntity<List<MobileUserModel>> getMobileUsersByOrganization(
            @PathVariable String organization
    ) {
        return ResponseEntity.ok(
                mobileUserRepository.findByOrganizationOrderByFullNameAsc(cleanText(organization))
        );
    }

    @GetMapping("/organization/{organization}/email/{email}")
    public ResponseEntity<?> getMobileUserByEmail(
            @PathVariable String organization,
            @PathVariable String email
    ) {
        Optional<MobileUserModel> optionalUser =
                mobileUserRepository.findByOrganizationAndEmailIgnoreCase(
                        cleanText(organization),
                        cleanEmail(email)
                );

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Mobile user not found"));
        }

        return ResponseEntity.ok(optionalUser.get());
    }

    @PostMapping
    public ResponseEntity<?> createMobileUser(@RequestBody MobileUserModel mobileUser) {
        String validationError = validateMobileUser(mobileUser);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validationError));
        }

        normalizeMobileUser(mobileUser);

        boolean exists = mobileUserRepository.existsByOrganizationAndEmailIgnoreCase(
                mobileUser.getOrganization(),
                mobileUser.getEmail()
        );

        if (exists) {
            return ResponseEntity.status(409).body(
                    Map.of("message", "Mobile user email already exists for this organization")
            );
        }

        return ResponseEntity.ok(mobileUserRepository.save(mobileUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMobileUserById(
            @PathVariable Long id,
            @RequestBody MobileUserModel incoming
    ) {
        Optional<MobileUserModel> optionalUser = mobileUserRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Mobile user not found"));
        }

        MobileUserModel existing = optionalUser.get();
        copyEditableFields(existing, incoming);
        return ResponseEntity.ok(mobileUserRepository.save(existing));
    }

    @PutMapping("/organization/{organization}/email/{email}")
    public ResponseEntity<?> updateMobileUserByEmail(
            @PathVariable String organization,
            @PathVariable String email,
            @RequestBody MobileUserModel incoming
    ) {
        Optional<MobileUserModel> optionalUser =
                mobileUserRepository.findByOrganizationAndEmailIgnoreCase(
                        cleanText(organization),
                        cleanEmail(email)
                );

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Mobile user not found"));
        }

        MobileUserModel existing = optionalUser.get();
        copyEditableFields(existing, incoming);
        return ResponseEntity.ok(mobileUserRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMobileUserById(@PathVariable Long id) {
        if (!mobileUserRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Mobile user not found"));
        }

        mobileUserRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Mobile user deleted successfully"));
    }

    @Transactional
    @DeleteMapping("/organization/{organization}/email/{email}")
    public ResponseEntity<?> deleteMobileUserByEmail(
            @PathVariable String organization,
            @PathVariable String email
    ) {
        String cleanOrganization = cleanText(organization);
        String cleanEmail = cleanEmail(email);

        boolean exists = mobileUserRepository.existsByOrganizationAndEmailIgnoreCase(
                cleanOrganization,
                cleanEmail
        );

        if (!exists) {
            return ResponseEntity.status(404).body(Map.of("message", "Mobile user not found"));
        }

        mobileUserRepository.deleteByOrganizationAndEmailIgnoreCase(cleanOrganization, cleanEmail);
        return ResponseEntity.ok(Map.of("message", "Mobile user deleted successfully"));
    }

    private void copyEditableFields(MobileUserModel existing, MobileUserModel incoming) {
        if (incoming.getOrganization() != null && !incoming.getOrganization().trim().isEmpty()) {
            existing.setOrganization(cleanText(incoming.getOrganization()));
        }
        if (incoming.getFullName() != null && !incoming.getFullName().trim().isEmpty()) {
            existing.setFullName(cleanText(incoming.getFullName()));
        }
        if (incoming.getEmail() != null && !incoming.getEmail().trim().isEmpty()) {
            existing.setEmail(cleanEmail(incoming.getEmail()));
        }
        existing.setPhone(cleanText(incoming.getPhone()));
        existing.setPinCode(cleanText(incoming.getPinCode()));
        existing.setUserRole(cleanText(incoming.getUserRole()));
        existing.setDepartment(cleanText(incoming.getDepartment()));
        existing.setBlocked(Boolean.TRUE.equals(incoming.getBlocked()));
        existing.setActive(!Boolean.TRUE.equals(incoming.getBlocked()));
        existing.setStatus(cleanStatus(incoming.getStatus(), existing.getActive()));
        existing.setUpdatedBy(cleanText(incoming.getUpdatedBy()));
    }

    private void normalizeMobileUser(MobileUserModel mobileUser) {
        mobileUser.setOrganization(cleanText(mobileUser.getOrganization()));
        mobileUser.setFullName(cleanText(mobileUser.getFullName()));
        mobileUser.setEmail(cleanEmail(mobileUser.getEmail()));
        mobileUser.setPhone(cleanText(mobileUser.getPhone()));
        mobileUser.setPinCode(cleanText(mobileUser.getPinCode()));
        mobileUser.setUserRole(cleanText(mobileUser.getUserRole()));
        mobileUser.setDepartment(cleanText(mobileUser.getDepartment()));
        mobileUser.setBlocked(Boolean.TRUE.equals(mobileUser.getBlocked()));
        mobileUser.setActive(!Boolean.TRUE.equals(mobileUser.getBlocked()));
        mobileUser.setStatus(cleanStatus(mobileUser.getStatus(), mobileUser.getActive()));
    }

    private String validateMobileUser(MobileUserModel mobileUser) {
        if (mobileUser == null) {
            return "Mobile user body is required";
        }
        if (isBlank(mobileUser.getOrganization())) {
            return "Organization is required";
        }
        if (isBlank(mobileUser.getFullName())) {
            return "Full name is required";
        }
        if (isBlank(mobileUser.getEmail())) {
            return "Email is required";
        }
        if (isBlank(mobileUser.getUserRole())) {
            return "Role is required";
        }
        if (isBlank(mobileUser.getDepartment())) {
            return "Department is required";
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanEmail(String value) {
        return cleanText(value).toLowerCase();
    }

    private String cleanStatus(String status, Boolean active) {
        String cleanStatus = cleanText(status).toUpperCase();
        if (!cleanStatus.isEmpty()) {
            return cleanStatus;
        }
        return Boolean.TRUE.equals(active) ? "ACTIVE" : "BLOCKED";
    }
}
