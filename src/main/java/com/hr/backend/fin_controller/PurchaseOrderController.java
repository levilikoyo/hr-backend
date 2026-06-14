package com.hr.backend.fin_controller;

import com.hr.backend.fin_model.PurchaseOrderLineModel;
import com.hr.backend.fin_model.PurchaseOrderModel;
import com.hr.backend.fin_model.PurchaseOrderRequest;
import com.hr.backend.fin_repository.PurchaseOrderLineRepository;
import com.hr.backend.fin_repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/purchase-orders")
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderLineRepository lineRepository;

    @Transactional
    @PostMapping
    public ResponseEntity<?> save(@RequestBody PurchaseOrderRequest request) {
        PurchaseOrderModel incoming = request == null ? null : request.getHeader();
        if (incoming == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Purchase order header is required"));
        }
        String organization = clean(incoming.getOrganization());
        String poNo = clean(incoming.getPoNo());
        if (organization.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Organization is required"));
        }
        if (poNo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "PO No. is required"));
        }

        PurchaseOrderModel header = purchaseOrderRepository
                .findByOrganizationAndPoNo(organization, poNo)
                .orElse(new PurchaseOrderModel());

        header.setOrganization(organization);
        header.setPoNo(poNo);
        header.setVendorCode(clean(incoming.getVendorCode()));
        header.setVendorName(clean(incoming.getVendorName()));
        header.setOrderDate(clean(incoming.getOrderDate()));
        header.setExpectedReceiptDate(clean(incoming.getExpectedReceiptDate()));
        header.setBuyer(clean(incoming.getBuyer()));
        header.setStatus(emptyTo(incoming.getStatus(), "Draft"));
        header.setSubtotal(zeroSafe(incoming.getSubtotal()));
        header.setTax(zeroSafe(incoming.getTax()));
        header.setCommitment(zeroSafe(incoming.getCommitment()));
        header.setReceived(zeroSafe(incoming.getReceived()));
        header.setBalance(zeroSafe(incoming.getBalance()));
        header.setCreatedBy(clean(incoming.getCreatedBy()));
        if (clean(header.getCreatedDate()).isEmpty()) {
            header.setCreatedDate(LocalDate.now().toString());
        }
        purchaseOrderRepository.save(header);

        lineRepository.deleteByOrganizationAndPoNo(organization, poNo);
        int lineNo = 1;
        List<PurchaseOrderLineModel> lines = request.getLines();
        if (lines != null) {
            for (PurchaseOrderLineModel line : lines) {
                line.setOrganization(organization);
                line.setPoNo(poNo);
                line.setLineNo(lineNo++);
                line.setQuantity(zeroSafe(line.getQuantity()));
                line.setUnitCost(zeroSafe(line.getUnitCost()));
                line.setDiscountPercent(zeroSafe(line.getDiscountPercent()));
                line.setTaxPercent(zeroSafe(line.getTaxPercent()));
                line.setLineAmount(zeroSafe(line.getLineAmount()));
                line.setAmountLcy(zeroSafe(line.getAmountLcy()));
                line.setReceived(zeroSafe(line.getReceived()));
                line.setInvoiced(zeroSafe(line.getInvoiced()));
                lineRepository.save(line);
            }
        }

        return ResponseEntity.ok(Map.of("message", "SUCCESS", "poNo", poNo));
    }

    @GetMapping("/organization/{organization}")
    public List<PurchaseOrderModel> byOrganization(@PathVariable String organization) {
        return purchaseOrderRepository.findByOrganizationOrderByIdDesc(clean(organization));
    }

    @GetMapping("/organization/{organization}/po/{poNo}/lines")
    public List<PurchaseOrderLineModel> lines(@PathVariable String organization, @PathVariable String poNo) {
        return lineRepository.findByOrganizationAndPoNoOrderByLineNoAsc(clean(organization), clean(poNo));
    }

    @Transactional
    @PutMapping("/organization/{organization}/po/{poNo}/post")
    public ResponseEntity<?> post(@PathVariable String organization, @PathVariable String poNo, @RequestBody Map<String, String> body) {
        PurchaseOrderModel header = purchaseOrderRepository
                .findByOrganizationAndPoNo(clean(organization), clean(poNo))
                .orElse(null);
        if (header == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Purchase order not found"));
        }
        header.setStatus("Posted");
        header.setPostedBy(body == null ? "" : clean(body.get("postedBy")));
        header.setPostedDate(LocalDate.now().toString());
        purchaseOrderRepository.save(header);

        List<PurchaseOrderLineModel> lines = lineRepository.findByOrganizationAndPoNoOrderByLineNoAsc(clean(organization), clean(poNo));
        for (PurchaseOrderLineModel line : lines) {
            line.setStatus("Posted");
            lineRepository.save(line);
        }
        return ResponseEntity.ok(Map.of("message", "SUCCESS", "poNo", clean(poNo)));
    }

    private String clean(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String emptyTo(String value, String fallback) {
        String clean = clean(value);
        return clean.isEmpty() ? fallback : clean;
    }

    private java.math.BigDecimal zeroSafe(java.math.BigDecimal value) {
        return value == null ? java.math.BigDecimal.ZERO : value;
    }
}
