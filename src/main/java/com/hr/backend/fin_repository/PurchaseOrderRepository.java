package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.PurchaseOrderModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderModel, Long> {
    Optional<PurchaseOrderModel> findByOrganizationAndPoNo(String organization, String poNo);
    boolean existsByOrganizationAndPoNo(String organization, String poNo);
    List<PurchaseOrderModel> findByOrganizationOrderByIdDesc(String organization);
}
