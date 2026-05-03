/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.service;

/**
 *
 * @author apple
 */

import com.hr.backend.model.EmployeeDocument;
import com.hr.backend.repository.EmployeeDocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmployeeDocumentService {

    private final S3Client s3Client;
    private final EmployeeDocumentRepository repository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region}")
    private String region;

    public EmployeeDocumentService(S3Client s3Client, EmployeeDocumentRepository repository) {
        this.s3Client = s3Client;
        this.repository = repository;
    }

    public EmployeeDocument saveDocument(
            String employeeCode,
            String employeeNames,
            String category,
            String documentName,
            MultipartFile file
    ) throws Exception {

        String originalFileName = file.getOriginalFilename();

        String fileKey = "employee-documents/"
                + employeeCode + "/"
                + category.toLowerCase() + "/"
                + UUID.randomUUID() + "_" + originalFileName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(file.getBytes())
        );

        String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileKey;

        EmployeeDocument doc = new EmployeeDocument();
        doc.setEmployeeCode(employeeCode);
        doc.setEmployeeNames(employeeNames);
        doc.setCategory(category);
        doc.setDocumentName(documentName);
        doc.setOriginalFileName(originalFileName);
        doc.setFileKey(fileKey);
        doc.setFileUrl(fileUrl);
        doc.setContentType(file.getContentType());
        doc.setUploadedAt(LocalDateTime.now());

        return repository.save(doc);
    }
}
