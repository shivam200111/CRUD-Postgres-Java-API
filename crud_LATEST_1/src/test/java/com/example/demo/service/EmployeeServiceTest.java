package com.example.demo.service;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee.setAddress("123 Main St");
    }

    @Test
    void testCreateEmployee() {
        when(employeeRepository.save(employee)).thenReturn(employee);
        Employee createdEmployee = employeeService.createEmployee(employee);
        assertNotNull(createdEmployee);
        assertEquals("John Doe", createdEmployee.getName());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testGetAllEmployees() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Employee> employeePage = new PageImpl<>(Arrays.asList(employee));
        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        Page<Employee> result = employeeService.getAllEmployees(0, 2);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllEmployeesWithNegativeValues() {
        Exception exception = assertThrows(RuntimeException.class, () -> employeeService.getAllEmployees(-1, 2));
        assertEquals("Use positive value only", exception.getMessage());
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Optional<Employee> result = employeeService.getEmployeeById(1L);
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteEmployeeById() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);
        String result = employeeService.deleteEmployeeById(1L);
        assertEquals("Employee deleted successfully", result);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEmployeeById_NotFound() {
        when(employeeRepository.existsById(1L)).thenReturn(false);
        String result = employeeService.deleteEmployeeById(1L);
        assertEquals("No Employee found with the given ID", result);
    }

    @Test
    void testUpdateEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        String result = employeeService.updateEmployee(1L, "Jane Doe", "jane.doe@example.com", "456 Main St");
        assertEquals("Employee updated successfully", result);
        assertEquals("Jane Doe", employee.getName());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        String result = employeeService.updateEmployee(1L, "Jane Doe", "jane.doe@example.com", "456 Main St");
        assertEquals("No Employee found with the given ID", result);
    }
}
