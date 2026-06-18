package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.FundDocumentModel;
import com.hr.backend.fin_repository.FundDocumentRepository;
import com.hr.backend.service.StorageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/fund-documents")
@CrossOrigin(origins = "*")
public class FundDocumentController {

    private final StorageService storageService;
    private final FundDocumentRepository repository;

    public FundDocumentController(StorageService storageService, FundDocumentRepository repository) {
        this.storageService = storageService;
        this.repository = repository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("organization") String organization,
            @RequestParam("fundCode") String fundCode,
            @RequestParam("fundName") String fundName,
            @RequestParam("category") String category,
            @RequestParam("documentName") String documentName,
            @RequestParam(value = "uploadedBy", required = false) String uploadedBy,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String cleanOrganization = clean(organization);
            String cleanFundCode = clean(fundCode);
            String cleanCategory = clean(category).toLowerCase();
            String originalFileName = file.getOriginalFilename() == null
                    ? "document"
                    : file.getOriginalFilename();
            String contentType = file.getContentType() == null
                    ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                    : file.getContentType();
            LocalDateTime archiveTime = LocalDateTime.now();
            String objectName = cleanOrganization + "/funds/" + cleanFundCode + "/" + cleanCategory + "/"
                    + System.currentTimeMillis() + "_" + sanitizeFileName(originalFileName);

            System.out.println("Fund document upload received: org=" + cleanOrganization
                    + ", fundCode=" + cleanFundCode
                    + ", category=" + cleanCategory
                    + ", file=" + originalFileName
                    + ", size=" + file.getSize());

            String fileUrl = storageService.uploadFile(
                    file.getBytes(),
                    objectName,
                    contentType
            );

            FundDocumentModel document = new FundDocumentModel();
            document.setOrganization(cleanOrganization);
            document.setFundCode(cleanFundCode);
            document.setFundName(clean(fundName));
            document.setCategory(cleanCategory);
            document.setDocumentName(clean(documentName));
            document.setOriginalFileName(originalFileName);
            document.setVersionNumber("1.0");
            document.setArchiveStatus("Archived");
            document.setFileUrl(fileUrl);
            document.setContentType(contentType);
            document.setUploadedAt(archiveTime);
            document.setUploadedBy(clean(uploadedBy));
            document.setArchivedAt(archiveTime);
            document.setArchivedBy(clean(uploadedBy));

            return ResponseEntity.ok(repository.save(document));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fund document upload failed: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    @GetMapping("/organization/{organization}/fund/{fundCode}")
    public List<FundDocumentModel> getDocuments(
            @PathVariable String organization,
            @PathVariable String fundCode
    ) {
        return repository.findByOrganizationAndFundCodeOrderByUploadedAtDesc(
                clean(organization),
                clean(fundCode)
        );
    }

    @GetMapping("/organization/{organization}/fund/{fundCode}/category/{category}")
    public List<FundDocumentModel> getDocumentsByCategory(
            @PathVariable String organization,
            @PathVariable String fundCode,
            @PathVariable String category
    ) {
        return repository.findByOrganizationAndFundCodeAndCategoryOrderByUploadedAtDesc(
                clean(organization),
                clean(fundCode),
                clean(category).toLowerCase()
        );
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        FundDocumentModel document = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        byte[] fileBytes = storageService.downloadFile(document.getFileUrl());

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + document.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType(document)))
                .body(fileBytes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        FundDocumentModel document = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        storageService.deleteFile(document.getFileUrl());
        repository.deleteById(id);

        return ResponseEntity.ok("Document deleted successfully");
    }

    @PatchMapping("/{id}/rename")
    public ResponseEntity<?> renameDocument(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload
    ) {
        FundDocumentModel document = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        String documentName = clean(payload == null ? "" : payload.get("documentName"));
        if (documentName.isEmpty()) {
            return ResponseEntity.badRequest().body("Document name is required");
        }
        document.setDocumentName(documentName);
        return ResponseEntity.ok(repository.save(document));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String sanitizeFileName(String value) {
        String cleaned = clean(value).replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleaned.isEmpty() ? "document" : cleaned;
    }

    private String contentType(FundDocumentModel document) {
        String value = clean(document.getContentType());
        return value.isEmpty() ? MediaType.APPLICATION_OCTET_STREAM_VALUE : value;
    }
}
