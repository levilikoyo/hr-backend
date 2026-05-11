/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.VendorModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<VendorModel, Long> {

    List<VendorModel> findByOrganization(String organization);

    Optional<VendorModel> findByVendorCodeAndOrganization(String vendorCode, String organization);

    boolean existsByVendorCodeAndOrganization(String vendorCode, String organization);

    List<VendorModel> findByOrganizationAndBlocked(String organization, Boolean blocked);

    List<VendorModel> findByOrganizationAndStatus(String organization, String status);

    List<VendorModel> findByOrganizationAndInvoicing(String organization, String invoicing);
}
