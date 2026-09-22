package com.librarysystem.commonservice.services.arrayutils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChunkServiceImpl implements ChunkService {

    @Override
    public <T> List<List<T>> chunk(List<T> source, int chunkSize) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than 0");
        }

        List<List<T>> chunks = new ArrayList<>();

        for (int i = 0; i < source.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, source.size());

            chunks.add(new ArrayList<>(source.subList(i, end)));
        }

        return chunks;
    }
}
