/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.CurrencyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<CurrencyModel, Long> {

    Optional<CurrencyModel> findByCurrencyCode(String curencyCode);

    boolean existsByCurrencyCode(String curencyCode);

    Optional<CurrencyModel> findByCurrencyCodeAndOrganization(String curencyCode, String organization);
    List<CurrencyModel> findByOrganization(String organization);

}
