package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.BankModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<BankModel, Long> {

    List<BankModel> findByOrganization(String organization);

    Optional<BankModel> findByBankCodeAndOrganization(String bankCode, String organization);

    boolean existsByBankCodeAndOrganization(String bankCode, String organization);
}
