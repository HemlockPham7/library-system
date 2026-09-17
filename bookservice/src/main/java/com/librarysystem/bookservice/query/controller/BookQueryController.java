package com.librarysystem.bookservice.query.controller;

import com.librarysystem.bookservice.query.model.BookPaginationResponseModel;
import com.librarysystem.bookservice.query.model.BookResponseCommonModel;
import com.librarysystem.bookservice.query.queries.GetAllBooksQuery;
import com.librarysystem.bookservice.query.queries.GetBookDetailQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
public class BookQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping("{bookId}")
    public BookResponseCommonModel getBookDetails(@PathVariable String bookId) {
        GetBookDetailQuery query = GetBookDetailQuery.builder()
                .id(bookId)
                .build();
        return queryGateway.query(query, ResponseTypes.instanceOf(BookResponseCommonModel.class)).join();
    }

    @GetMapping
    public BookPaginationResponseModel getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        GetAllBooksQuery query = GetAllBooksQuery.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .direction(direction)
                .build();

        return queryGateway.query(query, ResponseTypes.instanceOf(BookPaginationResponseModel.class)).join();
    }
}
