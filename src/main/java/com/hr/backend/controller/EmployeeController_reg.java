/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.controller;

/**
 *
 * @author apple
 */
import com.hr.backend.model.Employee_reg;
import com.hr.backend.repository.EmployeeRepository_reg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/employees-reg")
@CrossOrigin(origins = "*")
public class EmployeeController_reg {

    @Autowired
    private EmployeeRepository_reg employeeRepository;

    @PostMapping
    public Employee_reg saveEmployeeReg(@RequestBody Employee_reg employee) {
        requireText(employee.getEmployeeId(), "Employee ID is required");
        employee.setEmployeeId(cleanCode(employee.getEmployeeId()));
        return employeeRepository.save(employee);
    }

    @GetMapping
    public List<Employee_reg> getAllEmployeeReg() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Employee_reg getEmployeeRegById(@PathVariable Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> notFound("Employee registration not found"));
    }

    @GetMapping("/employeeId/{employeeId}")
    public Employee_reg getEmployeeRegByEmployeeId(@PathVariable String employeeId) {
        Optional<Employee_reg> employee = employeeRepository.findByEmployeeId(cleanCode(employeeId));
        return employee.orElseThrow(() -> notFound("Employee registration not found"));
    }

    @PutMapping("/{id}")
    public Employee_reg updateEmployeeReg(@PathVariable Integer id, @RequestBody Employee_reg updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {
            requireText(updatedEmployee.getEmployeeId(), "Employee ID is required");
            employee.setEmployeeId(cleanCode(updatedEmployee.getEmployeeId()));
            employee.setFirstName(updatedEmployee.getFirstName());
            employee.setLastName(updatedEmployee.getLastName());
            employee.setSirName(updatedEmployee.getSirName());
            employee.setGender(updatedEmployee.getGender());
            employee.setDob(updatedEmployee.getDob());

            employee.setPob(updatedEmployee.getPob());
            employee.setAge(updatedEmployee.getAge());
            employee.setDep(updatedEmployee.getDep());
            employee.setSub_dep(updatedEmployee.getSub_dep());
            employee.setStatus(updatedEmployee.getStatus());
            employee.setMariage(updatedEmployee.getMariage());

            employee.setNation(updatedEmployee.getNation());
            employee.setPhone(updatedEmployee.getPhone());
            employee.setEmail(updatedEmployee.getEmail());
            employee.setHire_date(updatedEmployee.getHire_date());
            employee.setTitle(updatedEmployee.getTitle());
            employee.setCourse(updatedEmployee.getCourse());
            employee.setGrade(updatedEmployee.getGrade());

            employee.setPersName(updatedEmployee.getPersName());
            employee.setPersphone(updatedEmployee.getPersphone());
            employee.setPersemail(updatedEmployee.getPersemail());
            employee.setPersrela(updatedEmployee.getPersrela());
        
            return employeeRepository.save(employee);
        }).orElseThrow(() -> notFound("Employee registration not found"));
    }

    @DeleteMapping("/{id}")
    public String deleteEmployeeReg(@PathVariable Integer id) {
        if (!employeeRepository.existsById(id)) {
            throw notFound("Employee registration not found");
        }
        employeeRepository.deleteById(id);
        return "Employee deleted successfully";
    }

    private void requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private String cleanCode(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}
