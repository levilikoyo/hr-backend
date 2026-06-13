package com.hr.backend.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MpesaClientService {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Value("${MPESA_ENABLED:false}")
    private boolean enabled;

    @Value("${MPESA_BASE_URL:https://sandbox.safaricom.co.ke}")
    private String baseUrl;

    @Value("${MPESA_CONSUMER_KEY:}")
    private String consumerKey;

    @Value("${MPESA_CONSUMER_SECRET:}")
    private String consumerSecret;

    @Value("${MPESA_SHORTCODE:}")
    private String shortcode;

    @Value("${MPESA_PASSKEY:}")
    private String passkey;

    @Value("${MPESA_CALLBACK_URL:}")
    private String callbackUrl;

    @Value("${MPESA_TRANSACTION_TYPE:CustomerPayBillOnline}")
    private String transactionType;

    @Value("${MPESA_COUNTRY_CODE:243}")
    private String countryCode;

    public MpesaResponse requestPayment(String phoneNumber, BigDecimal amount, String accountReference, String description) {
        if (!enabled) {
            return MpesaResponse.error("M-Pesa is not enabled on the server.");
        }
        if (isBlank(consumerKey) || isBlank(consumerSecret) || isBlank(shortcode) || isBlank(passkey) || isBlank(callbackUrl)) {
            return MpesaResponse.error("M-Pesa server credentials are incomplete.");
        }

        try {
            String token = accessToken();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String passwordSource = shortcode + passkey + timestamp;
            String password = Base64.getEncoder().encodeToString(passwordSource.getBytes(StandardCharsets.UTF_8));
            String normalizedPhone = normalizePhone(phoneNumber);

            Map<String, Object> body = new HashMap<>();
            body.put("BusinessShortCode", shortcode);
            body.put("Password", password);
            body.put("Timestamp", timestamp);
            body.put("TransactionType", transactionType);
            body.put("Amount", amount.setScale(0, java.math.RoundingMode.HALF_UP).intValue());
            body.put("PartyA", normalizedPhone);
            body.put("PartyB", shortcode);
            body.put("PhoneNumber", normalizedPhone);
            body.put("CallBackURL", callbackUrl);
            body.put("AccountReference", limit(accountReference, 12));
            body.put("TransactionDesc", limit(description, 100));

            Request request = new Request.Builder()
                    .url(cleanBaseUrl() + "/mpesa/stkpush/v1/processrequest")
                    .post(RequestBody.create(gson.toJson(body), JSON))
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() == null ? "" : response.body().string();
                JsonObject json = gson.fromJson(responseBody, JsonObject.class);
                if (!response.isSuccessful()) {
                    return MpesaResponse.error(read(json, "errorMessage", responseBody));
                }
                return MpesaResponse.success(
                        read(json, "MerchantRequestID", ""),
                        read(json, "CheckoutRequestID", ""),
                        read(json, "ResponseDescription", "M-Pesa payment request sent.")
                );
            }
        } catch (Exception e) {
            return MpesaResponse.error(e.getMessage());
        }
    }

    private String accessToken() throws Exception {
        Request request = new Request.Builder()
                .url(cleanBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials")
                .get()
                .addHeader("Authorization", Credentials.basic(consumerKey, consumerSecret))
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                throw new IllegalStateException(body);
            }
            JsonObject json = gson.fromJson(body, JsonObject.class);
            String token = read(json, "access_token", "");
            if (token.isEmpty()) {
                throw new IllegalStateException("M-Pesa access token was not returned.");
            }
            return token;
        }
    }

    private String cleanBaseUrl() {
        String value = baseUrl == null ? "" : baseUrl.trim();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private String normalizePhone(String phoneNumber) {
        String digits = phoneNumber == null ? "" : phoneNumber.replaceAll("\\D", "");
        if (digits.startsWith("0")) {
            return cleanCountryCode() + digits.substring(1);
        }
        return digits;
    }

    private String cleanCountryCode() {
        String digits = countryCode == null ? "" : countryCode.replaceAll("\\D", "");
        return digits.isEmpty() ? "243" : digits;
    }

    private String limit(String value, int max) {
        String clean = value == null ? "" : value.trim();
        return clean.length() <= max ? clean : clean.substring(0, max);
    }

    private String read(JsonObject json, String field, String fallback) {
        return json != null && json.has(field) && !json.get(field).isJsonNull()
                ? json.get(field).getAsString()
                : fallback;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static class MpesaResponse {
        private final boolean success;
        private final String merchantRequestId;
        private final String checkoutRequestId;
        private final String message;

        private MpesaResponse(boolean success, String merchantRequestId, String checkoutRequestId, String message) {
            this.success = success;
            this.merchantRequestId = merchantRequestId;
            this.checkoutRequestId = checkoutRequestId;
            this.message = message;
        }

        public static MpesaResponse success(String merchantRequestId, String checkoutRequestId, String message) {
            return new MpesaResponse(true, merchantRequestId, checkoutRequestId, message);
        }

        public static MpesaResponse error(String message) {
            return new MpesaResponse(false, "", "", message);
        }

        public boolean isSuccess() { return success; }
        public String getMerchantRequestId() { return merchantRequestId; }
        public String getCheckoutRequestId() { return checkoutRequestId; }
        public String getMessage() { return message; }
    }
}
