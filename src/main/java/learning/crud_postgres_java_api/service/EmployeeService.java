package learning.crud_postgres_java_api.service;

import learning.crud_postgres_java_api.model.Employee;
import learning.crud_postgres_java_api.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Create Employee
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // Get All Employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get Employee by ID
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    // Delete Employee by ID
    public String deleteEmployeeById(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return "Employee deleted successfully";
        }
        return "No Employee found with the given ID";
    }

    // Update Employee by ID
    public String updateEmployee(Long id, String name, String email, String address) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            if (name != null) employee.setName(name);
            if (email != null) employee.setEmail(email);
            if (address != null) employee.setAddress(address);
            employeeRepository.save(employee);
            return "Employee updated successfully";
        }
        return "No Employee found with the given ID";
    }
}
