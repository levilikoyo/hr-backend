/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */
import com.hr.backend.fin_model.AssetBookCategoryModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetBookCategoryRepository extends JpaRepository<AssetBookCategoryModel, Long> {

    List<AssetBookCategoryModel> findByOrganization(String organization);

    Optional<AssetBookCategoryModel> findByBookCodeAndOrganization(
            String bookCode,
            String organization
    );

    boolean existsByBookCodeAndOrganization(
            String bookCode,
            String organization
    );
}
