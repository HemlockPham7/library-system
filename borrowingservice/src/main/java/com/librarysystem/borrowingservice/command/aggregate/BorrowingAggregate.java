package com.librarysystem.borrowingservice.command.aggregate;

import com.librarysystem.borrowingservice.command.command.CreateBorrowingCommand;
import com.librarysystem.borrowingservice.command.command.DeleteBorrowingCommand;
import com.librarysystem.borrowingservice.command.event.BorrowingCreatedEvent;
import com.librarysystem.borrowingservice.command.event.BorrowingDeletedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Aggregate
public class BorrowingAggregate {

    @AggregateIdentifier
    private String id;

    private String bookId;
    private String employeeId;
    private Date borrowingDate;
    private Date returnDate;

    public BorrowingAggregate(){}

    @CommandHandler
    public BorrowingAggregate(CreateBorrowingCommand command){
        BorrowingCreatedEvent event = new BorrowingCreatedEvent();
        BeanUtils.copyProperties(command,event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteBorrowingCommand command){
        BorrowingDeletedEvent event = new BorrowingDeletedEvent(command.getId());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(BorrowingCreatedEvent event){
        this.id = event.getId();
        this.bookId = event.getBookId();
        this.employeeId = event.getEmployeeId();
        this.borrowingDate = event.getBorrowingDate();
    }

    @EventSourcingHandler
    public void on(BorrowingDeletedEvent event){
        this.id = event.getId();
    }
}