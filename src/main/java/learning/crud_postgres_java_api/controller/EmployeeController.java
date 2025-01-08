package learning.crud_postgres_java_api.controller;

import jakarta.validation.Valid;
import learning.crud_postgres_java_api.db.Dbfunctions;
import learning.crud_postgres_java_api.model.Employee;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Data
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final Dbfunctions dbfunctions;

    @Autowired
    public EmployeeController(Dbfunctions dbfunctions) {
        this.dbfunctions = dbfunctions;
    }

    @PostMapping("/insert")
    public Employee createEmployee(@Valid @RequestBody Employee employee) {
        Connection conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
        dbfunctions.insertData(conn, employee.getKeycloakUserId(), employee.getName(), employee.getEmail(), employee.getAddress(), employee.getPassword());
        return employee;
    }

    @GetMapping("/read")
    public List<Employee> readEmployeeData() {
        Connection conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
        return dbfunctions.readData(conn, "Employee3");
    }

    @GetMapping("/read/{id}")
    public Employee readEmployee(@PathVariable long id) {
        Connection conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
        List<Employee> employees = dbfunctions.readDatafromid(conn, id, "Employee3");
        return employees.isEmpty() ? null : employees.get(0);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
        Connection conn = null;
        try {
            conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
            String message = dbfunctions.deleteData(conn, id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting employee: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable long id,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String address) {
        Connection conn = dbfunctions.connect_to_db("learn1", "postgres", "roots");
        if (conn == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to establish database connection");
        }
        try {
            List<Employee> employees = dbfunctions.readDatafromid(conn, id, "Employee3");  // Use Employee3 table
            if (employees.isEmpty()) {
                return ResponseEntity.ok("No data found with the given ID");
            }
            Employee employee = employees.get(0);
            if (name != null) {
                employee.setName(name);
            }
            if (email != null) {
                employee.setEmail(email);
            }
            if (address != null) {
                employee.setAddress(address);
            }
            String message = dbfunctions.updateData(conn, id, employee.getName(), employee.getEmail(), employee.getAddress());
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating employee: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }
        }
    }
}
