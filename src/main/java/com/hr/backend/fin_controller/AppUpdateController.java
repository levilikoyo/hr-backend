package com.hr.backend.fin_controller;

import com.hr.backend.service.StorageService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/app-updates")
public class AppUpdateController {

    private static final String WINDOWS_MANIFEST_OBJECT = "desktop-updates/latest-windows.properties";

    private final StorageService storageService;

    public AppUpdateController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Value("${ems.app.id:com.likoyo.emsl}")
    private String appId;

    @Value("${ems.app.update.version:1.0.0}")
    private String latestVersion;

    @Value("${ems.app.update.min-version:1.0.0}")
    private String minRequiredVersion;

    @Value("${ems.app.update.notes:EMS-L update available.}")
    private String releaseNotes;

    @Value("${ems.app.update.mandatory:false}")
    private boolean mandatory;

    @Value("${ems.app.update.mac.url:}")
    private String macUrl;

    @Value("${ems.app.update.mac.sha256:}")
    private String macSha256;

    @Value("${ems.app.update.windows.url:}")
    private String windowsUrl;

    @Value("${ems.app.update.windows.sha256:}")
    private String windowsSha256;

    @Value("${ems.app.update.windows.installer-type:exe}")
    private String windowsInstallerType;

    @Value("${ems.app.update.bucket:ems-l-desktop-updates-136775602294}")
    private String updateBucket;

    @Value("${ems.app.update.publish-token:}")
    private String publishToken;

    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> latest() {
        Properties publishedWindows = loadPublishedWindowsManifest();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("appId", appId);
        String effectiveLatestVersion = firstNonEmpty(publishedWindows.getProperty("version"), latestVersion);
        response.put("version", clean(effectiveLatestVersion));
        response.put("latestVersion", clean(effectiveLatestVersion));
        response.put("minRequiredVersion", clean(minRequiredVersion));
        response.put("mandatory", Boolean.parseBoolean(firstNonEmpty(publishedWindows.getProperty("mandatory"), String.valueOf(mandatory))));
        response.put("notes", firstNonEmpty(publishedWindows.getProperty("notes"), releaseNotes));
        response.put("publishedAt", firstNonEmpty(publishedWindows.getProperty("publishedAt"), OffsetDateTime.now().toString()));
        response.put("mac", platform(macUrl, macSha256, "pkg"));
        response.put("windows", platform(
                firstNonEmpty(publishedWindows.getProperty("url"), windowsUrl),
                firstNonEmpty(publishedWindows.getProperty("sha256"), windowsSha256),
                firstNonEmpty(publishedWindows.getProperty("installerType"), clean(windowsInstallerType).isEmpty() ? "exe" : clean(windowsInstallerType))
        ));

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(response);
    }

    @PostMapping(value = "/publish/windows", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> publishWindows(
            @RequestParam("file") MultipartFile file,
            @RequestParam("version") String version,
            @RequestParam(value = "notes", required = false) String notes,
            @RequestParam(value = "mandatory", defaultValue = "false") boolean updateMandatory,
            @RequestParam(value = "uploadedBy", required = false) String uploadedBy,
            @RequestHeader(value = "X-EMS-Update-Token", required = false) String token
    ) throws Exception {
        if (clean(publishToken).isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Update publishing is not configured on the server."));
        }
        if (!clean(publishToken).equals(clean(token))) {
            return ResponseEntity.status(403).body(Map.of("message", "You are not allowed to publish application updates."));
        }

        String cleanVersion = clean(version);
        if (cleanVersion.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Version is required."));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Windows .exe file is required."));
        }
        String originalName = clean(file.getOriginalFilename());
        if (!originalName.toLowerCase().endsWith(".exe")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please select a Windows .exe file."));
        }

        byte[] bytes = file.getBytes();
        String sha256 = sha256(bytes);
        String objectName = "desktop-updates/windows/EMS-L-" + safeFilePart(cleanVersion) + ".exe";
        String url = storageService.uploadObjectToBucket(updateBucket, bytes, objectName, "application/vnd.microsoft.portable-executable");
        String publishedAt = OffsetDateTime.now().toString();

        Properties manifest = new Properties();
        manifest.setProperty("version", cleanVersion);
        manifest.setProperty("url", url);
        manifest.setProperty("sha256", sha256);
        manifest.setProperty("installerType", "portable-exe");
        manifest.setProperty("mandatory", String.valueOf(updateMandatory));
        manifest.setProperty("notes", clean(notes).isEmpty() ? "EMS-L Windows update " + cleanVersion : clean(notes));
        manifest.setProperty("uploadedBy", clean(uploadedBy));
        manifest.setProperty("publishedAt", publishedAt);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        manifest.store(out, "EMS-L latest Windows portable update");
        storageService.uploadObject(out.toByteArray(), WINDOWS_MANIFEST_OBJECT, "text/plain; charset=UTF-8");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Windows update published.");
        response.put("version", cleanVersion);
        response.put("url", url);
        response.put("sha256", sha256);
        response.put("installerType", "portable-exe");
        response.put("publishedAt", publishedAt);
        return ResponseEntity.ok(response);
    }

    private Map<String, String> platform(String url, String sha256, String installerType) {
        Map<String, String> platform = new LinkedHashMap<>();
        platform.put("url", clean(url));
        platform.put("sha256", clean(sha256));
        platform.put("installerType", installerType);
        return platform;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonEmpty(String primary, String fallback) {
        String cleanPrimary = clean(primary);
        return cleanPrimary.isEmpty() ? clean(fallback) : cleanPrimary;
    }

    private Properties loadPublishedWindowsManifest() {
        Properties properties = new Properties();
        try {
            byte[] bytes = storageService.downloadObject(WINDOWS_MANIFEST_OBJECT);
            if (bytes != null && bytes.length > 0) {
                properties.load(new ByteArrayInputStream(bytes));
            }
        } catch (Exception ignored) {
            // Environment variables remain the fallback manifest.
        }
        return properties;
    }

    private String safeFilePart(String value) {
        String cleaned = clean(value).replaceAll("[^A-Za-z0-9._-]", "-");
        return cleaned.isEmpty() ? "update" : cleaned;
    }

    private String sha256(byte[] bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(bytes);
        StringBuilder builder = new StringBuilder();
        for (byte b : hash) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
