package com.hr.backend.fin_repository;

import com.hr.backend.fin_model.FundDocumentModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundDocumentRepository extends JpaRepository<FundDocumentModel, Long> {

    List<FundDocumentModel> findByOrganizationAndFundCodeOrderByUploadedAtDesc(
            String organization,
            String fundCode
    );

    List<FundDocumentModel> findByOrganizationAndFundCodeAndCategoryOrderByUploadedAtDesc(
            String organization,
            String fundCode,
            String category
    );
}
