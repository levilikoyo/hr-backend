package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.OrganizationModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationModel, Long> {

    List<OrganizationModel> findByStatusIgnoreCaseOrderByCodeAsc(String status);

    List<OrganizationModel> findAllByOrderByCodeAsc();

    Optional<OrganizationModel> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    void deleteByCodeIgnoreCase(String code);
}
