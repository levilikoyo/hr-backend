package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.AccountReportingMappingModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountReportingMappingRepository
        extends JpaRepository<AccountReportingMappingModel, Long> {

    List<AccountReportingMappingModel> findByOrganization(String organization);

    List<AccountReportingMappingModel> findByOrganizationAndSourceFrameworkCode(
            String organization,
            String sourceFrameworkCode
    );

    List<AccountReportingMappingModel> findByOrganizationAndTargetFrameworkCode(
            String organization,
            String targetFrameworkCode
    );

    Optional<AccountReportingMappingModel>
            findByOrganizationAndSourceFrameworkCodeAndSourceGlCodeAndTargetFrameworkCode(
                    String organization,
                    String sourceFrameworkCode,
                    String sourceGlCode,
                    String targetFrameworkCode
            );

    boolean existsByOrganizationAndSourceFrameworkCodeAndSourceGlCodeAndTargetFrameworkCode(
            String organization,
            String sourceFrameworkCode,
            String sourceGlCode,
            String targetFrameworkCode
    );
}