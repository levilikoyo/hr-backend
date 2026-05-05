package com.hr.backend.controller;

import com.hr.backend.model.Ajouter_employee;
import com.hr.backend.repository.Ajouter_EmployeeReposotory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ajouter_employee")
@CrossOrigin(origins = "*")
public class Ajouter_employeeController {

    @Autowired
    private Ajouter_EmployeeReposotory employeeRepository;

    @PostMapping
    public Ajouter_employee saveEmployee(@RequestBody Ajouter_employee employee) {
        return employeeRepository.save(employee);
    }

    @GetMapping
    public List<Ajouter_employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/id/{id}")
    public Ajouter_employee getEmployeeById(@PathVariable Integer id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @GetMapping("/roll/{roll}")
    public Ajouter_employee getEmployeeByRoll(@PathVariable String roll) {
        Optional<Ajouter_employee> employee = employeeRepository.findByRoll(roll);
        return employee.orElse(null);
    }

    @GetMapping("/organization/{organization}")
    public List<Ajouter_employee> getEmployeeByOrganization(@PathVariable String organization) {
        return employeeRepository.findByOrganization(organization);
    }

    @PutMapping("/{id}")
    public Ajouter_employee updateEmployee(@PathVariable Integer id, @RequestBody Ajouter_employee updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {

            employee.setRoll(updatedEmployee.getRoll());
            employee.setOrganization(updatedEmployee.getOrganization());
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
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable Integer id) {
        employeeRepository.deleteById(id);
        return "Employee deleted successfully";
    }
@DeleteMapping("/{roll}/organization/{organization}")
public String deleteEmployee(
        @PathVariable String roll,
        @PathVariable String organization) {

    Ajouter_employee employee = employeeRepository
            .findByRollAndOrganization(roll, organization)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    employeeRepository.delete(employee);

    return "Deleted successfully";
}
    @PutMapping("/employment-info")
    public Ajouter_employee updateEmploymentInfo(@RequestBody Ajouter_employee updatedData) {
        Ajouter_employee employee = employeeRepository
                .findByRollAndOrganization(updatedData.getRoll(), updatedData.getOrganization())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setHiredDate(updatedData.getHiredDate());
        employee.setStatus(updatedData.getStatus());
        employee.setEndingDate(updatedData.getEndingDate());

        return employeeRepository.save(employee);
    }
}