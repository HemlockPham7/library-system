package com.librarysystem.bookservice.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponseModel {
    private int page;
    private int size;
    private long total;
}
