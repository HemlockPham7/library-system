package com.librarysystem.bookservice.query.projection;

import com.librarysystem.bookservice.command.data.Book;
import com.librarysystem.bookservice.command.data.BookRepository;
import com.librarysystem.bookservice.query.model.*;
import com.librarysystem.bookservice.query.queries.GetAllBooksQuery;
import com.librarysystem.bookservice.query.queries.GetBookDetailQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookProjection {

    @Autowired
    private BookRepository bookRepository;

    @QueryHandler
    public BookResponseCommonModel handle(GetBookDetailQuery query) throws Exception {
        BookResponseCommonModel model = new BookResponseCommonModel();

        Book book = bookRepository.findById(query.getId()).orElseThrow(() -> new Exception("Book not found"));
        BeanUtils.copyProperties(book, model);

        return model;
    }

    @QueryHandler
    public BookPaginationResponseModel handle(GetAllBooksQuery query) {
        Sort.Direction sortDirection = query.getDirection().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sortBy = Sort.by(sortDirection, query.getSort());
        Pageable pageable = PageRequest.of(
                query.getPage(),
                query.getSize(),
                sortBy
        );
        Page<Book> books = bookRepository.findAll(pageable);

        List<BookResponseCommonModel> data = books.getContent()
                .stream()
                .map(book -> {
                    BookResponseCommonModel model = new BookResponseCommonModel();

                    BeanUtils.copyProperties(book, model);
                    return model;
                })
                .collect(Collectors.toList());
        PaginationResponseModel pagination = new PaginationResponseModel(
                books.getNumber(),
                books.getSize(),
                books.getTotalElements()
        );

        return new BookPaginationResponseModel(data, pagination);
    }
}
