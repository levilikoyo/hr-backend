package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.GeneralLedgerEntryModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralLedgerEntryRepository
        extends JpaRepository<GeneralLedgerEntryModel, Long> {

    List<GeneralLedgerEntryModel> findByOrganization(String organization);

    List<GeneralLedgerEntryModel> findByOrganizationAndTransactionType(
            String organization,
            String transactionType
    );

    List<GeneralLedgerEntryModel> findByOrganizationAndFrameworkCode(
            String organization,
            String frameworkCode
    );

    // ✅ ADD THIS ONE
    List<GeneralLedgerEntryModel> findByOrganizationAndDocumentNo(
            String organization,
            String documentNo
    );

    List<GeneralLedgerEntryModel> findByOrganizationAndSourceTypeAndSourceDocumentNo(
            String organization,
            String sourceType,
            String sourceDocumentNo
    );

    boolean existsByOrganizationAndSourceTypeAndSourceDocumentNoAndSourceLineNo(
            String organization,
            String sourceType,
            String sourceDocumentNo,
            String sourceLineNo
    );
    List<GeneralLedgerEntryModel> findByOrganizationAndFrameworkCodeAndJournalBatchNameOrderByIdDesc(
        String organization,
        String frameworkCode,
        String journalBatchName
);
    
}