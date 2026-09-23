package com.librarysystem.bookservice.command.aggregate;


import com.librarysystem.bookservice.command.command.CreateBookCommand;
import com.librarysystem.bookservice.command.command.DeleteBookCommand;
import com.librarysystem.bookservice.command.command.UpdateBookCommand;
import com.librarysystem.bookservice.command.event.BookCreateEvent;
import com.librarysystem.bookservice.command.event.BookDeleteEvent;
import com.librarysystem.bookservice.command.event.BookUpdateEvent;
import com.librarysystem.commonservice.command.RollBackStatusBookCommand;
import com.librarysystem.commonservice.command.UpdateStatusBookCommand;
import com.librarysystem.commonservice.event.BookRollBackStatusEvent;
import com.librarysystem.commonservice.event.BookUpdateStatusEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
@Getter
@Setter
public class BookAggregate {

    @AggregateIdentifier
    private String id;
    private String name;
    private String author;
    private Boolean isReady;

    @CommandHandler
    public BookAggregate(CreateBookCommand command) {
        BookCreateEvent bookCreateEvent = new BookCreateEvent();
        BeanUtils.copyProperties(command, bookCreateEvent);

        AggregateLifecycle.apply(bookCreateEvent);
    }

    @CommandHandler
    public void handle(UpdateBookCommand command) {
        BookUpdateEvent bookUpdateEvent = new BookUpdateEvent();
        BeanUtils.copyProperties(command, bookUpdateEvent);

        AggregateLifecycle.apply(bookUpdateEvent);
    }

    @CommandHandler
    public void handle(DeleteBookCommand command) {
        BookDeleteEvent bookDeleteEvent = new BookDeleteEvent();
        BeanUtils.copyProperties(command, bookDeleteEvent);

        AggregateLifecycle.apply(bookDeleteEvent);
    }

    @CommandHandler
    public void handler(UpdateStatusBookCommand command) {
        BookUpdateStatusEvent bookUpdateStatusEvent = new BookUpdateStatusEvent();
        BeanUtils.copyProperties(command, bookUpdateStatusEvent);

        AggregateLifecycle.apply(bookUpdateStatusEvent);
    }

    @CommandHandler
    public void handler(RollBackStatusBookCommand command){
        BookRollBackStatusEvent event = new BookRollBackStatusEvent();
        BeanUtils.copyProperties(command,event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(BookCreateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookUpdateEvent bookUpdatedEvent){
        this.id = bookUpdatedEvent.getId();
        this.name = bookUpdatedEvent.getName();
        this.author = bookUpdatedEvent.getAuthor();
        this.isReady = bookUpdatedEvent.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookDeleteEvent bookDeletedEvent){
        this.id = bookDeletedEvent.getId();
    }

    @EventSourcingHandler
    public void on(BookUpdateStatusEvent event) {
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on (BookRollBackStatusEvent event){
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }
}
