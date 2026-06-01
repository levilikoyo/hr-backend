/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.fin_controller;

/**
 *
 * @author apple
 */


import com.hr.backend.fin_model.DimensionSetupModel;
import com.hr.backend.fin_model.DimensionValueModel;
import com.hr.backend.fin_repository.DimensionSetupRepository;
import com.hr.backend.fin_repository.DimensionValueRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dimension-setups")
@CrossOrigin(origins = "*")
public class DimensionSetupController {

    @Autowired
    private DimensionSetupRepository dimensionRepository;

    @Autowired
    private DimensionValueRepository dimensionValueRepository;

    @GetMapping("/test")
    public String test() {
        return "Dimension Setup API is working";
    }

    @PostMapping
    public ResponseEntity<?> saveDimension(@RequestBody DimensionSetupModel dimension) {
        try {
            if (isEmpty(dimension.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(dimension.getDimensionCode())) {
                return ResponseEntity.badRequest().body("Dimension code is required");
            }

            boolean exists = dimensionRepository.existsByOrganizationAndDimensionCode(
                    dimension.getOrganization(),
                    dimension.getDimensionCode()
            );

            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Dimension already exists");
            }

            if (dimension.getBlocked() == null) {
                dimension.setBlocked(false);
            }

            if (dimension.getRequired() == null) {
                dimension.setRequired(false);
            }

            if (dimension.getShowInActual() == null) {
                dimension.setShowInActual(true);
            }

            if (dimension.getDisplayOrder() == null) {
                dimension.setDisplayOrder(0);
            }

            if (isEmpty(dimension.getStatus())) {
                dimension.setStatus("Active");
            }

            if (isEmpty(dimension.getCreatedDate())) {
                dimension.setCreatedDate(todayDate());
            }

            return ResponseEntity.ok(dimensionRepository.save(dimension));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Save dimension failed: " + e.getMessage());
        }
    }

    @PutMapping("/dimension-info")
    public ResponseEntity<?> updateDimension(@RequestBody DimensionSetupModel updatedData) {
        try {
            if (isEmpty(updatedData.getOrganization())) {
                return ResponseEntity.badRequest().body("Organization is required");
            }

            if (isEmpty(updatedData.getDimensionCode())) {
                return ResponseEntity.badRequest().body("Dimension code is required");
            }

            DimensionSetupModel dimension = dimensionRepository
                    .findByOrganizationAndDimensionCode(
                            updatedData.getOrganization(),
                            updatedData.getDimensionCode()
                    )
                    .orElseThrow(() -> new RuntimeException("Dimension not found"));

            dimension.setDimensionName(updatedData.getDimensionName());
            dimension.setDescription(updatedData.getDescription());

            if (updatedData.getBlocked() != null) {
                dimension.setBlocked(updatedData.getBlocked());
            }

            if (updatedData.getRequired() != null) {
                dimension.setRequired(updatedData.getRequired());
            }

            if (updatedData.getShowInActual() != null) {
                dimension.setShowInActual(updatedData.getShowInActual());
            }

            if (updatedData.getDisplayOrder() != null) {
                dimension.setDisplayOrder(updatedData.getDisplayOrder());
            }

            if (!isEmpty(updatedData.getStatus())) {
                dimension.setStatus(updatedData.getStatus());
            }

            return ResponseEntity.ok(dimensionRepository.save(dimension));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Update dimension failed: " + e.getMessage());
        }
    }

    @GetMapping("/organization/{organization}")
    public List<DimensionSetupModel> getByOrganization(
            @PathVariable String organization) {

        return dimensionRepository.findByOrganization(organization);
    }

    @GetMapping("/organization/{organization}/active")
    public List<DimensionSetupModel> getActiveByOrganization(
            @PathVariable String organization) {

        return dimensionRepository.findByOrganizationAndBlockedFalse(organization);
    }

    @GetMapping("/organization/{organization}/actual-columns")
    public List<DimensionSetupModel> getActualColumns(
            @PathVariable String organization) {

        return dimensionRepository
                .findByOrganizationAndBlockedFalseAndShowInActualTrueOrderByDisplayOrderAsc(
                        organization
                );
    }

