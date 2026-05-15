/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.CustomerModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {

    List<CustomerModel> findByOrganization(String organization);

    Optional<CustomerModel> findByCustomerCodeAndOrganization(
            String customerCode,
            String organization
    );

    boolean existsByCustomerCodeAndOrganization(
            String customerCode,
            String organization
    );
}