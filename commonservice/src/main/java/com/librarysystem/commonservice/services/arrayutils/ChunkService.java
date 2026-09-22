package com.librarysystem.commonservice.services.arrayutils;

import java.util.List;

public interface ChunkService {

    <T> List<List<T>> chunk(List<T> source, int chunkSize);
}