package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.AuditWorkflowModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditWorkflowRepository extends JpaRepository<AuditWorkflowModel, Long> {
    List<AuditWorkflowModel> findByOrganizationAndAuditCodeOrderByStepNoAsc(String organization, String auditCode);
    void deleteByOrganizationAndAuditCode(String organization, String auditCode);
}
