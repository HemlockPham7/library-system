package com.librarysystem.bookservice.event;

import com.librarysystem.bookservice.command.data.Book;
import com.librarysystem.bookservice.command.data.BookRepository;
import com.librarysystem.bookservice.query.model.BookImportModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class BookImportEventConsumer {

    private final ObjectMapper objectMapper;
    private final BookRepository bookRepository;

    public BookImportEventConsumer(ObjectMapper objectMapper, BookRepository bookRepository) {
        this.objectMapper = objectMapper;
        this.bookRepository = bookRepository;
    }

    @KafkaListener(topics = "booksImport", containerFactory = "kafkaListenerContainerFactory")
    public void bookImportListener(String message) {
        try {
            List<BookImportModel> books =
                    objectMapper.readValue(message, new TypeReference<List<BookImportModel>>() {});

            List<Book> entities = books.stream()
                    .map(book -> {
                        Book entity = new Book();

                        entity.setId(UUID.randomUUID().toString());
                        entity.setName(book.getName());
                        entity.setAuthor(book.getAuthor());
                        entity.setIsReady(book.getIsReady());

                        return entity;
                    })
                    .toList();

            bookRepository.saveAll(entities);

            log.info("Saved book import chunk successfully. Size: {}", entities.size());
        } catch (Exception e) {
            log.error("Failed to process book import message", e);

            throw new RuntimeException("Failed to deserialize book import message", e);
        }
    }
}
