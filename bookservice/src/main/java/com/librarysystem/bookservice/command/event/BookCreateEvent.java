package com.librarysystem.bookservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateEvent {
    private String id;

    private String name;

    private String author;

    private Boolean isReady;
}
