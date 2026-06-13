package com.hr.backend.fin_controller;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app-updates")
public class AppUpdateController {

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

    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> latest() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("appId", appId);
        response.put("version", clean(latestVersion));
        response.put("latestVersion", clean(latestVersion));
        response.put("minRequiredVersion", clean(minRequiredVersion));
        response.put("mandatory", mandatory);
        response.put("notes", clean(releaseNotes));
        response.put("publishedAt", OffsetDateTime.now().toString());
        response.put("mac", platform(macUrl, macSha256, "pkg"));
        response.put("windows", platform(windowsUrl, windowsSha256, "exe"));

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(response);
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
}
