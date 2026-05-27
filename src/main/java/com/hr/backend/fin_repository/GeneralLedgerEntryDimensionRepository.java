/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.GeneralLedgerEntryDimensionModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GeneralLedgerEntryDimensionRepository
        extends JpaRepository<GeneralLedgerEntryDimensionModel, Long> {

    List<GeneralLedgerEntryDimensionModel> findByOrganization(String organization);

    List<GeneralLedgerEntryDimensionModel> findByOrganizationAndGlEntryId(
            String organization,
            Long glEntryId
    );

    List<GeneralLedgerEntryDimensionModel> findByOrganizationAndDocumentNo(
            String organization,
            String documentNo
    );

    Optional<GeneralLedgerEntryDimensionModel>
    findByOrganizationAndGlEntryIdAndDimensionCode(
            String organization,
            Long glEntryId,
            String dimensionCode
    );

    boolean existsByOrganizationAndGlEntryIdAndDimensionCode(
            String organization,
            Long glEntryId,
            String dimensionCode
    );

    void deleteByOrganizationAndGlEntryId(
            String organization,
            Long glEntryId
    );
    

    

   @Transactional
@Modifying
@Query(value = """
        DELETE FROM general_ledger_entry_dimensions
        WHERE organization = :organization
          AND gl_entry_id = :glEntryId
        """, nativeQuery = true)
int deleteDimensionsByGlEntryId(
        @Param("organization") String organization,
        @Param("glEntryId") Long glEntryId
);
}