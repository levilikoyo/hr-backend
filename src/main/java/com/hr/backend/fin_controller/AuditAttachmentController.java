/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.AuditAttachmentEntity;
import com.hr.backend.service.AuditAttachmentService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/audit-attachments")
@CrossOrigin(origins = "*")
public class AuditAttachmentController {

    private final AuditAttachmentService service;

    public AuditAttachmentController(AuditAttachmentService service) {
        this.service = service;
    }

    @PostMapping("/finding/upload")
    public ResponseEntity<String> uploadFindingAttachment(
            @RequestParam("organization") String organization,
            @RequestParam("auditCode") String auditCode,
            @RequestParam("findingCode") String findingCode,
            @RequestParam("transactionNo") String transactionNo,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            AuditAttachmentEntity saved = service.uploadFindingAttachment(
                    organization,
                    auditCode,
                    findingCode,
                    transactionNo,
                    file
            );

            String downloadUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/audit-attachments/")
                    .path(String.valueOf(saved.getId()))
                    .path("/download")
                    .toUriString();

            return ResponseEntity.ok(downloadUrl);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("UPLOAD_FAILED: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long id) {
        try {
            AuditAttachmentEntity attachment = service.getAttachment(id);

            byte[] data = attachment.getFileData() == null ? new byte[0] : attachment.getFileData();
            ByteArrayResource resource = new ByteArrayResource(data);

            String fileName = attachment.getFileName() == null || attachment.getFileName().trim().isEmpty()
                    ? "audit-attachment-" + id
                    : attachment.getFileName().trim();

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

            if (attachment.getContentType() != null && !attachment.getContentType().trim().isEmpty()) {
                try {
                    mediaType = MediaType.parseMediaType(attachment.getContentType());
                } catch (Exception ignored) {
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                }
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(data.length)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(encodedFileName)
                                    .build()
                                    .toString()
                    )
                    .body(resource);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
