package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.AuditFileModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditFileRepository extends JpaRepository<AuditFileModel, Long> {
    Optional<AuditFileModel> findByOrganizationAndAuditCode(String organization, String auditCode);
    boolean existsByOrganizationAndAuditCode(String organization, String auditCode);
    List<AuditFileModel> findByOrganizationOrderByIdDesc(String organization);
}
