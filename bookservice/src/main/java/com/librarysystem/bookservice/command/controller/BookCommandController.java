package com.librarysystem.bookservice.command.controller;

import com.librarysystem.bookservice.command.command.CreateBookCommand;
import com.librarysystem.bookservice.command.model.BookRequestModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class BookCommandController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String addBook(@RequestBody BookRequestModel model) {
        CreateBookCommand command = CreateBookCommand.builder()
                .id(UUID.randomUUID().toString())
                .name(model.getName())
                .author(model.getAuthor())
                .isReady(Boolean.TRUE)
                .build();
        return commandGateway.sendAndWait(command);
    }
}
