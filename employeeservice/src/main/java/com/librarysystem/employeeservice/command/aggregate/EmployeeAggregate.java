package com.librarysystem.employeeservice.command.aggregate;

import com.librarysystem.employeeservice.command.command.CreateEmployeeCommand;
import com.librarysystem.employeeservice.command.command.DeleteEmployeeCommand;
import com.librarysystem.employeeservice.command.command.UpdateEmployeeCommand;
import com.librarysystem.employeeservice.command.event.EmployeeCreateEvent;
import com.librarysystem.employeeservice.command.event.EmployeeDeleteEvent;
import com.librarysystem.employeeservice.command.event.EmployeeUpdateEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class EmployeeAggregate {
    @AggregateIdentifier
    private String id;
    private String firstName;
    private String lastName;
    private String Kin;
    private Boolean isDisciplined;

    @CommandHandler
    public EmployeeAggregate(CreateEmployeeCommand command){
        EmployeeCreateEvent event = new EmployeeCreateEvent();
        BeanUtils.copyProperties(command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateEmployeeCommand command){
        EmployeeUpdateEvent event = new EmployeeUpdateEvent();
        BeanUtils.copyProperties(command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteEmployeeCommand command){
        EmployeeDeleteEvent event = new EmployeeDeleteEvent();
        BeanUtils.copyProperties(command,event);

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(EmployeeCreateEvent event){
        this.id = event.getId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.Kin = event.getKin();
        this.isDisciplined = event.getIsDisciplined();
    }

    @EventSourcingHandler
    public void on(EmployeeUpdateEvent event){
        this.id = event.getId();
        this.firstName = event.getFirstName();
        this.lastName = event.getLastName();
        this.Kin = event.getKin();
        this.isDisciplined = event.getIsDisciplined();
    }

    @EventSourcingHandler
    public void on(EmployeeDeleteEvent event){
        this.id = event.getId();
    }
}
