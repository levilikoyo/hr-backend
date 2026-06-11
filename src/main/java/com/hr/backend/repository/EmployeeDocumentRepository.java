/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.repository;

/**
 *
 * @author apple
 */

import com.hr.backend.model.ArchiveTreeDTO;
import com.hr.backend.model.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

  public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {


    List<EmployeeDocument> findByEmployeeCode(String employeeCode);

    List<EmployeeDocument> findByEmployeeCodeAndCategory(String employeeCode, String category);
    
   @Query("SELECT new com.hr.backend.model.ArchiveTreeDTO(d.organization, d.employeeCode, d.employeeNames, d.category) " +
       "FROM EmployeeDocument d GROUP BY d.organization, d.employeeCode, d.employeeNames, d.category")
List<ArchiveTreeDTO> getArchiveTreeData();

List<EmployeeDocument> findByEmployeeCodeAndCategoryAndOrganization(
        String employeeCode,
        String category,
        String organization
);

List<EmployeeDocument> findByEmployeeCodeAndOrganization(String employeeCode, String organization);
}
