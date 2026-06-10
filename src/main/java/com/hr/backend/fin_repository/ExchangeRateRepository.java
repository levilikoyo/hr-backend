/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.ExchangeRateModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateModel, Long> {

    List<ExchangeRateModel> findByOrganization(String organization);

    Optional<ExchangeRateModel> findByCurrencyCodeAndExchangeCurrencyDateAndOrganization(
            String currencyCode,
            String exchangeCurrencyDate,
            String organization
    );

    boolean existsByCurrencyCodeAndExchangeCurrencyDateAndOrganization(
            String currencyCode,
            String exchangeCurrencyDate,
            String organization
    );
    List<ExchangeRateModel> findByOrganizationAndCurrencyCode(
        String organization,
        String currencyCode
);

    Optional<ExchangeRateModel> findTopByOrganizationAndCurrencyCodeAndExchangeCurrencyDateLessThanEqualOrderByExchangeCurrencyDateDesc(
            String organization,
            String currencyCode,
            String exchangeCurrencyDate
    );

    Optional<ExchangeRateModel> findTopByOrganizationAndCurrencyCodeAndExchangeCurrencyDateGreaterThanOrderByExchangeCurrencyDateAsc(
            String organization,
            String currencyCode,
            String exchangeCurrencyDate
    );
}
