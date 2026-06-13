package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.ValidationRuleModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidationRuleRepository extends JpaRepository<ValidationRuleModel, Long> {

    boolean existsByOrganizationAndCode(String organization, String code);

    Optional<ValidationRuleModel> findByOrganizationAndCode(String organization, String code);

    List<ValidationRuleModel> findByOrganizationOrderByCodeAsc(String organization);
}
