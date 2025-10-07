package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock EmployeeRepository employeeRepository;
    EmployeeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmployeeServiceImpl(employeeRepository, new ModelMapper());
    }

    @Test
    void getByEmail_notFound() {
        when(employeeRepository.findByEmail("e@x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getEmployeeByEmail("e@x"));
    }

    @Test
    void addEmployee_success() {
        EmployeeDTO in = EmployeeDTO.builder().email("e@x").name("N").build();
        when(employeeRepository.existsByEmail("e@x")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setId(7L);
            return e;
        });
        EmployeeDTO out = service.addEmployee(in);
        assertEquals(7L, out.getId());
    }

    @Test
    void addEmployee_exists() {
        EmployeeDTO in = EmployeeDTO.builder().email("e@x").build();
        when(employeeRepository.existsByEmail("e@x")).thenReturn(true);
        assertThrows(AlreadyExistException.class, () -> service.addEmployee(in));
    }
}
