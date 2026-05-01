/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hr.backend.repository;

/**
 *
 * @author apple
 */

import com.hr.backend.model.Employee_reg;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository_reg extends JpaRepository<Employee_reg, Integer> {
    Optional<Employee_reg> findByEmployeeId(String employeeId);
    
    @Query(value = "SELECT * FROM employee_reg ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Employee_reg findLastEmployee();

}
