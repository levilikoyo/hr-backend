/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.MobileNotificationModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobileNotificationRepository extends JpaRepository<MobileNotificationModel, Long> {

    List<MobileNotificationModel> findByOrganizationAndUserEmailOrderByIdDesc(
            String organization,
            String userEmail
    );

    List<MobileNotificationModel> findByOrganizationAndUserRoleOrderByIdDesc(
            String organization,
            String userRole
    );

    List<MobileNotificationModel> findByOrganizationAndUserEmailAndReadStatusFalseOrderByIdDesc(
            String organization,
            String userEmail
    );

    List<MobileNotificationModel> findByOrganizationAndUserRoleAndReadStatusFalseOrderByIdDesc(
            String organization,
            String userRole
    );
}
