package com.librarysystem.commonservice.services.csv;

import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public interface CsvService {

    <T> List<T> parse(MultipartFile file, Function<CSVRecord, T> mapper) throws IOException;
}
