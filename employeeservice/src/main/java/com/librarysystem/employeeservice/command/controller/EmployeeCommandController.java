package com.librarysystem.employeeservice.command.controller;

import com.librarysystem.employeeservice.command.command.CreateEmployeeCommand;
import com.librarysystem.employeeservice.command.command.DeleteEmployeeCommand;
import com.librarysystem.employeeservice.command.command.UpdateEmployeeCommand;
import com.librarysystem.employeeservice.command.model.CreateEmployeeModel;
import com.librarysystem.employeeservice.command.model.UpdateEmployeeModel;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeCommandController {

    private final CommandGateway commandGateway;

    public EmployeeCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String addEmployee(@Valid @RequestBody CreateEmployeeModel model){
        CreateEmployeeCommand command = CreateEmployeeCommand.builder()
                .id(UUID.randomUUID().toString())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .Kin(model.getKin())
                .isDisciplined(Boolean.FALSE)
                .build();
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{employeeId}")
    public String updateEmployee(@Valid @RequestBody UpdateEmployeeModel model, @PathVariable String employeeId) {
        UpdateEmployeeCommand command = UpdateEmployeeCommand.builder()
                .id(employeeId)
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .Kin(model.getKin())
                .isDisciplined(model.getIsDisciplined())
                .build();
        return commandGateway.sendAndWait(command);
    }

    @DeleteMapping("/{employeeId}")
    public String deleteEmployee(@PathVariable String employeeId){
        DeleteEmployeeCommand command = DeleteEmployeeCommand.builder()
                .id(employeeId)
                .build();
        return commandGateway.sendAndWait(command);
    }
}
