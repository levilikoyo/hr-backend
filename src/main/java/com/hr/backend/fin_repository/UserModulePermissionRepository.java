package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.UserModulePermissionModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserModulePermissionRepository extends JpaRepository<UserModulePermissionModel, Long> {

    List<UserModulePermissionModel> findByUsernameIgnoreCaseOrderByMenuCodeAscModuleCodeAsc(String username);

    List<UserModulePermissionModel> findByUsernameIgnoreCaseAndOrganizationCodeIgnoreCaseOrderByMenuCodeAscModuleCodeAsc(
            String username,
            String organizationCode
    );

    Optional<UserModulePermissionModel> findByUsernameIgnoreCaseAndOrganizationCodeIgnoreCaseAndModuleCodeIgnoreCase(
            String username,
            String organizationCode,
            String moduleCode
    );
}
