/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.repository;

/**
 *
 * @author apple
 */

import com.hr.backend.model.SocialAffiliation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAffiliationRepository extends JpaRepository<SocialAffiliation, Integer> {

    List<SocialAffiliation> findByOrganisationIgnoreCase(String organisation);

    Optional<SocialAffiliation> findByEmpCodeAndOrganisationIgnoreCase(String empCode, String organisation);
}
