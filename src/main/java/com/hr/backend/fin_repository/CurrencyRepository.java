package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.CurrencyModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<CurrencyModel, Long> {

    List<CurrencyModel> findByOrganization(String organization);

    Optional<CurrencyModel> findByCurencyCode(String curencyCode);

    boolean existsByCurencyCode(String curencyCode);

    Optional<CurrencyModel> findByCurencyCodeAndOrganization(String curencyCode, String organization);
    

  Optional<CurrencyModel> findByOrganizationAndLcy(String organization, Boolean lcy);
  
}