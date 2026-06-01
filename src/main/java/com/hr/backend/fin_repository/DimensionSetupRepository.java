/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */


import com.hr.backend.fin_model.DimensionSetupModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DimensionSetupRepository extends JpaRepository<DimensionSetupModel, Long> {

    boolean existsByOrganizationAndDimensionCode(
            String organization,
            String dimensionCode
    );

    Optional<DimensionSetupModel> findByOrganizationAndDimensionCode(
            String organization,
            String dimensionCode
    );

    List<DimensionSetupModel> findByOrganization(
            String organization
    );

    List<DimensionSetupModel> findByOrganizationAndBlockedFalse(
            String organization
    );

    List<DimensionSetupModel> findByOrganizationAndBlockedFalseAndShowInActualTrueOrderByDisplayOrderAsc(
            String organization
    );

    List<DimensionSetupModel> findByOrganizationOrderByDisplayOrderAscDimensionCodeAsc(
            String organization
    );

    List<DimensionSetupModel> findByOrganizationAndBlockedFalseAndStatusIgnoreCaseOrderByDisplayOrderAscDimensionCodeAsc(
            String organization,
            String status
    );
}