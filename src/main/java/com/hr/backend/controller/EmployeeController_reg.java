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

@RestController
@RequestMapping("/api/employees-reg")
@CrossOrigin(origins = "*")
public class EmployeeController_reg {

    @Autowired
    private EmployeeRepository_reg employeeRepository;

    @PostMapping
    public Employee_reg saveEmployeeReg(@RequestBody Employee_reg employee) {
        return employeeRepository.save(employee);
    }

    @GetMapping
    public List<Employee_reg> getAllEmployeeReg() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Employee_reg getEmployeeRegById(@PathVariable Integer id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @GetMapping("/employeeId/{employeeId}")
    public Employee_reg getEmployeeRegByEmployeeId(@PathVariable String employeeId) {
        Optional<Employee_reg> employee = employeeRepository.findByEmployeeId(employeeId);
        return employee.orElse(null);
    }

    @PutMapping("/{id}")
    public Employee_reg updateEmployeeReg(@PathVariable Integer id, @RequestBody Employee_reg updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setEmployeeId(updatedEmployee.getEmployeeId());
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
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteEmployeeReg(@PathVariable Integer id) {
        employeeRepository.deleteById(id);
        return "Employee deleted successfully";
    }
}
