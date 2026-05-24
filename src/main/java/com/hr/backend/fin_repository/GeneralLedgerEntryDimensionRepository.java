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
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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
}