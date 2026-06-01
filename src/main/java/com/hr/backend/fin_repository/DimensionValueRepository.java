/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */



import com.hr.backend.fin_model.DimensionValueModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DimensionValueRepository extends JpaRepository<DimensionValueModel, Long> {

    /*
     * Existing / Swing-safe methods
     * Do not remove these because they may already be used by your Java Swing app.
     */

    boolean existsByOrganizationAndDimensionCodeAndValueCode(
            String organization,
            String dimensionCode,
            String valueCode
    );

    Optional<DimensionValueModel> findByOrganizationAndDimensionCodeAndValueCode(
            String organization,
            String dimensionCode,
            String valueCode
    );

    List<DimensionValueModel> findByOrganization(
            String organization
    );

    List<DimensionValueModel> findByOrganizationAndDimensionCode(
            String organization,
            String dimensionCode
    );

    List<DimensionValueModel> findByOrganizationAndBlockedFalse(
            String organization
    );

    List<DimensionValueModel> findByOrganizationAndDimensionCodeAndBlockedFalse(
            String organization,
            String dimensionCode
    );

    List<DimensionValueModel> findByOrganizationAndStatusIgnoreCase(
            String organization,
            String status
    );

    List<DimensionValueModel> findByOrganizationAndDimensionCodeAndStatusIgnoreCase(
            String organization,
            String dimensionCode,
            String status
    );

    /*
     * Mobile / ordered methods
     */

    List<DimensionValueModel> findByOrganizationAndDimensionCodeOrderByValueCodeAsc(
            String organization,
            String dimensionCode
    );

    List<DimensionValueModel> findByOrganizationAndDimensionCodeAndBlockedFalseOrderByValueCodeAsc(
            String organization,
            String dimensionCode
    );

    List<DimensionValueModel> findByOrganizationAndDimensionCodeAndBlockedFalseAndStatusIgnoreCaseOrderByValueCodeAsc(
            String organization,
            String dimensionCode,
            String status
    );
}