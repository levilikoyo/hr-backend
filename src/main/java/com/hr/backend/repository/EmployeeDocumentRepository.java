/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.repository;

/**
 *
 * @author apple
 */

import com.hr.backend.model.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

  public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {


    List<EmployeeDocument> findByEmployeeCode(String employeeCode);

    List<EmployeeDocument> findByEmployeeCodeAndCategory(String employeeCode, String category);
}
