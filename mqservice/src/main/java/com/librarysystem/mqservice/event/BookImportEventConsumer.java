package com.librarysystem.mqservice.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.librarysystem.commonservice.model.BookImportModel;

import java.util.List;

@Component
@Slf4j
public class BookImportEventConsumer {

    private final ObjectMapper objectMapper;

    public BookImportEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "booksImport", containerFactory = "kafkaListenerContainerFactory")
    public void bookImportListener(String message) {
        try {
            List<BookImportModel> books =
                    objectMapper.readValue(message, new TypeReference<List<BookImportModel>>() {});

            log.info("Received book import chunk. Size: {}", books.size());
        } catch (Exception e) {
            log.error("Failed to process book import message", e);

            throw new RuntimeException("Failed to deserialize book import message", e);
        }
    }
}
