package com.hr.backend.controller;

import com.hr.backend.model.RhArchiveFolder;
import com.hr.backend.repository.RhArchiveFolderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/rh-archive-folders")
public class RhArchiveFolderController {

    private final RhArchiveFolderRepository repository;

    public RhArchiveFolderController(RhArchiveFolderRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/organization/{organization}")
    public List<RhArchiveFolder> byOrganization(@PathVariable String organization) {
        return repository.findByOrganizationOrderByFullPathAsc(cleanText(organization));
    }

    @PostMapping
    public RhArchiveFolder create(@RequestBody Map<String, String> request) {
        String organization = cleanText(request.get("organization"));
        String folderName = cleanFolderName(request.get("folderName"));
        String parentPath = cleanPath(request.get("parentPath"));
        String createdBy = cleanText(request.get("createdBy"));

        requireText(organization, "Organization is required");
        requireText(folderName, "Folder name is required");

        String fullPath = parentPath.isEmpty() ? folderName : parentPath + "/" + folderName;
        repository.findByOrganizationAndFullPath(organization, fullPath).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Folder already exists.");
        });

        RhArchiveFolder folder = new RhArchiveFolder();
        folder.setOrganization(organization);
        folder.setFolderName(folderName);
        folder.setParentPath(parentPath);
        folder.setFullPath(fullPath);
        folder.setCreatedAt(LocalDateTime.now());
        folder.setCreatedBy(createdBy);
        return repository.save(folder);
    }

    private void requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanPath(String value) {
        return cleanText(value).replace("\\", "/").replaceAll("/+", "/").replaceAll("^/|/$", "");
    }

    private String cleanFolderName(String value) {
        String text = cleanText(value).replace("\\", "-").replace("/", "-");
        return text.replaceAll("\\s+", " ");
    }
}
