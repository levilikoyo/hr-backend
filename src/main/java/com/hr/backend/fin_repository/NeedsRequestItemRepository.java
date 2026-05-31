/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.NeedsRequestItemModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NeedsRequestItemRepository extends JpaRepository<NeedsRequestItemModel, Long> {

    List<NeedsRequestItemModel> findByOrganizationAndNeedsRequestId(
            String organization,
            Long needsRequestId
    );
}
