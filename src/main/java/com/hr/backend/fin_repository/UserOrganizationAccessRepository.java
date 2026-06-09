package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.UserOrganizationAccessModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrganizationAccessRepository extends JpaRepository<UserOrganizationAccessModel, Long> {

    List<UserOrganizationAccessModel> findByUsernameIgnoreCaseAndStatusIgnoreCaseOrderByDefaultOrganizationDescOrganizationCodeAsc(
            String username,
            String status
    );

    List<UserOrganizationAccessModel> findByUsernameIgnoreCaseOrderByDefaultOrganizationDescOrganizationCodeAsc(String username);

    Optional<UserOrganizationAccessModel> findByUsernameIgnoreCaseAndOrganizationCodeIgnoreCase(
            String username,
            String organizationCode
    );

    boolean existsByUsernameIgnoreCaseAndOrganizationCodeIgnoreCase(String username, String organizationCode);
}
