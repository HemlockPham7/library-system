package com.librarysystem.bookservice.query.model;

import com.librarysystem.commonservice.model.PaginationResponseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPaginationResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<com.librarysystem.commonservice.model.BookResponseCommonModel> data;
    private PaginationResponseModel pagination;
}
