package com.librarysystem.bookservice.command.event;

import com.librarysystem.bookservice.command.data.Book;
import com.librarysystem.bookservice.command.data.BookRepository;
import com.librarysystem.commonservice.event.BookUpdateStatusEvent;
import org.axonframework.eventhandling.EventHandler;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.librarysystem.commonservice.services.cache.CacheConstant.CACHE_BOOKS_PAGES_GROUP;
import static com.librarysystem.commonservice.services.cache.CacheConstant.CACHE_BOOK_DETAIL_PREFIX;

@Component
public class BookEventHandler {

    private final BookRepository bookRepository;
    private final RedissonClient redissonClient;

    public BookEventHandler(BookRepository bookRepository, RedissonClient redissonClient) {
        this.bookRepository = bookRepository;
        this.redissonClient = redissonClient;
    }

    @EventHandler
    public void on(BookCreateEvent event) {
        Book book = new Book();
        BeanUtils.copyProperties(event, book);

        bookRepository.save(book);
        clearBooksPageCache();
    }

    @EventHandler
    public void on(BookUpdateEvent bookUpdateEvent){
        Optional<Book> oldBook = bookRepository.findById(bookUpdateEvent.getId());

        oldBook.ifPresent(book -> {
            book.setAuthor(bookUpdateEvent.getAuthor());
            book.setName(bookUpdateEvent.getName());
            book.setIsReady(bookUpdateEvent.getIsReady());
            bookRepository.save(book);

            clearBookDetailCache(bookUpdateEvent.getId());
            clearBooksPageCache();
        });
    }

    @EventHandler
    public void on(BookDeleteEvent event){
        Optional<Book> oldBook = bookRepository.findById(event.getId());
        oldBook.ifPresent(book -> {
            bookRepository.delete((book));

            clearBookDetailCache(event.getId());
            clearBooksPageCache();
        });
    }

    @EventHandler
    public void on(BookUpdateStatusEvent event) {
        Optional<Book> oldBook = bookRepository.findById(event.getBookId());
        oldBook.ifPresent(book -> {
            book.setIsReady(event.getIsReady());
            bookRepository.save(book);
        });
    }

    private void clearBookDetailCache(String bookId) {
        String cacheKey = CACHE_BOOK_DETAIL_PREFIX + bookId;
        redissonClient.getBucket(cacheKey).deleteAsync();
    }

    private void clearBooksPageCache() {
        redissonClient.getMap(CACHE_BOOKS_PAGES_GROUP).deleteAsync();
    }
}
