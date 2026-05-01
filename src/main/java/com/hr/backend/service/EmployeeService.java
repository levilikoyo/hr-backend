/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.service;

/**
 *
 * @author apple
 */

import com.hr.backend.model.Employee_reg;
import com.hr.backend.repository.EmployeeRepository_reg;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository_reg repository;

    public EmployeeService(EmployeeRepository_reg repository) {
        this.repository = repository;
    }

    public Employee_reg saveEmployee(Employee_reg employee) {
        Employee_reg lastEmployee = repository.findLastEmployee();

        // First employee: keep user-entered ID
        if (lastEmployee == null) {
            if (employee.getEmployeeId() == null || !employee.getEmployeeId().matches(".*\\d+$")) {
                throw new RuntimeException("First employee ID must end with digits, e.g. AP001, CED-001, AR-DRC-0001");
           
            }
            return repository.save(employee);
        }

        String lastId = lastEmployee.getEmployeeId();

        if (lastId == null || !lastId.matches(".*\\d+$")) {
            throw new RuntimeException("Last employee ID is invalid. It must end with digits.");
        }

        String prefix = lastId.replaceAll("\\d+$", "");
        String numberPart = lastId.replaceAll("^.*?(\\d+)$", "$1");

        int number = Integer.parseInt(numberPart) + 1;
        String newNumber = String.format("%0" + numberPart.length() + "d", number);

        employee.setEmployeeId(prefix + newNumber);

        return repository.save(employee);
    }
}