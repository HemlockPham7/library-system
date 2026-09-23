package com.librarysystem.bookservice.query.projection;

import com.librarysystem.bookservice.command.data.Book;
import com.librarysystem.bookservice.command.data.BookRepository;
import com.librarysystem.bookservice.query.model.*;
import com.librarysystem.bookservice.query.queries.GetAllBooksQuery;
import com.librarysystem.commonservice.queries.GetBookDetailQuery;
import com.librarysystem.commonservice.model.PaginationResponseModel;
import org.axonframework.queryhandling.QueryHandler;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
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

import static com.librarysystem.commonservice.services.cache.CacheConstant.CACHE_BOOKS_PAGES_GROUP;
import static com.librarysystem.commonservice.services.cache.CacheConstant.CACHE_BOOK_DETAIL_PREFIX;

@Component
public class BookProjection {

    private final BookRepository bookRepository;
    private final RedissonClient redissonClient;

    public BookProjection(BookRepository bookRepository, RedissonClient redissonClient) {
        this.bookRepository = bookRepository;
        this.redissonClient = redissonClient;
    }

    @QueryHandler
    public com.librarysystem.commonservice.model.BookResponseCommonModel handle(GetBookDetailQuery query) throws Exception {
        String cacheKey = CACHE_BOOK_DETAIL_PREFIX + query.getId();
        RBucket<com.librarysystem.commonservice.model.BookResponseCommonModel> bucket = redissonClient.getBucket(cacheKey);

        com.librarysystem.commonservice.model.BookResponseCommonModel cachedModel = bucket.get();
        if (cachedModel != null) {
            return cachedModel;
        }

        Book book = bookRepository.findById(query.getId()).orElseThrow(() -> new Exception("Book not found with BookId: " + query.getId()));

        com.librarysystem.commonservice.model.BookResponseCommonModel model = new com.librarysystem.commonservice.model.BookResponseCommonModel();
        BeanUtils.copyProperties(book, model);

        bucket.set(model, Duration.ofMinutes(10));

        return model;
    }

    @QueryHandler
    public BookPaginationResponseModel handle(GetAllBooksQuery query) {
        RMap<String, BookPaginationResponseModel> pagesMap = redissonClient.getMap(CACHE_BOOKS_PAGES_GROUP);
        String pageFieldKey = String.format("page:%d:size:%d:sort:%s:dir:%s",
                query.getPage(), query.getSize(), query.getSort(), query.getDirection());

        BookPaginationResponseModel cachedPagination = pagesMap.get(pageFieldKey);

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

        List<com.librarysystem.commonservice.model.BookResponseCommonModel> data = books.getContent()
                .stream()
                .map(book -> {
                    com.librarysystem.commonservice.model.BookResponseCommonModel model = new com.librarysystem.commonservice.model.BookResponseCommonModel();

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

        pagesMap.put(pageFieldKey, result);
        if (pagesMap.remainTimeToLive() < 0) {
            pagesMap.expire(Duration.ofMinutes(10));
        }

        return result;
    }
}
