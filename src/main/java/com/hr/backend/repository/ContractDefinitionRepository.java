/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hr.backend.repository;

/**
 *
 * @author apple
 */

import com.hr.backend.model.ContractDefinition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractDefinitionRepository extends JpaRepository<ContractDefinition, Integer> {

    Optional<ContractDefinition> findByContractCode(String contractCode);
     List<ContractDefinition> findByOrganization(String organization);
     Optional<ContractDefinition> findByContractCodeAndOrganization(String contractCode, String organization);

}
