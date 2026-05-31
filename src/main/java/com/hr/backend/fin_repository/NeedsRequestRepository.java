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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NeedsRequestRepository extends JpaRepository<NeedsRequestModel, Long> {

    List<NeedsRequestModel> findByOrganizationOrderByIdDesc(String organization);

    List<NeedsRequestModel> findByOrganizationAndStatusOrderByIdDesc(
            String organization,
            String status
    );

    boolean existsByOrganizationAndRequestNo(String organization, String requestNo);

    @Query("SELECT n FROM NeedsRequestModel n " +
           "WHERE n.organization = :organization " +
           "AND n.status LIKE CONCAT(:statusPrefix, '%') " +
           "ORDER BY n.id DESC")
    List<NeedsRequestModel> findPendingByOrganizationAndStatusPrefix(
            @Param("organization") String organization,
            @Param("statusPrefix") String statusPrefix
    );
}