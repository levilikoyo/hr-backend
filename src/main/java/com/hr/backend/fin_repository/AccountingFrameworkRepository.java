package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.AccountingFrameworkModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountingFrameworkRepository extends JpaRepository<AccountingFrameworkModel, Long> {

    List<AccountingFrameworkModel> findByOrganization(String organization);

    Optional<AccountingFrameworkModel> findByOrganizationAndFrameworkCode(
            String organization,
            String frameworkCode
    );

    boolean existsByOrganizationAndFrameworkCode(
            String organization,
            String frameworkCode
    );
}