package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.GeneralLedgerEntryModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query(value = """
        SELECT document_no
        FROM general_ledger_entries
        WHERE organization = :organization
          AND framework_code = :frameworkCode
          AND journal_batch_name = :journalBatchName
          AND document_no LIKE :documentNoPattern
        ORDER BY id DESC
        LIMIT 1
        """, nativeQuery = true)
String findLastDocumentNo(
        @Param("organization") String organization,
        @Param("frameworkCode") String frameworkCode,
        @Param("documentNoPattern") String documentNoPattern
);


    
}