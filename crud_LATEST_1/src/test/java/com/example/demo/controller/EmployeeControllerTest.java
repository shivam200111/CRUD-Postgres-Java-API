package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    public void testCreateEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee.setAddress("123 Main St");



        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/employees/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.address").value("123 Main St"));

        verify(employeeService, times(1)).createEmployee(any(Employee.class));
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        Employee emp1 = new Employee();
        emp1.setId(1L);
        emp1.setName("John Doe");
        emp1.setEmail("john.doe@example.com");
        emp1.setAddress("123 Main St");

        Employee emp2 = new Employee();
        emp2.setId(2L);
        emp2.setName("Jane Doe");
        emp2.setEmail("jane.doe@example.com");
        emp2.setAddress("456 Elm St");

        Page<Employee> employeePage = new PageImpl<>(List.of(emp1, emp2), PageRequest.of(0, 5), 2);
        when(employeeService.getAllEmployees(0, 5)).thenReturn(employeePage);

        mockMvc.perform(get("/employees/read")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].name").value("Jane Doe"));

        verify(employeeService, times(1)).getAllEmployees(0, 5);
    }


    @Test
    public void testGetEmployeeById() throws Exception {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");

        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/employees/read/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    public void testGetEmployeeByIdNotFound() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/employees/read/1"))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    public void testDeleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployeeById(1L)).thenReturn("Employee deleted successfully");

        mockMvc.perform(delete("/employees/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee deleted successfully"));

        verify(employeeService, times(1)).deleteEmployeeById(1L);
    }

    @Test
    public void testUpdateEmployee() throws Exception {
        when(employeeService.updateEmployee(1L, "Jane Doe", "jane.doe@example.com", "456 Elm St"))
                .thenReturn("Employee updated successfully");

        mockMvc.perform(put("/employees/update/1")
                .param("name", "Jane Doe")
                .param("email", "jane.doe@example.com")
                .param("address", "456 Elm St"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee updated successfully"));

        verify(employeeService, times(1)).updateEmployee(1L, "Jane Doe", "jane.doe@example.com", "456 Elm St");
    }
}