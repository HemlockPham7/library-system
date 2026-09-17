package com.librarysystem.bookservice.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPaginationResponseModel {
    private List<BookResponseCommonModel> data;

    private PaginationResponseModel pagination;
}
