/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.FixedAssetModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedAssetRepository extends JpaRepository<FixedAssetModel, Long> {

    List<FixedAssetModel> findByOrganization(String organization);

    Optional<FixedAssetModel> findByAssetCodeAndOrganization(
            String assetCode,
            String organization
    );

    boolean existsByAssetCodeAndOrganization(
            String assetCode,
            String organization
    );
}
