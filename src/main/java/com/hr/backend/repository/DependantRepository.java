/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.repository;

/**
 *
 * @author apple
 */

import com.hr.backend.model.Dependant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DependantRepository extends JpaRepository<Dependant, Integer> {

    List<Dependant> findByOrganizationIgnoreCase(String organization);

    List<Dependant> findByEmployeeIdAndOrganizationIgnoreCase(String employeeId, String organization);
}
