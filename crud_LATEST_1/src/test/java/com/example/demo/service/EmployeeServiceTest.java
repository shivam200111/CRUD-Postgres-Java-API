package com.example.demo.service;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;


    @BeforeEach
    void setUp() {
        employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("John Doe");
        employee1.setEmail("john.doe@example.com");
        employee1.setAddress("123 Main St");

        employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Jane Doe");
        employee2.setEmail("jane@example.com");
        employee2.setAddress("456 Avenue");

        employee3 = new Employee();
        employee3.setId(3L);
        employee3.setName("Doe");
        employee3.setEmail("Doe@example.com");
        employee3.setAddress("Doe abc");
    }

    /** Test Create Employee **/
    @Test
    void createEmployee_ShouldSaveAndReturnEmployee() {
        when(employeeRepository.save(employee1)).thenReturn(employee1);

        Employee createdEmployee = employeeService.createEmployee(employee1);

        assertNotNull(createdEmployee);
        assertEquals("John Doe", createdEmployee.getName());
        verify(employeeRepository, times(1)).save(employee1);
    }

    /** Test Get All Employees **/
    @Test
    void getAllEmployees_ShouldReturnPagedEmployees() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        Page<Employee> employeePage = new PageImpl<>(employees);
        Pageable pageable = PageRequest.of(0, 2);

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);

        Page<Employee> result = employeeService.getAllEmployees(0, 2);

        assertEquals(2, result.getTotalElements());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    /** Test Get All Employees - Exception Handling **/
    @Test
    void getAllEmployees_ShouldThrowException_WhenNegativeValuesUsed() {
        assertThrows(RuntimeException.class, () -> employeeService.getAllEmployees(-1, 5));
        assertThrows(RuntimeException.class, () -> employeeService.getAllEmployees(2, -5));
    }

    /** Test Get Employee By ID **/
    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        Optional<Employee> foundEmployee = employeeService.getEmployeeById(1L);

        assertTrue(foundEmployee.isPresent());
        assertEquals("John Doe", foundEmployee.get().getName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_ShouldReturnEmpty_WhenEmployeeNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.getEmployeeById(99L);

        assertFalse(result.isPresent());
    }

    /** Test Delete Employee **/
    @Test
    void deleteEmployeeById_ShouldDeleteEmployee_WhenEmployeeExists() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);

        String result = employeeService.deleteEmployeeById(1L);

        assertEquals("Employee deleted successfully", result);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEmployeeById_ShouldReturnNotFoundMessage_WhenEmployeeDoesNotExist() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        String result = employeeService.deleteEmployeeById(99L);

        assertEquals("No Employee found with the given ID", result);
    }

    /** Test Update Employee **/
    @Test
    void updateEmployee_ShouldUpdateEmployee_WhenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        String result = employeeService.updateEmployee(1L, "New Name", "newemail@example.com", "New Address");

        assertEquals("Employee updated successfully", result);
        assertEquals("New Name", employee1.getName());
        assertEquals("newemail@example.com", employee1.getEmail());
        assertEquals("New Address", employee1.getAddress());
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    void updateEmployee_ShouldReturnNotFoundMessage_WhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        String result = employeeService.updateEmployee(99L, "New Name", "newemail@example.com", "New Address");

        assertEquals("No Employee found with the given ID", result);
    }


    @Test
    void updateEmployee_ShouldUpdateOnlyNonNullFields() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        employeeService.updateEmployee(1L, "New Name", null, "New Address");
        assertEquals("New Name", employee1.getName());
        assertEquals("john.doe@example.com", employee1.getEmail()); // Should remain unchanged
        assertEquals("New Address", employee1.getAddress());

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee2));
        employeeService.updateEmployee(2L, null, "joh.doe@example.com", "New Address");
        assertEquals("Jane Doe", employee2.getName());
        assertEquals("joh.doe@example.com", employee2.getEmail()); // Should remain unchanged
        assertEquals("New Address", employee2.getAddress());

        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee3));
        employeeService.updateEmployee(3L, "sk", "joh.doe@example.com", null);
        assertEquals("sk", employee3.getName());
        assertEquals("joh.doe@example.com", employee3.getEmail()); // Should remain unchanged
        assertEquals("Doe abc", employee3.getAddress());

    }
}
