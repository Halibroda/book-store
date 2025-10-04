package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper mapper) {
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
            .map(e -> mapper.map(e, EmployeeDTO.class))
            .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + email));
        return mapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO dto) {
        Employee existing = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + email));
        Long id = existing.getId();
        mapper.map(dto, existing);
        existing.setId(id);
        Employee saved = employeeRepository.save(existing);
        return mapper.map(saved, EmployeeDTO.class);
    }

    @Override
    public void deleteEmployeeByEmail(String email) {
        if (!employeeRepository.existsByEmail(email)) {
            throw new NotFoundException("Employee not found: " + email);
        }
        employeeRepository.deleteByEmail(email);
    }

    @Override
    public EmployeeDTO addEmployee(EmployeeDTO dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistException("Employee already exists: " + dto.getEmail());
        }
        Employee entity = mapper.map(dto, Employee.class);
        Employee saved = employeeRepository.save(entity);
        return mapper.map(saved, EmployeeDTO.class);
    }
}
