package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.SystemUserModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemUserRepository extends JpaRepository<SystemUserModel, Long> {

    List<SystemUserModel> findAllByOrderByFullNameAsc();

    Optional<SystemUserModel> findByUsernameIgnoreCase(String username);

    Optional<SystemUserModel> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);
}
