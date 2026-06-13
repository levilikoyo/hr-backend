package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.ValidationRuleLineModel;
import com.hr.backend.fin_model.ValidationRuleModel;
import com.hr.backend.fin_repository.ValidationRuleLineRepository;
import com.hr.backend.fin_repository.ValidationRuleRepository;
import jakarta.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validation-rules")
@CrossOrigin(origins = "*")
public class ValidationRuleController {

    @Autowired
    private ValidationRuleRepository ruleRepository;

    @Autowired
    private ValidationRuleLineRepository lineRepository;

    @GetMapping("/organization/{organization}")
    public List<ValidationRuleModel> byOrganization(@PathVariable String organization) {
        return ruleRepository.findByOrganizationOrderByCodeAsc(organization);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ValidationRuleModel rule) {
        try {
            String validation = validateRule(rule);
            if (!validation.isEmpty()) {
                return ResponseEntity.badRequest().body(validation);
            }
            if (ruleRepository.existsByOrganizationAndCode(rule.getOrganization(), rule.getCode())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Validation rule already exists for this organization");
            }
            if (rule.getActive() == null) {
                rule.setActive(true);
            }
            if (isEmpty(rule.getCreatedDate())) {
                rule.setCreatedDate(today());
            }
            return ResponseEntity.ok(ruleRepository.save(rule));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Save validation rule failed: " + e.getMessage());
        }
    }

    @PutMapping("/rule-info")
    public ResponseEntity<?> update(@RequestBody ValidationRuleModel updated) {
        try {
            String validation = validateRule(updated);
            if (!validation.isEmpty()) {
                return ResponseEntity.badRequest().body(validation);
            }
            ValidationRuleModel rule = ruleRepository
                    .findByOrganizationAndCode(updated.getOrganization(), updated.getCode())
                    .orElseThrow(() -> new RuntimeException("Validation rule not found"));

            rule.setDescription(updated.getDescription());
            rule.setEffectiveDate(updated.getEffectiveDate());
            if (updated.getActive() != null) {
                rule.setActive(updated.getActive());
            }
            rule.setUpdatedBy(updated.getUpdatedBy());
            rule.setUpdatedDate(today());
            return ResponseEntity.ok(ruleRepository.save(rule));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Update validation rule failed: " + e.getMessage());
        }
    }

    @GetMapping("/organization/{organization}/rule/{ruleCode}/lines")
    public List<ValidationRuleLineModel> lines(
            @PathVariable String organization,
            @PathVariable String ruleCode) {

        return lineRepository.findByOrganizationAndRuleCodeOrderByBusinessObjectCodeAsc(organization, ruleCode);
    }

    @PostMapping("/organization/{organization}/rule/{ruleCode}/lines")
    @Transactional
    public ResponseEntity<?> replaceLines(
            @PathVariable String organization,
            @PathVariable String ruleCode,
            @RequestBody List<ValidationRuleLineModel> lines) {

        try {
            if (isEmpty(organization)) {
                return ResponseEntity.badRequest().body("Organization is required");
            }
            if (isEmpty(ruleCode)) {
                return ResponseEntity.badRequest().body("Validation rule code is required");
            }
            lineRepository.deleteByOrganizationAndRuleCode(organization, ruleCode);
            if (lines != null) {
                for (ValidationRuleLineModel line : lines) {
                    if (line == null || isEmpty(line.getBusinessObjectCode())) {
                        continue;
                    }
                    line.setOrganization(organization);
                    line.setRuleCode(ruleCode);
                    if (line.getActive() == null) {
                        line.setActive(true);
                    }
                    if (isEmpty(line.getCreatedDate())) {
                        line.setCreatedDate(today());
                    }
                    lineRepository.save(line);
                }
            }
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Save validation rule lines failed: " + e.getMessage());
        }
    }

    private String validateRule(ValidationRuleModel rule) {
        if (rule == null) {
            return "Validation rule is required";
        }
        if (isEmpty(rule.getOrganization())) {
            return "Organization is required";
        }
        if (isEmpty(rule.getCode())) {
            return "Validation rule code is required";
        }
        return "";
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
