package com.librarysystem.borrowingservice.command.saga;

import com.librarysystem.borrowingservice.command.command.DeleteBorrowingCommand;
import com.librarysystem.borrowingservice.command.event.BorrowingCreatedEvent;
import com.librarysystem.borrowingservice.command.event.BorrowingDeletedEvent;
import com.librarysystem.commonservice.command.RollBackStatusBookCommand;
import com.librarysystem.commonservice.command.UpdateStatusBookCommand;
import com.librarysystem.commonservice.event.BookRollBackStatusEvent;
import com.librarysystem.commonservice.event.BookUpdateStatusEvent;
import com.librarysystem.commonservice.model.BookResponseCommonModel;
import com.librarysystem.commonservice.model.EmployeeResponseCommonModel;
import com.librarysystem.commonservice.queries.GetBookDetailQuery;
import com.librarysystem.commonservice.queries.GetDetailEmployeeQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class BorrowingSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    private void handle(BorrowingCreatedEvent event) {
        log.info("BorrowingCreatedEvent in saga for BookId: " + event.getBookId() + " : EmployeeId: " + event.getEmployeeId());
        try {
            GetBookDetailQuery getBookDetailQuery = GetBookDetailQuery.builder()
                    .id(event.getBookId())
                    .build();
            BookResponseCommonModel bookResponseCommonModel = queryGateway.query(
                    getBookDetailQuery,
                    ResponseTypes.instanceOf(BookResponseCommonModel.class)
            ).join();
            if (!bookResponseCommonModel.getIsReady()) {
                throw new Exception("Borrowed book");
            } else {
                SagaLifecycle.associateWith("bookId", event.getBookId());
                UpdateStatusBookCommand command = UpdateStatusBookCommand.builder()
                        .bookId(event.getBookId())
                        .isReady(false)
                        .employeeId(event.getEmployeeId())
                        .borrowingId(event.getId())
                        .build();
                commandGateway.sendAndWait(command);
            }
        } catch (Exception ex){
            rollbackBorrowingRecord(event.getId());
            log.error(ex.getMessage());
        }
    }

    @SagaEventHandler(associationProperty = "bookId")
    private void handler(BookUpdateStatusEvent event){
        log.info("BookUpdateStatusEvent in Saga for BookId : " + event.getBookId());
        try {
            GetDetailEmployeeQuery getDetailEmployeeQuery = GetDetailEmployeeQuery.builder()
                    .id(event.getEmployeeId())
                    .build();
            EmployeeResponseCommonModel employeeModel = queryGateway.query(
                    getDetailEmployeeQuery,
                    ResponseTypes.instanceOf(EmployeeResponseCommonModel.class)
            ).join();
            if (employeeModel.getIsDisciplined()) {
                throw new Exception("Employee is disciplined");
            } else {
                log.info("borrow book successfully");
                SagaLifecycle.end();
            }

        } catch (Exception ex) {
            // rollBackBookStatus
            SagaLifecycle.associateWith("bookId", event.getBookId());
            RollBackStatusBookCommand command = RollBackStatusBookCommand.builder()
                    .bookId(event.getBookId())
                    .isReady(true)
                    .employeeId(event.getEmployeeId())
                    .borrowingId(event.getBorrowingId())
                    .build();
            commandGateway.sendAndWait(command);
            log.error(ex.getMessage());
        }

    }

    @SagaEventHandler(associationProperty = "bookId")
    private void handle(BookRollBackStatusEvent event){
        log.info("BookRollBackStatusEvent in Saga for book Id : {} " + event.getBookId());
        rollbackBorrowingRecord(event.getBorrowingId());
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    private void handle(BorrowingDeletedEvent event){
        log.info("BorrowDeletedEvent in Saga for Borrowing Id : {} " + event.getId());
        SagaLifecycle.end();
    }

    private void rollbackBorrowingRecord(String id){
        DeleteBorrowingCommand command = DeleteBorrowingCommand.builder()
                .id(id)
                .build();
        commandGateway.sendAndWait(command);
    }

}
