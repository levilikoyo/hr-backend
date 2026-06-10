/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.GLAccountModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GLAccountRepository extends JpaRepository<GLAccountModel, Long> {

    List<GLAccountModel> findByOrganization(String organization);

    Optional<GLAccountModel> findByGlCodeAndOrganization(String glCode, String organization);

    boolean existsByGlCodeAndOrganization(String glCode, String organization);

    boolean existsByOrganizationAndFrameworkCodeAndGlCode(
            String organization,
            String frameworkCode,
            String glCode
    );

    List<GLAccountModel> findByOrganizationAndBlocked(String organization, Boolean blocked);

    List<GLAccountModel> findByOrganizationAndAccountCategory(String organization, String accountCategory);

    List<GLAccountModel> findByOrganizationAndIncomeBalance(String organization, String incomeBalance);
    
    List<GLAccountModel> findByOrganizationAndFrameworkCode(
        String organization,
        String frameworkCode
);
}
