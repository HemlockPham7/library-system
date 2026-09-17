package com.librarysystem.employeeservice.command.event;

import com.librarysystem.employeeservice.command.data.Employee;
import com.librarysystem.employeeservice.command.data.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class EmployeeEventHandler {

    private final EmployeeRepository employeeRepository;

    public EmployeeEventHandler(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @EventHandler
    public void on(EmployeeCreateEvent event) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(event,employee);

        employeeRepository.save(employee);
    }

    @EventHandler
    public void on(EmployeeUpdateEvent event) throws Exception {
        Optional<Employee> oldEmployee = employeeRepository.findById(event.getId());
        Employee employee = oldEmployee.orElseThrow(() -> new Exception("Employee not found"));
        employee.setFirstName(event.getFirstName());
        employee.setLastName(event.getLastName());
        employee.setKin(event.getKin());
        employee.setIsDisciplined(event.getIsDisciplined());

        employeeRepository.save(employee);
    }

    @EventHandler
    public void on(EmployeeDeleteEvent event) {
        try {
            employeeRepository.findById(event.getId()).orElseThrow(() -> new Exception("Employee not found"));
            employeeRepository.deleteById(event.getId());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
