package com.librarysystem.employeeservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteEmployeeCommand {

    @TargetAggregateIdentifier
    private String id;
}
