package com.librarysystem.employeeservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllEmployeeQuery {
    private int page;
    private int size;
    private String sort;
    private String direction;
    private Boolean isDisciplined;
}
