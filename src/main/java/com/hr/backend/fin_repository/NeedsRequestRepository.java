/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.NeedsRequestModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NeedsRequestRepository extends JpaRepository<NeedsRequestModel, Long> {

    List<NeedsRequestModel> findByOrganizationOrderByIdDesc(String organization);

    List<NeedsRequestModel> findByOrganizationAndStatusOrderByIdDesc(
            String organization,
            String status
    );

    boolean existsByOrganizationAndRequestNo(String organization, String requestNo);
}
