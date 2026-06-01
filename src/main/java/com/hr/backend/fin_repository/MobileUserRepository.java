/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */


import com.hr.backend.fin_model.MobileUserModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobileUserRepository extends JpaRepository<MobileUserModel, Long> {

    Optional<MobileUserModel> findByEmailIgnoreCase(String email);

    Optional<MobileUserModel> findByOrganizationAndEmailIgnoreCase(
            String organization,
            String email
    );
}