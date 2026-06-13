package com.hr.backend.fin_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "validation_rule_lines",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"organization", "rule_code", "business_object_code"})
        }
)
public class ValidationRuleLineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Column(name = "rule_code", nullable = false)
    private String ruleCode;

    @Column(name = "business_object_code", nullable = false)
    private String businessObjectCode;

    @Column(name = "filtering_rule", length = 1000)
    private String filteringRule;

    private Boolean active = true;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private String createdDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public String getBusinessObjectCode() { return businessObjectCode; }
    public void setBusinessObjectCode(String businessObjectCode) { this.businessObjectCode = businessObjectCode; }
    public String getFilteringRule() { return filteringRule; }
    public void setFilteringRule(String filteringRule) { this.filteringRule = filteringRule; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
