/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.NeedsRequestApprovalHistoryModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NeedsRequestApprovalHistoryRepository
        extends JpaRepository<NeedsRequestApprovalHistoryModel, Long> {

    List<NeedsRequestApprovalHistoryModel> findByOrganizationAndNeedsRequestIdOrderByIdAsc(
            String organization,
            Long needsRequestId
    );
}
