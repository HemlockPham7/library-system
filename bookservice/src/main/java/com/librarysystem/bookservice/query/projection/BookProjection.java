package com.librarysystem.bookservice.query.projection;

import com.librarysystem.bookservice.command.data.Book;
import com.librarysystem.bookservice.command.data.BookRepository;
import com.librarysystem.bookservice.query.model.*;
import com.librarysystem.bookservice.query.queries.GetAllBooksQuery;
import com.librarysystem.bookservice.query.queries.GetBookDetailQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookProjection {

    private final BookRepository bookRepository;
    private final RedissonClient redissonClient;

    public BookProjection(BookRepository bookRepository, RedissonClient redissonClient) {
        this.bookRepository = bookRepository;
        this.redissonClient = redissonClient;
    }

    @QueryHandler
    public BookResponseCommonModel handle(GetBookDetailQuery query) throws Exception {
        String cacheKey = "book:detail:" + query.getId();
        RBucket<BookResponseCommonModel> bucket = redissonClient.getBucket(cacheKey);

        BookResponseCommonModel cachedModel = bucket.get();
        if (cachedModel != null) {
            return cachedModel;
        }

        Book book = bookRepository.findById(query.getId()).orElseThrow(() -> new Exception("Book not found with BookId: " + query.getId()));

        BookResponseCommonModel model = new BookResponseCommonModel();
        BeanUtils.copyProperties(book, model);

        bucket.set(model, Duration.ofMinutes(10));

        return model;
    }

    @QueryHandler
    public BookPaginationResponseModel handle(GetAllBooksQuery query) {
        String cacheKey = String.format("books:page:%d:size:%d:sort:%s:dir:%s",
                query.getPage(), query.getSize(), query.getSort(), query.getDirection());
        RBucket<BookPaginationResponseModel> bucket = redissonClient.getBucket(cacheKey);

        BookPaginationResponseModel cachedPagination = bucket.get();
        if (cachedPagination != null) {
            return cachedPagination;
        }

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

        BookPaginationResponseModel result = new BookPaginationResponseModel(data, pagination);

        bucket.set(result, Duration.ofMinutes(5));

        return result;
    }
}
