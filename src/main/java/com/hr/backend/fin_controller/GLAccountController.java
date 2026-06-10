/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */

import com.hr.backend.fin_model.GLAccountModel;
import com.hr.backend.fin_repository.GLAccountRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gl-accounts")
@CrossOrigin(origins = "*")
public class GLAccountController {

    @Autowired
    private GLAccountRepository glAccountRepository;

    @PostMapping
    public ResponseEntity<?> saveGLAccount(@RequestBody GLAccountModel glAccount) {
        try {
            if (glAccount.getOrganization() == null || glAccount.getOrganization().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Organization is required");
            }
            if (glAccount.getFrameworkCode() == null || glAccount.getFrameworkCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Accounting framework is required");
            }
            if (glAccount.getGlCode() == null || glAccount.getGlCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("G/L account code is required");
            }

            glAccount.setOrganization(glAccount.getOrganization().trim());
            glAccount.setFrameworkCode(glAccount.getFrameworkCode().trim());
            glAccount.setGlCode(glAccount.getGlCode().trim());

            if (glAccountRepository.existsByOrganizationAndFrameworkCodeAndGlCode(
                    glAccount.getOrganization(),
                    glAccount.getFrameworkCode(),
                    glAccount.getGlCode()
            )) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Duplicate G/L account code for this organization and framework: " + glAccount.getGlCode());
            }

            return ResponseEntity.ok(glAccountRepository.save(glAccount));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Duplicate G/L account code for this organization and framework: " + glAccount.getGlCode());
        }
    }

    @GetMapping("/organization/{organization}")
    public List<GLAccountModel> getByOrganization(@PathVariable String organization) {
        return glAccountRepository.findByOrganization(organization);
    }

   
/*
    @PutMapping("/update")
    public GLAccountModel updateGLAccount(@RequestBody GLAccountModel updatedData) {
        return glAccountRepository.update(updatedData);
    }

    @GetMapping("/organization/{organization}/blocked")
    public List<GLAccountModel> getBlockedAccounts(@PathVariable String organization) {
        return glAccountRepository.findBlockedAccounts(organization);
    }

    @GetMapping("/organization/{organization}/active")
    public List<GLAccountModel> getActiveAccounts(@PathVariable String organization) {
        return glAccountRepository.findActiveAccounts(organization);
    }
*/
    @DeleteMapping("/{id}")
    public String deleteGLAccount(@PathVariable Long id) {
        glAccountRepository.deleteById(id);
        return "G/L Account deleted successfully";
    }

    @GetMapping("/test")
    public String test() {
        return "G/L Account controller is working";
    }
    
    @GetMapping("/organization/{organization}/framework/{frameworkCode}")
public List<GLAccountModel> getGLAccountsByOrganizationAndFramework(
        @PathVariable String organization,
        @PathVariable String frameworkCode
) {
    return glAccountRepository.findByOrganizationAndFrameworkCode(
            organization,
            frameworkCode
    );
}
}
