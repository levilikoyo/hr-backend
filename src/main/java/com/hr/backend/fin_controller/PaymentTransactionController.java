package com.hr.backend.fin_controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hr.backend.fin_model.OrganizationModel;
import com.hr.backend.fin_model.MpesaPaymentRequest;
import com.hr.backend.fin_model.PaymentTransactionModel;
import com.hr.backend.fin_repository.OrganizationRepository;
import com.hr.backend.fin_repository.PaymentTransactionRepository;
import com.hr.backend.service.MpesaClientService;
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
    private final MpesaClientService mpesaClientService;
    private final Gson gson = new Gson();

    public PaymentTransactionController(
            PaymentTransactionRepository paymentRepository,
            OrganizationRepository organizationRepository,
            MpesaClientService mpesaClientService
    ) {
        this.paymentRepository = paymentRepository;
        this.organizationRepository = organizationRepository;
        this.mpesaClientService = mpesaClientService;
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

    @PostMapping("/mpesa/initiate")
    public ResponseEntity<?> initiateMpesaPayment(@RequestBody MpesaPaymentRequest request) {
        String validationError = validateMpesa(request);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validationError));
        }

        String organizationCode = cleanUpper(request.getOrganizationCode());
        Optional<OrganizationModel> optionalOrganization = organizationRepository.findByCodeIgnoreCase(organizationCode);
        if (optionalOrganization.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Organization not found"));
        }

        OrganizationModel organization = optionalOrganization.get();
        PaymentTransactionModel payment = new PaymentTransactionModel();
        payment.setOrganizationCode(organizationCode);
        payment.setOrganizationName(cleanText(organization.getName()));
        payment.setBillingPeriod(defaultUpper(request.getBillingPeriod(), "MONTHLY"));
        payment.setBillingStatus("PENDING");
        payment.setAmount(request.getAmount());
        payment.setCurrency(defaultUpper(request.getCurrency(), defaultUpper(organization.getBaseCurrency(), "USD")));
        payment.setProvider("M_PESA");
        payment.setProviderReference("MPESA-" + System.currentTimeMillis());
        payment.setPaymentDate(request.getPaymentDate() == null ? LocalDate.now() : request.getPaymentDate());
        payment.setPaidFrom(request.getPaidFrom());
        payment.setPaidThrough(request.getPaidThrough());
        payment.setGraceUntil(request.getGraceUntil());
        payment.setPayerPhone(cleanText(request.getPhoneNumber()));
        payment.setNotes(cleanText(request.getNotes()));
        payment.setCreatedBy(cleanText(request.getCreatedBy()));

        MpesaClientService.MpesaResponse response = mpesaClientService.requestPayment(
                request.getPhoneNumber(),
                request.getAmount(),
                organizationCode,
                "EMS-L subscription " + organizationCode
        );
        payment.setProviderStatus(response.isSuccess() ? "PENDING" : "FAILED");
        payment.setProviderCheckoutId(response.getCheckoutRequestId());
        payment.setProviderMessage(response.getMessage());
        PaymentTransactionModel saved = paymentRepository.save(payment);

        if (!response.isSuccess()) {
            return ResponseEntity.status(502).body(Map.of(
                    "message", response.getMessage(),
                    "paymentId", saved.getId()
            ));
        }
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/mpesa/callback")
    public ResponseEntity<?> mpesaCallback(@RequestBody Map<String, Object> callback) {
        JsonObject root = gson.toJsonTree(callback == null ? Map.of() : callback).getAsJsonObject();
        JsonObject body = getObject(root, "Body");
        JsonObject stkCallback = getObject(body, "stkCallback");
        if (stkCallback == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid M-Pesa callback body"));
        }

        String checkoutRequestId = read(stkCallback, "CheckoutRequestID");
        Optional<PaymentTransactionModel> optionalPayment =
                paymentRepository.findFirstByProviderCheckoutIdOrderByCreatedAtDesc(checkoutRequestId);
        if (optionalPayment.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Payment callback received but transaction was not found"));
        }

        PaymentTransactionModel payment = optionalPayment.get();
        int resultCode = readInt(stkCallback, "ResultCode");
        payment.setProviderMessage(read(stkCallback, "ResultDesc"));
        if (resultCode == 0) {
            payment.setBillingStatus("PAID");
            payment.setProviderStatus("PAID");
            payment.setProviderReceipt(callbackMetadata(stkCallback, "MpesaReceiptNumber"));
            paymentRepository.save(payment);
            organizationRepository.findByCodeIgnoreCase(payment.getOrganizationCode()).ifPresent(organization -> {
                applyPaymentToOrganization(organization, payment);
                organizationRepository.save(organization);
            });
        } else {
            payment.setBillingStatus("FAILED");
            payment.setProviderStatus("FAILED");
            paymentRepository.save(payment);
        }
        return ResponseEntity.ok(Map.of("message", "Callback processed"));
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

    private String validateMpesa(MpesaPaymentRequest request) {
        if (request == null) {
            return "M-Pesa payment body is required";
        }
        if (isBlank(request.getOrganizationCode())) {
            return "Organization is required";
        }
        if (request.getAmount() == null) {
            return "Amount is required";
        }
        if (request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return "Amount must be greater than zero";
        }
        if (isBlank(request.getPhoneNumber())) {
            return "M-Pesa phone number is required";
        }
        if (request.getPaidThrough() == null) {
            return "Paid through date is required";
        }
        return null;
    }

    private String read(JsonObject json, String field) {
        return json != null && json.has(field) && !json.get(field).isJsonNull()
                ? json.get(field).getAsString()
                : "";
    }

    private JsonObject getObject(JsonObject json, String field) {
        return json != null && json.has(field) && json.get(field).isJsonObject()
                ? json.getAsJsonObject(field)
                : null;
    }

    private int readInt(JsonObject json, String field) {
        try {
            return json != null && json.has(field) ? json.get(field).getAsInt() : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private String callbackMetadata(JsonObject stkCallback, String name) {
        try {
            JsonObject metadata = stkCallback.getAsJsonObject("CallbackMetadata");
            JsonArray items = metadata.getAsJsonArray("Item");
            for (JsonElement item : items) {
                JsonObject row = item.getAsJsonObject();
                if (name.equals(read(row, "Name")) && row.has("Value")) {
                    return row.get("Value").getAsString();
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private String cleanText(String value) { return value == null ? "" : value.trim(); }
    private String cleanUpper(String value) { return cleanText(value).toUpperCase(); }
    private String defaultUpper(String value, String fallback) {
        String clean = cleanUpper(value);
        return clean.isEmpty() ? fallback : clean;
    }
}
