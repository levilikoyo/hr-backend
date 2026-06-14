package com.hr.backend.fin_model;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderRequest {

    private PurchaseOrderModel header;
    private List<PurchaseOrderLineModel> lines = new ArrayList<>();

    public PurchaseOrderModel getHeader() { return header; }
    public void setHeader(PurchaseOrderModel header) { this.header = header; }
    public List<PurchaseOrderLineModel> getLines() { return lines; }
    public void setLines(List<PurchaseOrderLineModel> lines) { this.lines = lines; }
}
