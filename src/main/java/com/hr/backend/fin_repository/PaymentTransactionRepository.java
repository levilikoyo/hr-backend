package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.PaymentTransactionModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionModel, Long> {

    List<PaymentTransactionModel> findAllByOrderByCreatedAtDesc();

    List<PaymentTransactionModel> findByOrganizationCodeIgnoreCaseOrderByCreatedAtDesc(String organizationCode);
}
