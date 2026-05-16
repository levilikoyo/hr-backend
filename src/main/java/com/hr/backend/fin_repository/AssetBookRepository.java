/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.AssetBookModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetBookRepository extends JpaRepository<AssetBookModel, Long> {

    List<AssetBookModel> findByOrganization(String organization);

    List<AssetBookModel> findByAssetCodeAndOrganization(
            String assetCode,
            String organization
    );

    Optional<AssetBookModel> findByAssetCodeAndBookCodeAndOrganization(
            String assetCode,
            String bookCode,
            String organization
    );

    boolean existsByAssetCodeAndBookCodeAndOrganization(
            String assetCode,
            String bookCode,
            String organization
    );
}
