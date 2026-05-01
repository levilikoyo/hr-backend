/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hr.backend.repository;

/**
 *
 * @author apple
 */
import com.hr.backend.model.Ajouter_employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Ajouter_EmployeeReposotory extends JpaRepository<Ajouter_employee, Integer> {
    Optional<Ajouter_employee> findByRoll(String roll);
    Optional<Ajouter_employee> findByRollAndOrganization(String roll, String organization);
      List<Ajouter_employee> findByOrganization(String organization);
    
}
