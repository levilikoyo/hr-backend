package com.hr.backend.fin_model;

import java.util.ArrayList;
import java.util.List;

public class AuditRequest {
    private AuditFileModel header;
    private List<AuditChecklistItemModel> checklist = new ArrayList<>();
    private List<AuditFindingModel> findings = new ArrayList<>();
    private List<AuditIneligibleCostModel> ineligibleCosts = new ArrayList<>();
    private List<AuditWorkflowModel> workflow = new ArrayList<>();

    public AuditFileModel getHeader() { return header; }
    public void setHeader(AuditFileModel header) { this.header = header; }
    public List<AuditChecklistItemModel> getChecklist() { return checklist; }
    public void setChecklist(List<AuditChecklistItemModel> checklist) { this.checklist = checklist; }
    public List<AuditFindingModel> getFindings() { return findings; }
    public void setFindings(List<AuditFindingModel> findings) { this.findings = findings; }
    public List<AuditIneligibleCostModel> getIneligibleCosts() { return ineligibleCosts; }
    public void setIneligibleCosts(List<AuditIneligibleCostModel> ineligibleCosts) { this.ineligibleCosts = ineligibleCosts; }
    public List<AuditWorkflowModel> getWorkflow() { return workflow; }
    public void setWorkflow(List<AuditWorkflowModel> workflow) { this.workflow = workflow; }
}
