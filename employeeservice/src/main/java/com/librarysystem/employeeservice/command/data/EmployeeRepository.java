package com.librarysystem.employeeservice.command.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Page<Employee> findAllByIsDisciplined(Boolean isDisciplined, Pageable pageable);
}
