/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fin_service;

/**
 *
 * @author apple
 */
import fin_DTO.FundDTO;
import fin_Repository.FundNotFoundException;
import fin_Repository.FundRepository;
import fin_model.Fund;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundService {

    private final FundRepository fundRepository;

    public FundService(FundRepository fundRepository) {
        this.fundRepository = fundRepository;
    }

    public FundDTO createFund(FundDTO dto) {
        validateFund(dto);

        if (fundRepository.existsByFundCode(dto.getFundCode())) {
            throw new RuntimeException("Fund code already exists: " + dto.getFundCode());
        }

        Fund fund = dto.toEntity();
        Fund saved = fundRepository.save(fund);

        return new FundDTO(saved);
    }

    public List<FundDTO> getAllFunds() {
        return fundRepository.findAll()
                .stream()
                .map(FundDTO::new)
                .collect(Collectors.toList());
    }

    public FundDTO getFundById(Long id) {
        Fund fund = fundRepository.findById(id)
                .orElseThrow(() -> new FundNotFoundException("Fund not found with id: " + id));

        return new FundDTO(fund);
    }

    public FundDTO getFundByCode(String fundCode) {
        Fund fund = fundRepository.findByFundCode(fundCode)
                .orElseThrow(() -> new FundNotFoundException("Fund not found with code: " + fundCode));

        return new FundDTO(fund);
    }

    public List<FundDTO> getFundsByOrganization(String organization) {
        return fundRepository.findByOrganization(organization)
                .stream()
                .map(FundDTO::new)
                .collect(Collectors.toList());
    }

    public List<FundDTO> searchFunds(String keyword) {
        return fundRepository
                .findByFundCodeContainingIgnoreCaseOrFundNameContainingIgnoreCaseOrDonorContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword
                )
                .stream()
                .map(FundDTO::new)
                .collect(Collectors.toList());
    }

    public FundDTO updateFund(Long id, FundDTO dto) {
        Fund fund = fundRepository.findById(id)
                .orElseThrow(() -> new FundNotFoundException("Fund not found with id: " + id));

        fund.setFundName(dto.getFundName());
        fund.setFundType(dto.getFundType());
        fund.setDonor(dto.getDonor());
        fund.setCurrency(dto.getCurrency());
        fund.setBudgetYear(dto.getBudgetYear());
        fund.setGrantAgreementNo(dto.getGrantAgreementNo());
        fund.setStartDate(dto.getStartDate());
        fund.setClosingDate(dto.getClosingDate());
        fund.setRestricted(dto.getRestricted());
        fund.setBlocked(dto.getBlocked());
        fund.setStatus(dto.getStatus());
        fund.setDescription(dto.getDescription());
        fund.setLogoPath(dto.getLogoPath());
        fund.setHeaderPath(dto.getHeaderPath());
        fund.setFooterPath(dto.getFooterPath());
        fund.setBudget(safe(dto.getBudget()));
        fund.setCommitments(safe(dto.getCommitments()));
        fund.setEncumbrances(safe(dto.getEncumbrances()));
        fund.setActuals(safe(dto.getActuals()));
        fund.setActualYtd(safe(dto.getActualYtd()));
        fund.setAmountToDemand(safe(dto.getAmountToDemand()));
        fund.setOrganization(dto.getOrganization());

        Fund updated = fundRepository.save(fund);

        return new FundDTO(updated);
    }

    public FundDTO updateFundBalance(
            String fundCode,
            BigDecimal budget,
            BigDecimal commitments,
            BigDecimal encumbrances,
            BigDecimal actuals,
            BigDecimal actualYtd,
            BigDecimal amountToDemand
    ) {
        Fund fund = fundRepository.findByFundCode(fundCode)
                .orElseThrow(() -> new FundNotFoundException("Fund not found with code: " + fundCode));

        fund.setBudget(safe(budget));
        fund.setCommitments(safe(commitments));
        fund.setEncumbrances(safe(encumbrances));
        fund.setActuals(safe(actuals));
        fund.setActualYtd(safe(actualYtd));
        fund.setAmountToDemand(safe(amountToDemand));

        Fund saved = fundRepository.save(fund);

        return new FundDTO(saved);
    }

    public void deleteFund(Long id) {
        if (!fundRepository.existsById(id)) {
            throw new FundNotFoundException("Fund not found with id: " + id);
        }

        fundRepository.deleteById(id);
    }

    public BigDecimal calculateAvailableBalance(String fundCode) {
        Fund fund = fundRepository.findByFundCode(fundCode)
                .orElseThrow(() -> new FundNotFoundException("Fund not found with code: " + fundCode));

        BigDecimal budget = safe(fund.getBudget());
        BigDecimal commitments = safe(fund.getCommitments());
        BigDecimal encumbrances = safe(fund.getEncumbrances());
        BigDecimal actuals = safe(fund.getActuals());

        return budget.subtract(commitments).subtract(encumbrances).subtract(actuals);
    }

    private void validateFund(FundDTO dto) {
        if (dto.getFundCode() == null || dto.getFundCode().trim().isEmpty()) {
            throw new RuntimeException("Fund code is required");
        }

        if (dto.getFundName() == null || dto.getFundName().trim().isEmpty()) {
            throw new RuntimeException("Fund name is required");
        }
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
