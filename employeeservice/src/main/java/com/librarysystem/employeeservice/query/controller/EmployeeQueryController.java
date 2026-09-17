package com.librarysystem.employeeservice.query.controller;

import com.librarysystem.employeeservice.query.model.EmployeePaginationResponseModel;
import com.librarysystem.employeeservice.query.model.EmployeeResponseCommonModel;
import com.librarysystem.employeeservice.query.queries.GetAllEmployeeQuery;
import com.librarysystem.employeeservice.query.queries.GetDetailEmployeeQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@Slf4j
public class EmployeeQueryController {

    private final QueryGateway queryGateway;

    public EmployeeQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public EmployeePaginationResponseModel getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false, defaultValue = "false") Boolean isDisciplined
    ) {

        GetAllEmployeeQuery query = GetAllEmployeeQuery.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .direction(direction)
                .isDisciplined(isDisciplined)
                .build();
        return queryGateway.query(query, ResponseTypes.instanceOf(EmployeePaginationResponseModel.class)).join();
    }

    @GetMapping("/{employeeId}")
    public EmployeeResponseCommonModel getDetailEmployee(@PathVariable String employeeId){
        GetDetailEmployeeQuery query = GetDetailEmployeeQuery.builder()
                .id(employeeId)
                .build();
        return queryGateway.query(query, ResponseTypes.instanceOf(EmployeeResponseCommonModel.class)).join();
    }
}
