package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.AuditChecklistItemModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditChecklistItemRepository extends JpaRepository<AuditChecklistItemModel, Long> {
    List<AuditChecklistItemModel> findByOrganizationAndAuditCodeOrderByLineNoAsc(String organization, String auditCode);
    void deleteByOrganizationAndAuditCode(String organization, String auditCode);
}
