/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.service;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.AuditAttachmentEntity;
import com.hr.backend.fin_repository.AuditAttachmentRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuditAttachmentService {

    private final AuditAttachmentRepository repository;

    public AuditAttachmentService(AuditAttachmentRepository repository) {
        this.repository = repository;
    }

    public AuditAttachmentEntity uploadFindingAttachment(
            String organization,
            String auditCode,
            String findingCode,
            String transactionNo,
            MultipartFile file
    ) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file received.");
        }

        AuditAttachmentEntity attachment = new AuditAttachmentEntity();
        attachment.setOrganization(clean(organization));
        attachment.setAuditCode(clean(auditCode));
        attachment.setFindingCode(clean(findingCode));
        attachment.setTransactionNo(clean(transactionNo));
        attachment.setFileName(clean(file.getOriginalFilename()));
        attachment.setContentType(clean(file.getContentType()));
        attachment.setAttachmentType("FINDING");
        attachment.setFileData(file.getBytes());
        attachment.setUploadedAt(LocalDateTime.now());
        attachment.setUploadedBy("SYSTEM");

        return repository.save(attachment);
    }

    public AuditAttachmentEntity getAttachment(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found: " + id));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