    @GetMapping("/organization/{organization}/code/{dimensionCode}")
    public ResponseEntity<?> getOneDimension(
            @PathVariable String organization,
            @PathVariable String dimensionCode) {

        return dimensionRepository
                .findByOrganizationAndDimensionCode(organization, dimensionCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * NEW MOBILE ENDPOINT
     * This endpoint is used by the mobile Needs Request form.
     * It returns each active dimension with its active values.
     *
     * Example:
     * /api/dimension-setups/organization/APN/grouped
     */
    @GetMapping("/organization/{organization}/grouped")
    public List<DimensionGroupResponse> getGroupedDimensions(
            @PathVariable String organization) {

        List<DimensionSetupModel> setups =
                dimensionRepository.findByOrganizationOrderByDisplayOrderAscDimensionCodeAsc(
                        organization
                );

        List<DimensionGroupResponse> result = new ArrayList<>();

        for (DimensionSetupModel setup : setups) {

            if (!isActiveSetup(setup)) {
                continue;
            }

            List<DimensionValueModel> values =
                    dimensionValueRepository
                            .findByOrganizationAndDimensionCodeAndBlockedFalseAndStatusIgnoreCaseOrderByValueCodeAsc(
                                    organization,
                                    setup.getDimensionCode(),
                                    "Active"
                            );

            List<DimensionValueResponse> valueResponses = new ArrayList<>();

            for (DimensionValueModel value : values) {
                DimensionValueResponse valueResponse = new DimensionValueResponse();

                valueResponse.setId(value.getId());
                valueResponse.setValueCode(value.getValueCode());
                valueResponse.setValueName(value.getValueName());
                valueResponse.setDescription(value.getDescription());

                valueResponses.add(valueResponse);
            }

            DimensionGroupResponse group = new DimensionGroupResponse();

            group.setId(setup.getId());
            group.setOrganization(setup.getOrganization());
            group.setDimensionCode(setup.getDimensionCode());
            group.setDimensionName(setup.getDimensionName());
            group.setDescription(setup.getDescription());
            group.setRequired(Boolean.TRUE.equals(setup.getRequired()));
            group.setDisplayOrder(setup.getDisplayOrder());
            group.setValues(valueResponses);

            result.add(group);
        }

        return result;
    }

    private boolean isActiveSetup(DimensionSetupModel setup) {
        if (setup == null) {
            return false;
        }

        if (Boolean.TRUE.equals(setup.getBlocked())) {
            return false;
        }

        if (Boolean.FALSE.equals(setup.getShowInActual())) {
            return false;
        }

        String status = setup.getStatus();

        return status == null || status.trim().equalsIgnoreCase("Active");
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String todayDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static class DimensionGroupResponse {

        private Long id;
        private String organization;
        private String dimensionCode;
        private String dimensionName;
        private String description;
        private Boolean required;
        private Integer displayOrder;
        private List<DimensionValueResponse> values;

        public Long getId() {
            return id;
        }

        public String getOrganization() {
            return organization;
        }

        public String getDimensionCode() {
            return dimensionCode;
        }

        public String getDimensionName() {
            return dimensionName;
        }

        public String getDescription() {
            return description;
        }

        public Boolean getRequired() {
            return required;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public List<DimensionValueResponse> getValues() {
            return values;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public void setDimensionCode(String dimensionCode) {
            this.dimensionCode = dimensionCode;
        }

        public void setDimensionName(String dimensionName) {
            this.dimensionName = dimensionName;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }

        public void setValues(List<DimensionValueResponse> values) {
            this.values = values;
        }
    }

    public static class DimensionValueResponse {

        private Long id;
        private String valueCode;
        private String valueName;
        private String description;

        public Long getId() {
            return id;
        }

        public String getValueCode() {
            return valueCode;
        }

        public String getValueName() {
            return valueName;
        }

        public String getDescription() {
            return description;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setValueCode(String valueCode) {
            this.valueCode = valueCode;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
