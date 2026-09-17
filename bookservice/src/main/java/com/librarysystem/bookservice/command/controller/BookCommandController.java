package com.librarysystem.bookservice.command.controller;

import com.librarysystem.bookservice.command.command.CreateBookCommand;
import com.librarysystem.bookservice.command.command.DeleteBookCommand;
import com.librarysystem.bookservice.command.command.UpdateBookCommand;
import com.librarysystem.bookservice.command.model.BookRequestModel;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class BookCommandController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String addBook(@Valid @RequestBody BookRequestModel model) {
        CreateBookCommand command = CreateBookCommand.builder()
                .id(UUID.randomUUID().toString())
                .name(model.getName())
                .author(model.getAuthor())
                .isReady(Boolean.TRUE)
                .build();
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{bookId}")
    public String updateBook(@RequestBody BookRequestModel model, @PathVariable String bookId) {
        UpdateBookCommand command = UpdateBookCommand.builder()
                .id(bookId)
                .name(model.getName())
                .author(model.getAuthor())
                .isReady(model.getIsReady())
                .build();
        return commandGateway.sendAndWait(command);
    }

    @DeleteMapping("/{bookId}")
    public String deleteBook(@PathVariable String bookId) {
        DeleteBookCommand command = DeleteBookCommand.builder()
                .id(bookId)
                .build();
        return commandGateway.sendAndWait(command);
    }
}
