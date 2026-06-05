/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.mobile_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.mobile_model.DepartmentModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentModel, Long> {

    List<DepartmentModel> findByOrganizationOrderByDepartmentCodeAsc(String organization);

    List<DepartmentModel> findByOrganizationAndStatusOrderByDepartmentCodeAsc(
            String organization,
            String status
    );

    List<DepartmentModel> findByOrganizationAndBlockedFalseOrderByDepartmentCodeAsc(
            String organization
    );

    Optional<DepartmentModel> findByOrganizationAndDepartmentCode(
            String organization,
            String departmentCode
    );

    boolean existsByOrganizationAndDepartmentCode(
            String organization,
            String departmentCode
    );

    void deleteByOrganizationAndDepartmentCode(
            String organization,
            String departmentCode
    );
}
