package learning.crud_postgres_java_api.controller;

import learning.crud_postgres_java_api.model.Employee;
import learning.crud_postgres_java_api.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Create Employee
    @PostMapping("/insert")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(createdEmployee);
    }

    // Read all Employees
    @GetMapping("/read")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // Read Employee by ID
    @GetMapping("/read/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete Employee by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable Long id) {
        String message = employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(message);
    }

    // Update Employee by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable Long id,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String address) {
        String message = employeeService.updateEmployee(id, name, email, address);
        return ResponseEntity.ok(message);
    }
}
