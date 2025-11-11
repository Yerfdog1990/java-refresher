
---

# 🧾 Lesson Notes: DTO, DAO, and CRUD Operations in Spring Boot (with ModelMapper)

---

## **1. Overview**

In a **Spring Boot + JPA** application:

* **DAO (Data Access Object)** → interacts with the database (Repository layer).
* **DTO (Data Transfer Object)** → carries data between client and server layers.
* **Entity** → maps directly to a database table.
* **Service layer** → connects DAO and DTO using **ModelMapper** to convert between them.
* **Controller** → exposes REST API endpoints to handle client requests.

---

## **2. Data Flow Summary**

### ✅ When Saving Data (Client → Database)

`JSON → DTO → Entity → Repository.save() → Database`

### ✅ When Retrieving Data (Database → Client)

`Repository.findAll() → Entity → DTO → JSON`

### ✅ When Updating Data

`JSON (DTO) → Entity (via ModelMapper) → Repository.save()`

### ✅ When Deleting Data

`Repository.deleteById(id)`

---

## **3. Complete CRUD Example: Employee Management**

---

### 🧩 3.1. pom.xml — Add Dependencies

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- H2 Database (for testing) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- ModelMapper -->
    <dependency>
        <groupId>org.modelmapper</groupId>
        <artifactId>modelmapper</artifactId>
        <version>3.2.0</version>
    </dependency>

    <!-- Spring Boot Starter Test (optional) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

### 🧩 3.2. application.properties

```properties
spring.datasource.url=jdbc:h2:mem:employeesdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
logging.level.org.hibernate.SQL=DEBUG
```

---

### 🧩 3.3. ModelMapper Configuration

```java
// AppConfig.java
package com.example.demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```

---

### 🧩 3.4. Entity Class (Database Representation)

```java
// Employee.java
package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double salary;

    // Constructors
    public Employee() {}
    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
```

---

### 🧩 3.5. DTO Class (Data Transfer Object)

```java
// EmployeeDTO.java
package com.example.demo.dto;

public class EmployeeDTO {
    private Long id;
    private String name;
    private double salary;

    // Constructors
    public EmployeeDTO() {}
    public EmployeeDTO(Long id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
```

---

### 🧩 3.6. DAO (Repository Layer)

```java
// EmployeeRepository.java
package com.example.demo.repository;

import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
```

---

### 🧩 3.7. Service Layer (Business Logic + DTO ↔ Entity Mapping)

```java
// EmployeeService.java
package com.example.demo.service;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public EmployeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    // 🔹 CREATE
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        Employee saved = employeeRepository.save(employee);
        return modelMapper.map(saved, EmployeeDTO.class);
    }

    // 🔹 READ (all)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(emp -> modelMapper.map(emp, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    // 🔹 READ (by ID)
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id " + id));
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    // 🔹 UPDATE
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            throw new RuntimeException("Employee not found with id " + id);
        }

        Employee employee = optionalEmployee.get();
        // Map fields from DTO → existing entity
        modelMapper.map(employeeDTO, employee);

        Employee updated = employeeRepository.save(employee);
        return modelMapper.map(updated, EmployeeDTO.class);
    }

    // 🔹 DELETE
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id " + id);
        }
        employeeRepository.deleteById(id);
    }
}
```

---

### 🧩 3.8. Controller Layer (REST Endpoints)

```java
// EmployeeController.java
package com.example.demo.controller;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO saved = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.ok(saved);
    }

    // ✅ READ ALL
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // ✅ READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(updated);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee with ID " + id + " deleted successfully.");
    }
}
```

---

## 🧪 **4. Example API Usage**

### ➤ Create Employee (POST)

**Request:**

```http
POST /api/employees
Content-Type: application/json

{
  "name": "Alice",
  "salary": 55000
}
```

**Response:**

```json
{
  "id": 1,
  "name": "Alice",
  "salary": 55000
}
```

---

### ➤ Get All Employees (GET)

```http
GET /api/employees
```

**Response:**

```json
[
  { "id": 1, "name": "Alice", "salary": 55000 },
  { "id": 2, "name": "Bob", "salary": 60000 }
]
```

---

### ➤ Get Employee by ID (GET)

```http
GET /api/employees/1
```

**Response:**

```json
{
  "id": 1,
  "name": "Alice",
  "salary": 55000
}
```

---

### ➤ Update Employee (PUT)

```http
PUT /api/employees/1
Content-Type: application/json

{
  "name": "Alice Johnson",
  "salary": 58000
}
```

**Response:**

```json
{
  "id": 1,
  "name": "Alice Johnson",
  "salary": 58000
}
```

---

### ➤ Delete Employee (DELETE)

```http
DELETE /api/employees/1
```

**Response:**

```
Employee with ID 1 deleted successfully.
```

---

## ✅ **5. Summary: How Data Moves**

| Operation  | Direction         | Flow                              | Description         |
| ---------- | ----------------- | --------------------------------- | ------------------- |
| **Create** | DTO → Entity → DB | Controller → Service → Repository | New record inserted |
| **Read**   | DB → Entity → DTO | Repository → Service → Controller | Data fetched as DTO |
| **Update** | DTO → Entity → DB | Controller → Service → Repository | Record modified     |
| **Delete** | ID → Repository   | Controller → Service → Repository | Record removed      |

---

## 🧠 **6. Key Takeaways**

* **DAO (Repository)** handles all database access.
* **DTO** carries data to/from the client without exposing internal entity structure.
* **Service Layer** converts between DTO and Entity using **ModelMapper**.
* **Controller** is responsible for handling REST API requests and responses.
* **ModelMapper** eliminates manual field mapping, making your code cleaner and maintainable.

---

