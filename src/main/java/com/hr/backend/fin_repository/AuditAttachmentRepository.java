/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_repository;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.AuditAttachmentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditAttachmentRepository extends JpaRepository<AuditAttachmentEntity, Long> {

    List<AuditAttachmentEntity> findByOrganizationAndAuditCodeOrderByIdDesc(
            String organization,
            String auditCode
    );

    List<AuditAttachmentEntity> findByOrganizationAndAuditCodeAndFindingCodeOrderByIdDesc(
            String organization,
            String auditCode,
            String findingCode
    );

    List<AuditAttachmentEntity> findByOrganizationAndAuditCodeAndTransactionNoOrderByIdDesc(
            String organization,
            String auditCode,
            String transactionNo
    );
}