/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fin_controller;

/**
 *
 * @author apple
 */

import fin_DTO.FundDTO;
import fin_service.FundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funds")
@CrossOrigin(origins = "*")
public class FundController {

    private final FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @PostMapping
    public ResponseEntity<FundDTO> createFund(@RequestBody FundDTO dto) {
        return ResponseEntity.ok(fundService.createFund(dto));
    }

    @GetMapping
    public ResponseEntity<List<FundDTO>> getAllFunds() {
        return ResponseEntity.ok(fundService.getAllFunds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FundDTO> getFundById(@PathVariable Long id) {
        return ResponseEntity.ok(fundService.getFundById(id));
    }

    @GetMapping("/code/{fundCode}")
    public ResponseEntity<FundDTO> getFundByCode(@PathVariable String fundCode) {
        return ResponseEntity.ok(fundService.getFundByCode(fundCode));
    }

    @GetMapping("/organization/{organization}")
    public ResponseEntity<List<FundDTO>> getFundsByOrganization(@PathVariable String organization) {
        return ResponseEntity.ok(fundService.getFundsByOrganization(organization));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FundDTO>> searchFunds(@RequestParam String keyword) {
        return ResponseEntity.ok(fundService.searchFunds(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FundDTO> updateFund(@PathVariable Long id, @RequestBody FundDTO dto) {
        return ResponseEntity.ok(fundService.updateFund(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFund(@PathVariable Long id) {
        fundService.deleteFund(id);
        return ResponseEntity.ok("Fund deleted successfully");
    }

    @GetMapping("/{fundCode}/available-balance")
    public ResponseEntity<BigDecimal> getAvailableBalance(@PathVariable String fundCode) {
        return ResponseEntity.ok(fundService.calculateAvailableBalance(fundCode));
    }

    @PutMapping("/{fundCode}/balance")
    public ResponseEntity<FundDTO> updateFundBalance(
            @PathVariable String fundCode,
            @RequestBody Map<String, BigDecimal> body
    ) {
        BigDecimal budget = body.get("budget");
        BigDecimal commitments = body.get("commitments");
        BigDecimal encumbrances = body.get("encumbrances");
        BigDecimal actuals = body.get("actuals");
        BigDecimal actualYtd = body.get("actualYtd");
        BigDecimal amountToDemand = body.get("amountToDemand");

        return ResponseEntity.ok(
                fundService.updateFundBalance(
                        fundCode,
                        budget,
                        commitments,
                        encumbrances,
                        actuals,
                        actualYtd,
                        amountToDemand
                )
        );
    }
}
