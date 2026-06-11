package com.hr.backend.controller;

import com.hr.backend.model.Ajouter_employee;
import com.hr.backend.repository.ContractAllocationRepository;
import com.hr.backend.repository.Ajouter_EmployeeReposotory;
import com.hr.backend.repository.DependantRepository;
import com.hr.backend.repository.EmployeeDocumentRepository;
import com.hr.backend.repository.SocialAffiliationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ajouter_employee")
@CrossOrigin(origins = "*")
public class Ajouter_employeeController {

    @Autowired
    private Ajouter_EmployeeReposotory employeeRepository;

    @Autowired
    private DependantRepository dependantRepository;

    @Autowired
    private SocialAffiliationRepository socialAffiliationRepository;

    @Autowired
    private ContractAllocationRepository contractAllocationRepository;

    @Autowired
    private EmployeeDocumentRepository employeeDocumentRepository;

    @PostMapping
    public Ajouter_employee saveEmployee(@RequestBody Ajouter_employee employee) {
        requireText(employee.getOrganization(), "Organization is required");
        requireText(employee.getRoll(), "Employee roll is required");
        employee.setOrganization(cleanText(employee.getOrganization()));
        employee.setRoll(cleanCode(employee.getRoll()));
        return employeeRepository.save(employee);
    }

    @GetMapping
    public List<Ajouter_employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/id/{id}")
    public Ajouter_employee getEmployeeById(@PathVariable Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> notFound("Employee not found"));
    }

    @GetMapping("/roll/{roll}")
    public Ajouter_employee getEmployeeByRoll(@PathVariable String roll) {
        Optional<Ajouter_employee> employee = employeeRepository.findByRoll(cleanCode(roll));
        return employee.orElseThrow(() -> notFound("Employee not found"));
    }

    @GetMapping("/roll/{roll}/organization/{organization}")
    public Ajouter_employee getEmployeeByRollAndOrganization(
            @PathVariable String roll,
            @PathVariable String organization) {
        return employeeRepository
                .findByRollAndOrganization(cleanCode(roll), cleanText(organization))
                .orElseThrow(() -> notFound("Employee not found"));
    }

    @GetMapping("/organization/{organization}")
    public List<Ajouter_employee> getEmployeeByOrganization(@PathVariable String organization) {
        return employeeRepository.findByOrganization(cleanText(organization));
    }

    @PutMapping("/{id}")
    public Ajouter_employee updateEmployee(@PathVariable Integer id, @RequestBody Ajouter_employee updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {

            requireText(updatedEmployee.getOrganization(), "Organization is required");
            requireText(updatedEmployee.getRoll(), "Employee roll is required");
            employee.setRoll(cleanCode(updatedEmployee.getRoll()));
            employee.setOrganization(cleanText(updatedEmployee.getOrganization()));
            employee.setNames(updatedEmployee.getNames());
            employee.setSir_name(updatedEmployee.getSir_name());
            employee.setAdress(updatedEmployee.getAdress());
            employee.setDob(updatedEmployee.getDob());
            employee.setPob(updatedEmployee.getPob());
            employee.setPhone(updatedEmployee.getPhone());
            employee.setMail(updatedEmployee.getMail());
            employee.setBlood(updatedEmployee.getBlood());

            employee.setGender(updatedEmployee.getGender());
            employee.setMariage(updatedEmployee.getMariage());
            employee.setNation(updatedEmployee.getNation());

            employee.setEduc_level(updatedEmployee.getEduc_level());
            employee.setEduc_faculty(updatedEmployee.getEduc_faculty());
            employee.setJob_title(updatedEmployee.getJob_title());

            employee.setPersName(updatedEmployee.getPersName());
            employee.setPersphone(updatedEmployee.getPersphone());
            employee.setPersemail(updatedEmployee.getPersemail());
            employee.setPersrela(updatedEmployee.getPersrela());
            employee.setPersadress(updatedEmployee.getPersadress());

            return employeeRepository.save(employee);
        }).orElseThrow(() -> notFound("Employee not found"));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public String deleteEmployee(@PathVariable Integer id) {
        Ajouter_employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> notFound("Employee not found"));
        deleteEmployeeChildren(employee.getRoll(), employee.getOrganization());
        employeeRepository.delete(employee);
        return "Employee deleted successfully";
    }
@DeleteMapping("/{roll}/organization/{organization}")
@Transactional
public String deleteEmployee(
        @PathVariable String roll,
        @PathVariable String organization) {

    String cleanRoll = cleanCode(roll);
    String cleanOrganization = cleanText(organization);

    Ajouter_employee employee = employeeRepository
            .findByRollAndOrganization(cleanRoll, cleanOrganization)
            .orElseThrow(() -> notFound("Employee not found"));

    deleteEmployeeChildren(cleanRoll, cleanOrganization);
    employeeRepository.delete(employee);

    return "Deleted successfully";
}
    @PutMapping("/employment-info")
    public Ajouter_employee updateEmploymentInfo(@RequestBody Ajouter_employee updatedData) {
        Ajouter_employee employee = employeeRepository
                .findByRollAndOrganization(cleanCode(updatedData.getRoll()), cleanText(updatedData.getOrganization()))
               
                .orElseThrow(() -> notFound("Employee not found"));

        employee.setHiredDate(updatedData.getHiredDate());
        employee.setStatus(updatedData.getStatus());
        employee.setEndingDate(updatedData.getEndingDate());

        return employeeRepository.save(employee);
    }

    private void requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanCode(String value) {
        return cleanText(value).toUpperCase();
    }

    private void deleteEmployeeChildren(String roll, String organization) {
        dependantRepository.deleteAll(
                dependantRepository.findByEmployeeIdAndOrganizationIgnoreCase(roll, organization)
        );
        contractAllocationRepository.deleteAll(
                contractAllocationRepository.findByEmployeeRollAndOrganizationIgnoreCase(roll, organization)
        );
        socialAffiliationRepository
                .findByEmpCodeAndOrganisationIgnoreCase(roll, organization)
                .ifPresent(socialAffiliationRepository::delete);
        employeeDocumentRepository.deleteAll(
                employeeDocumentRepository.findByEmployeeCodeAndOrganization(roll, organization)
        );
    }
}
