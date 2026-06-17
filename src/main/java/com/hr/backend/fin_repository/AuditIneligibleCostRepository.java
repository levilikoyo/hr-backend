package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.AuditIneligibleCostModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditIneligibleCostRepository extends JpaRepository<AuditIneligibleCostModel, Long> {
    List<AuditIneligibleCostModel> findByOrganizationAndAuditCodeOrderByIdAsc(String organization, String auditCode);
    void deleteByOrganizationAndAuditCode(String organization, String auditCode);
}
