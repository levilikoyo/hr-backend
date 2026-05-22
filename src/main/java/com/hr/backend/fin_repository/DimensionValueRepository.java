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

public interface DimensionValueRepository
        extends JpaRepository<DimensionValueModel, Long> {

    List<DimensionValueModel> findByOrganization(String organization);

    List<DimensionValueModel> findByOrganizationAndDimensionCode(
            String organization,
            String dimensionCode
    );

    List<DimensionValueModel> findByOrganizationAndDimensionCodeAndBlockedFalse(
            String organization,
            String dimensionCode
    );

    Optional<DimensionValueModel> findByOrganizationAndDimensionCodeAndValueCode(
            String organization,
            String dimensionCode,
            String valueCode
    );

    boolean existsByOrganizationAndDimensionCodeAndValueCode(
            String organization,
            String dimensionCode,
            String valueCode
    );
}
