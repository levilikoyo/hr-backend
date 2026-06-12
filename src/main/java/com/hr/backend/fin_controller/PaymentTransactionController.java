package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.OrganizationModel;
import com.hr.backend.fin_model.PaymentTransactionModel;
import com.hr.backend.fin_repository.OrganizationRepository;
import com.hr.backend.fin_repository.PaymentTransactionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-transactions")
@CrossOrigin(origins = "*")
public class PaymentTransactionController {

    private final PaymentTransactionRepository paymentRepository;
    private final OrganizationRepository organizationRepository;

    public PaymentTransactionController(
            PaymentTransactionRepository paymentRepository,
            OrganizationRepository organizationRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.organizationRepository = organizationRepository;
    }

    @GetMapping
    public ResponseEntity<List<PaymentTransactionModel>> getAll() {
        return ResponseEntity.ok(paymentRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/organization/{organizationCode}")
    public ResponseEntity<List<PaymentTransactionModel>> getByOrganization(@PathVariable String organizationCode) {
        return ResponseEntity.ok(paymentRepository.findByOrganizationCodeIgnoreCaseOrderByCreatedAtDesc(cleanUpper(organizationCode)));
    }

    @PostMapping
    public ResponseEntity<?> recordPayment(@RequestBody PaymentTransactionModel payment) {
        String validationError = validate(payment);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validationError));
        }

        String organizationCode = cleanUpper(payment.getOrganizationCode());
        Optional<OrganizationModel> optionalOrganization = organizationRepository.findByCodeIgnoreCase(organizationCode);
        if (optionalOrganization.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Organization not found"));
        }

        OrganizationModel organization = optionalOrganization.get();
        payment.setOrganizationCode(organizationCode);
        payment.setOrganizationName(cleanText(organization.getName()));
        payment.setBillingPeriod(defaultUpper(payment.getBillingPeriod(), "MONTHLY"));
        payment.setBillingStatus(defaultUpper(payment.getBillingStatus(), "PAID"));
        payment.setCurrency(defaultUpper(payment.getCurrency(), defaultUpper(organization.getBaseCurrency(), "USD")));
        payment.setProvider(defaultUpper(payment.getProvider(), "MANUAL"));
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }

        PaymentTransactionModel saved = paymentRepository.save(payment);
        applyPaymentToOrganization(organization, saved);
        organizationRepository.save(organization);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/organization/{organizationCode}/block")
    public ResponseEntity<?> blockOrganization(@PathVariable String organizationCode, @RequestBody Map<String, String> body) {
        return updateBillingStatus(organizationCode, "BLOCKED", body);
    }

    @PostMapping("/organization/{organizationCode}/unblock")
    public ResponseEntity<?> unblockOrganization(@PathVariable String organizationCode, @RequestBody Map<String, String> body) {
        return updateBillingStatus(organizationCode, "PAID", body);
    }

    private ResponseEntity<?> updateBillingStatus(String organizationCode, String status, Map<String, String> body) {
        Optional<OrganizationModel> optionalOrganization = organizationRepository.findByCodeIgnoreCase(cleanUpper(organizationCode));
        if (optionalOrganization.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Organization not found"));
        }
        OrganizationModel organization = optionalOrganization.get();
        organization.setBillingStatus(status);
        if ("PAID".equals(status) && organization.getPaidThrough() == null) {
            organization.setPaidThrough(LocalDate.now().plusMonths(1));
        }
        organization.setUpdatedBy(cleanText(body == null ? "" : body.get("updatedBy")));
        return ResponseEntity.ok(organizationRepository.save(organization));
    }

    private void applyPaymentToOrganization(OrganizationModel organization, PaymentTransactionModel payment) {
        organization.setSubscriptionPlan(defaultUpper(organization.getSubscriptionPlan(), "STANDARD"));
        organization.setBillingPeriod(defaultUpper(payment.getBillingPeriod(), "MONTHLY"));
        organization.setBillingStatus(defaultUpper(payment.getBillingStatus(), "PAID"));
        organization.setPaidThrough(payment.getPaidThrough());
        organization.setGraceUntil(payment.getGraceUntil());
        organization.setPaymentProvider(defaultUpper(payment.getProvider(), "MANUAL"));
        organization.setPaymentCustomerId(cleanText(payment.getProviderReference()));
        organization.setStatus("ACTIVE");
        organization.setUpdatedBy(cleanText(payment.getCreatedBy()));
    }

    private String validate(PaymentTransactionModel payment) {
        if (payment == null) {
            return "Payment body is required";
        }
        if (isBlank(payment.getOrganizationCode())) {
            return "Organization is required";
        }
        if (payment.getAmount() == null) {
            return "Amount is required";
        }
        if (payment.getPaidThrough() == null) {
            return "Paid through date is required";
        }
        return null;
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private String cleanText(String value) { return value == null ? "" : value.trim(); }
    private String cleanUpper(String value) { return cleanText(value).toUpperCase(); }
    private String defaultUpper(String value, String fallback) {
        String clean = cleanUpper(value);
        return clean.isEmpty() ? fallback : clean;
    }
}
