package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.PurchaseOrderLineModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLineModel, Long> {
    List<PurchaseOrderLineModel> findByOrganizationAndPoNoOrderByLineNoAsc(String organization, String poNo);
    void deleteByOrganizationAndPoNo(String organization, String poNo);
}
