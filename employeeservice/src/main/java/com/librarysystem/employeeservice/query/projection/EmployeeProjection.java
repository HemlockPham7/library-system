package com.librarysystem.employeeservice.query.projection;

import com.librarysystem.commonservice.model.PaginationResponseModel;
import com.librarysystem.employeeservice.command.data.Employee;
import com.librarysystem.employeeservice.command.data.EmployeeRepository;
import com.librarysystem.employeeservice.query.model.EmployeePaginationResponseModel;
import com.librarysystem.employeeservice.query.model.EmployeeResponseCommonModel;
import com.librarysystem.employeeservice.query.queries.GetAllEmployeeQuery;
import com.librarysystem.employeeservice.query.queries.GetDetailEmployeeQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeProjection {

    private final EmployeeRepository employeeRepository;

    public EmployeeProjection(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @QueryHandler
    public EmployeePaginationResponseModel handle(GetAllEmployeeQuery query) {
        Sort.Direction sortDirection = query.getDirection().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sortBy = Sort.by(sortDirection, query.getSort());
        Pageable pageable = PageRequest.of(
                query.getPage(),
                query.getSize(),
                sortBy
        );
        Page<Employee> employees = employeeRepository.findAllByIsDisciplined(query.getIsDisciplined(), pageable);

        List<EmployeeResponseCommonModel> data = employees.getContent()
                .stream()
                .map(employee -> {
                    EmployeeResponseCommonModel model = new EmployeeResponseCommonModel();

                    BeanUtils.copyProperties(employee, model);
                    return model;
                })
                .collect(Collectors.toList());
        PaginationResponseModel pagination = new PaginationResponseModel(
                employees.getNumber(),
                employees.getSize(),
                employees.getTotalElements()
        );

        EmployeePaginationResponseModel result = new EmployeePaginationResponseModel(data, pagination);

        return result;
    }

    @QueryHandler
    public EmployeeResponseCommonModel handle(GetDetailEmployeeQuery query) throws Exception{
        Employee employee = employeeRepository.findById(query.getId()).orElseThrow(() -> new Exception("Employee not found"));

        EmployeeResponseCommonModel model = new EmployeeResponseCommonModel();
        BeanUtils.copyProperties(employee,model);

        return model;
    }
}
