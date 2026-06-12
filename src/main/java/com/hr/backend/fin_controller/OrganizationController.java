package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.OrganizationModel;
import com.hr.backend.fin_model.UserOrganizationAccessModel;
import com.hr.backend.fin_repository.OrganizationRepository;
import com.hr.backend.fin_repository.SystemUserRepository;
import com.hr.backend.fin_repository.UserOrganizationAccessRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*")
public class OrganizationController {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationAccessRepository accessRepository;
    private final SystemUserRepository userRepository;

    public OrganizationController(
            OrganizationRepository organizationRepository,
            UserOrganizationAccessRepository accessRepository,
            SystemUserRepository userRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.accessRepository = accessRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationModel>> getAllOrganizations() {
        return ResponseEntity.ok(organizationRepository.findAllByOrderByCodeAsc());
    }

    @GetMapping("/active")
    public ResponseEntity<List<OrganizationModel>> getActiveOrganizations() {
        return ResponseEntity.ok(organizationRepository.findByStatusIgnoreCaseOrderByCodeAsc("ACTIVE"));
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getOrganization(@PathVariable String code) {
        return organizationRepository.findByCodeIgnoreCase(cleanUpper(code))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Organization not found")));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<OrganizationModel>> getOrganizationsForUser(@PathVariable String username) {
        boolean admin = userRepository.findByUsernameIgnoreCase(cleanLower(username))
                .map(user -> "ADMIN".equalsIgnoreCase(cleanText(user.getGlobalRole())))
                .orElse(false);
        List<UserOrganizationAccessModel> accessRows =
                accessRepository.findByUsernameIgnoreCaseAndStatusIgnoreCaseOrderByDefaultOrganizationDescOrganizationCodeAsc(
                        cleanLower(username),
                        "ACTIVE"
                );

        if (accessRows.isEmpty()) {
            return ResponseEntity.ok(filterBillableOrganizations(
                    organizationRepository.findByStatusIgnoreCaseOrderByCodeAsc("ACTIVE"),
                    admin
            ));
        }

        Set<String> codes = new LinkedHashSet<>();
        for (UserOrganizationAccessModel access : accessRows) {
            codes.add(cleanUpper(access.getOrganizationCode()));
        }

        List<OrganizationModel> organizations = new ArrayList<>();
        for (String code : codes) {
            Optional<OrganizationModel> org = organizationRepository.findByCodeIgnoreCase(code);
            org.ifPresent(item -> {
                if (isBillableAccessAllowed(item, admin)) {
                    organizations.add(item);
                }
            });
        }
        return ResponseEntity.ok(organizations);
    }

    @PostMapping
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationModel organization) {
        String validationError = validateOrganization(organization);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validationError));
        }
        normalizeOrganization(organization);
        if (organizationRepository.existsByCodeIgnoreCase(organization.getCode())) {
            return ResponseEntity.status(409).body(Map.of("message", "Organization already exists"));
        }
        return ResponseEntity.ok(organizationRepository.save(organization));
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> updateOrganization(
            @PathVariable String code,
            @RequestBody OrganizationModel incoming
    ) {
        Optional<OrganizationModel> optionalOrg = organizationRepository.findByCodeIgnoreCase(cleanUpper(code));
        if (optionalOrg.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Organization not found"));
        }
        OrganizationModel existing = optionalOrg.get();
        copyEditableFields(existing, incoming);
        return ResponseEntity.ok(organizationRepository.save(existing));
    }

    @Transactional
    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteOrganization(@PathVariable String code) {
        String cleanCode = cleanUpper(code);
        if (!organizationRepository.existsByCodeIgnoreCase(cleanCode)) {
            return ResponseEntity.status(404).body(Map.of("message", "Organization not found"));
        }
        organizationRepository.deleteByCodeIgnoreCase(cleanCode);
        return ResponseEntity.ok(Map.of("message", "Organization deleted successfully"));
    }

    private void copyEditableFields(OrganizationModel existing, OrganizationModel incoming) {
        existing.setName(cleanText(incoming.getName()));
        existing.setLegalName(cleanText(incoming.getLegalName()));
        existing.setTaxNumber(cleanText(incoming.getTaxNumber()));
        existing.setRegistrationNumber(cleanText(incoming.getRegistrationNumber()));
        existing.setPhone(cleanText(incoming.getPhone()));
        existing.setEmail(cleanText(incoming.getEmail()));
        existing.setAddress(cleanText(incoming.getAddress()));
        existing.setCity(cleanText(incoming.getCity()));
        existing.setCountry(cleanText(incoming.getCountry()));
        existing.setBaseCurrency(cleanUpper(incoming.getBaseCurrency()));
        existing.setFiscalYearStart(cleanText(incoming.getFiscalYearStart()));
        existing.setFiscalYearEnd(cleanText(incoming.getFiscalYearEnd()));
        existing.setStatus(cleanUpper(incoming.getStatus()));
        existing.setSubscriptionPlan(cleanUpper(incoming.getSubscriptionPlan()));
        existing.setBillingPeriod(cleanUpper(incoming.getBillingPeriod()));
        existing.setBillingStatus(cleanUpper(incoming.getBillingStatus()));
        existing.setPaidThrough(incoming.getPaidThrough());
        existing.setGraceUntil(incoming.getGraceUntil());
        existing.setPaymentProvider(cleanUpper(incoming.getPaymentProvider()));
        existing.setPaymentCustomerId(cleanText(incoming.getPaymentCustomerId()));
        existing.setUpdatedBy(cleanText(incoming.getUpdatedBy()));
    }

    private void normalizeOrganization(OrganizationModel organization) {
        organization.setCode(cleanUpper(organization.getCode()));
        organization.setName(cleanText(organization.getName()));
        organization.setBaseCurrency(cleanUpper(organization.getBaseCurrency()));
        organization.setStatus(cleanUpper(organization.getStatus()));
        organization.setSubscriptionPlan(cleanUpper(organization.getSubscriptionPlan()));
        organization.setBillingPeriod(cleanUpper(organization.getBillingPeriod()));
        organization.setBillingStatus(cleanUpper(organization.getBillingStatus()));
        organization.setPaymentProvider(cleanUpper(organization.getPaymentProvider()));
        organization.setPaymentCustomerId(cleanText(organization.getPaymentCustomerId()));
    }

    private List<OrganizationModel> filterBillableOrganizations(List<OrganizationModel> organizations, boolean admin) {
        List<OrganizationModel> allowed = new ArrayList<>();
        for (OrganizationModel organization : organizations) {
            if (isBillableAccessAllowed(organization, admin)) {
                allowed.add(organization);
            }
        }
        return allowed;
    }

    private boolean isBillableAccessAllowed(OrganizationModel organization, boolean admin) {
        if (admin) {
            return true;
        }
        if (organization == null) {
            return false;
        }
        if (!"ACTIVE".equalsIgnoreCase(cleanText(organization.getStatus()))) {
            return false;
        }
        String billingStatus = cleanUpper(organization.getBillingStatus());
        if ("BLOCKED".equals(billingStatus) || "FAILED".equals(billingStatus) || "PAST_DUE".equals(billingStatus)) {
            return isDateTodayOrFuture(organization.getGraceUntil());
        }
        if ("TRIAL".equals(billingStatus)) {
            return isDateTodayOrFuture(organization.getGraceUntil()) || isDateTodayOrFuture(organization.getPaidThrough());
        }
        if ("PAID".equals(billingStatus) || "ACTIVE".equals(billingStatus)) {
            LocalDate paidThrough = organization.getPaidThrough();
            return paidThrough == null || isDateTodayOrFuture(paidThrough);
        }
        return billingStatus.isEmpty();
    }

    private boolean isDateTodayOrFuture(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    private String validateOrganization(OrganizationModel organization) {
        if (organization == null) {
            return "Organization body is required";
        }
        if (isBlank(organization.getCode())) {
            return "Organization code is required";
        }
        if (isBlank(organization.getName())) {
            return "Organization name is required";
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanUpper(String value) {
        return cleanText(value).toUpperCase();
    }

    private String cleanLower(String value) {
        return cleanText(value).toLowerCase();
    }
}
