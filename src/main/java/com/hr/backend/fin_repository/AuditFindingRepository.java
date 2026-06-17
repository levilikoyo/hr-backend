package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.AuditFindingModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditFindingRepository extends JpaRepository<AuditFindingModel, Long> {
    List<AuditFindingModel> findByOrganizationAndAuditCodeOrderByIdAsc(String organization, String auditCode);
    void deleteByOrganizationAndAuditCode(String organization, String auditCode);
}
