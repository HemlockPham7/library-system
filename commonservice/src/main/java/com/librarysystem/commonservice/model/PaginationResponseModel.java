package com.librarysystem.commonservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int page;
    private int size;
    private long total;
}
