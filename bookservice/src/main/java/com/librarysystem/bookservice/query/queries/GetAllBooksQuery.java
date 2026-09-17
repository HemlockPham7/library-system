package com.librarysystem.bookservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAllBooksQuery {
    private int page;
    private int size;
    private String sort;
    private String direction;
}
