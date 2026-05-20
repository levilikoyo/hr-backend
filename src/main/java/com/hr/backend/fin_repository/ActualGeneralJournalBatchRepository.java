package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.ActualGeneralJournalBatchModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActualGeneralJournalBatchRepository
        extends JpaRepository<ActualGeneralJournalBatchModel, Long> {

    List<ActualGeneralJournalBatchModel> findByOrganization(String organization);

    List<ActualGeneralJournalBatchModel> findByOrganizationAndFrameworkCode(
            String organization,
            String frameworkCode
    );

    Optional<ActualGeneralJournalBatchModel> findByOrganizationAndFrameworkCodeAndBatchName(
            String organization,
            String frameworkCode,
            String batchName
    );

    boolean existsByOrganizationAndFrameworkCodeAndBatchName(
            String organization,
            String frameworkCode,
            String batchName
    );
}