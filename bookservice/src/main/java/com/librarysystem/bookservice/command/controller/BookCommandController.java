package com.librarysystem.bookservice.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarysystem.bookservice.command.command.CreateBookCommand;
import com.librarysystem.bookservice.command.command.DeleteBookCommand;
import com.librarysystem.bookservice.command.command.UpdateBookCommand;
import com.librarysystem.commonservice.model.BookImportModel;
import com.librarysystem.bookservice.command.model.BookRequestModel;
import com.librarysystem.bookservice.command.model.MailRequestModel;
import com.librarysystem.commonservice.services.arrayutils.ChunkService;
import com.librarysystem.commonservice.services.csv.CsvService;
import com.librarysystem.commonservice.services.mq.KafkaService;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class BookCommandController {

    private static final int CHUNK_SIZE = 20;
    private static final String BOOK_IMPORT_TOPIC = "booksImport";

    private final CommandGateway commandGateway;
    private final KafkaService kafkaService;
    private final CsvService csvService;
    private final ChunkService chunkService;
    private final ObjectMapper objectMapper;

    public BookCommandController(CommandGateway commandGateway, KafkaService kafkaService, ObjectMapper objectMapper, CsvService csvService, ChunkService chunkService, ObjectMapper objectMapper1) {
        this.commandGateway = commandGateway;
        this.kafkaService = kafkaService;
        this.csvService = csvService;
        this.chunkService = chunkService;
        this.objectMapper = objectMapper1;
    }

    @PostMapping
    public String addBook(@Valid @RequestBody BookRequestModel model) {
        CreateBookCommand command = CreateBookCommand.builder()
                .id(UUID.randomUUID().toString())
                .name(model.getName())
                .author(model.getAuthor())
                .isReady(Boolean.TRUE)
                .build();
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{bookId}")
    public String updateBook(@RequestBody BookRequestModel model, @PathVariable String bookId) {
        UpdateBookCommand command = UpdateBookCommand.builder()
                .id(bookId)
                .name(model.getName())
                .author(model.getAuthor())
                .isReady(model.getIsReady())
                .build();
        return commandGateway.sendAndWait(command);
    }

    @DeleteMapping("/{bookId}")
    public String deleteBook(@PathVariable String bookId) {
        DeleteBookCommand command = DeleteBookCommand.builder()
                .id(bookId)
                .build();
        return commandGateway.sendAndWait(command);
    }

    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String importBooks(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }

        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }

        List<BookImportModel> books = csvService.parse(file, record -> new BookImportModel(
                        record.get("name"),
                        record.get("author"),
                        Boolean.parseBoolean(record.get("isReady"))
                ));

        List<List<BookImportModel>> bookChunks = chunkService.chunk(books, CHUNK_SIZE);

        for (List<BookImportModel> bookChunk : bookChunks) {
            String message = objectMapper.writeValueAsString(bookChunk);
            kafkaService.sendMessage(BOOK_IMPORT_TOPIC, message);
        }

        return String.format("Book import started. Total books: %d, total chunks: %d", books.size(), bookChunks.size());
    }

    @PostMapping("/kafka-health")
    public void mqHealthCheck(@RequestBody String message) {
        kafkaService.sendMessage("health-check", message);
    }

    @PostMapping("/mail-health")
    public void mailSenderHealthCheck(@RequestBody MailRequestModel model) {
        kafkaService.sendMessage("emailBasic", model.getEmail());
    }

    @PostMapping("/mail-send")
    public void mailSenderWithTemplate(@RequestBody MailRequestModel model) {
        kafkaService.sendMessage("emailTemplate", model.getEmail());
    }
}
