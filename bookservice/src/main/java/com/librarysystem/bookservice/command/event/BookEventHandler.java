package com.librarysystem.bookservice.command.event;

import com.librarysystem.bookservice.command.data.Book;
import com.librarysystem.bookservice.command.data.BookRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BookEventHandler {

    @Autowired
    private BookRepository bookRepository;

    @EventHandler
    public void on(BookCreateEvent event) {
        Book book = new Book();
        BeanUtils.copyProperties(event, book);

        bookRepository.save(book);
    }

    @EventHandler
    public void on(BookUpdateEvent bookUpdatedEvent){
        Optional<Book> oldBook = bookRepository.findById(bookUpdatedEvent.getId());

        if (oldBook.isPresent()) {
            Book book = oldBook.get();
            book.setName(bookUpdatedEvent.getName());
            book.setAuthor(bookUpdatedEvent.getAuthor());
            book.setIsReady(bookUpdatedEvent.getIsReady());

            bookRepository.save(book);
        }
    }

    @EventHandler
    public void on(BookDeleteEvent event){
        Optional<Book> oldBook = bookRepository.findById(event.getId());
        oldBook.ifPresent(book -> bookRepository.delete((book)));
    }
}
