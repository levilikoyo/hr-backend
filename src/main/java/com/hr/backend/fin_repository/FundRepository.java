/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.FundModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FundRepository extends JpaRepository<FundModel, Long> {

    Optional<FundModel> findByFundCode(String fundCode);

    boolean existsByFundCode(String fundCode);

    Optional<FundModel> findByOrganizationAndFund(String fundCode,String organization);
    List<FundModel> findByOrganization(String organization);

    List<FundModel> findByStatus(String status);

    List<FundModel> findByBlocked(Boolean blocked);

    List<FundModel> findByDonorContainingIgnoreCase(String donor);

    List<FundModel> findByFundCodeContainingIgnoreCaseOrFundNameContainingIgnoreCaseOrDonorContainingIgnoreCase(
            String fundCode,
            String fundName,
            String donor
    );
}
