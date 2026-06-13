package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.ValidationRuleLineModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidationRuleLineRepository extends JpaRepository<ValidationRuleLineModel, Long> {

    List<ValidationRuleLineModel> findByOrganizationAndRuleCodeOrderByBusinessObjectCodeAsc(
            String organization,
            String ruleCode
    );

    void deleteByOrganizationAndRuleCode(String organization, String ruleCode);
}
