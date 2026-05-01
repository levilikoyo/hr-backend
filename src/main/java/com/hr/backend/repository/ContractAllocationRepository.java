/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.repository;

/**
 *
 * @author apple
 */

import com.hr.backend.model.ContractAllocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractAllocationRepository extends JpaRepository<ContractAllocation, Integer> {

    List<ContractAllocation> findByOrganizationIgnoreCase(String organization);

    List<ContractAllocation> findByEmployeeRollAndOrganizationIgnoreCase(String employeeRoll, String organization);

    List<ContractAllocation> findByContractCodeAndOrganizationIgnoreCase(String contractCode, String organization);
}
