package com.hr.backend.repository;

import com.hr.backend.model.RhArchiveFolder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RhArchiveFolderRepository extends JpaRepository<RhArchiveFolder, Long> {

    List<RhArchiveFolder> findByOrganizationOrderByFullPathAsc(String organization);

    Optional<RhArchiveFolder> findByOrganizationAndFullPath(String organization, String fullPath);
}
