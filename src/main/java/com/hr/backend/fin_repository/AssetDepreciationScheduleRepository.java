/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.AssetDepreciationScheduleModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetDepreciationScheduleRepository
        extends JpaRepository<AssetDepreciationScheduleModel, Long> {

    List<AssetDepreciationScheduleModel> findByOrganization(String organization);

    List<AssetDepreciationScheduleModel> findByAssetCodeAndBookCodeAndOrganization(
            String assetCode,
            String bookCode,
            String organization
    );

    Optional<AssetDepreciationScheduleModel>
            findByAssetCodeAndBookCodeAndDepreciationPeriodAndOrganization(
                    String assetCode,
                    String bookCode,
                    String depreciationPeriod,
                    String organization
            );

    boolean existsByAssetCodeAndBookCodeAndDepreciationPeriodAndOrganization(
            String assetCode,
            String bookCode,
            String depreciationPeriod,
            String organization
    );
}