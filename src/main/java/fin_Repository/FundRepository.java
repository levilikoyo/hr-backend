/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fin_Repository;

/**
 *
 * @author apple
 */

import fin_model.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FundRepository extends JpaRepository<Fund, Long> {

    Optional<Fund> findByFundCode(String fundCode);

    boolean existsByFundCode(String fundCode);

    List<Fund> findByOrganization(String organization);

    List<Fund> findByStatus(String status);

    List<Fund> findByBlocked(Boolean blocked);

    List<Fund> findByDonorContainingIgnoreCase(String donor);

    List<Fund> findByFundCodeContainingIgnoreCaseOrFundNameContainingIgnoreCaseOrDonorContainingIgnoreCase(
            String fundCode,
            String fundName,
            String donor
    );
}
