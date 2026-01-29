
---

# The Basics of Input Validation

---

## 1. Introduction to Input Validation

Input validation is the **systematic process of checking incoming data** to ensure it satisfies **syntactic, structural, semantic, and security constraints** *before* the data is processed by the application.

In a RESTful system, **every request is an external input**. Even requests coming from:

* your own frontend
* trusted internal services
* automated systems

must be treated as **potentially invalid**.

> **Golden rule:**
> *Never trust input — validate it.*

---

## 2. Input Validation in the Context of REST Architecture

REST APIs are:

* stateless
* resource-oriented
* exposed over HTTP
* consumed by unknown clients

This makes validation **mandatory**, not optional.

### 2.1 Where Validation Fits in the Request Lifecycle

```
HTTP Request
   ↓
JSON Parsing (Jackson)
   ↓
DTO Binding
   ↓
Input Validation
   ↓
Controller Logic
   ↓
Service Layer
   ↓
Persistence Layer
```

If validation fails:

* the request is rejected early
* no business logic runs
* the database remains untouched

---

## 3. Goals of Input Validation

Input validation serves **four main goals**:

### 3.1 Data Integrity

Prevents:

* null values where data is required
* out-of-range numbers
* truncated strings
* inconsistent states

### 3.2 Application Stability

Prevents:

* `NullPointerException`
* `ConstraintViolationException`
* parsing and conversion errors
* unexpected crashes

### 3.3 Security

Blocks:

* SQL injection
* XSS payloads
* oversized payload attacks
* malformed JSON exploits

### 3.4 API Usability

Provides:

* clear feedback to clients
* predictable HTTP status codes
* meaningful error messages

---

## 4. Categories of Input Validation

---

## 4.1 Structural Validation

Structural validation ensures that the **shape of the input** matches expectations.

Examples:

* required fields exist
* JSON structure is valid
* correct data types are used

### Example

Expected DTO:

```json
{
  "code": "CMP-2026",
  "name": "Summer Campaign"
}
```

❌ Invalid:

```json
{
  "code": ["CMP-2026"],
  "name": 123
}
```

Structural validation happens during:

* JSON deserialization
* DTO binding

---

## 4.2 Syntactic Validation

Syntactic validation checks **format and constraints**.

Examples:

* string length
* allowed characters
* numeric ranges
* date formats

```java
@Size(min = 3, max = 20)
@Pattern(regexp = "[A-Z0-9-]+")
private String code;
```

---

## 4.3 Semantic (Business) Validation

Semantic validation ensures that input **makes sense in the business domain**.

Examples:

* campaign code must be unique
* end date must be after start date
* campaign cannot be closed if tasks are active

📌 This type of validation:

* requires database access
* depends on business rules
* belongs in the **service layer**

---

## 4.4 Cross-Field Validation

Some rules involve **multiple fields**.

Example:

```text
startDate < endDate
```

This cannot be expressed using simple annotations on a single field.

Solution:

* class-level validation
* custom validators

---

## 5. Validation Layers in a Spring Application

| Layer      | Responsibility         |
| ---------- | ---------------------- |
| DTO        | Structural + syntactic |
| Controller | Trigger validation     |
| Service    | Business rules         |
| Repository | Database constraints   |

📌 Validation should be **redundant by design**.

---

## 6. Bean Validation in Spring (Jakarta Validation)

Spring integrates **Jakarta Bean Validation (JSR 380)** automatically.

---

## 6.1 Dependency

Spring Boot includes validation by default:

```xml
spring-boot-starter-validation
```

---

## 6.2 Common Validation Annotations (In Depth)

### Null & Blank Checks

| Annotation  | Meaning                             |
| ----------- | ----------------------------------- |
| `@NotNull`  | Value must not be null              |
| `@NotEmpty` | Not null and not empty              |
| `@NotBlank` | Not null, not empty, not whitespace |

---

### Size Constraints

```java
@Size(min = 5, max = 100)
private String name;
```

Applies to:

* Strings
* Collections
* Arrays

---

### Numeric Constraints

```java
@Positive
@Max(100)
private int priority;
```

---

### Pattern Constraints

```java
@Pattern(
  regexp = "[A-Z]{3}-\\d{4}",
  message = "Invalid campaign code format"
)
```

---

## 7. DTO-Based Validation (Best Practice)

Entities represent **database structure**.
DTOs represent **API contracts**.

📌 **Validation belongs on DTOs.**

### Example

```java
public record CampaignDto(

    Long id,

    @NotBlank(message = "Code is mandatory")
    @Size(max = 20)
    String code,

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    String name,

    @Size(max = 500)
    String description

) {}
```

---

## 8. Triggering Validation Explicitly

Validation runs only when explicitly triggered.

```java
@PostMapping
public CampaignDto create(
        @Valid @RequestBody CampaignDto dto) {
    return service.create(dto);
}
```

Without `@Valid`:

* annotations are ignored
* invalid data flows through

---

## 9. Validation Failure Flow

```
Invalid Input
   ↓
Bean Validation Fails
   ↓
MethodArgumentNotValidException
   ↓
Exception Handler
   ↓
400 BAD REQUEST
```

---

## 10. Validation Error Handling Strategy

### 10.1 Why Custom Error Responses Matter

Default Spring responses:

* are verbose
* are inconsistent
* expose internal details

We want:

* consistency
* clarity
* client-friendly messages

---

### 10.2 Global Validation Error Handler

```java
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handle(MethodArgumentNotValidException ex) {

        ProblemDetail pd =
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        pd.setTitle("Input Validation Failed");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
            );

        pd.setProperty("errors", errors);
        return pd;
    }
}
```

---

## 11. Example Validation Error Response

```json
{
  "title": "Input Validation Failed",
  "status": 400,
  "errors": {
    "code": "Code is mandatory",
    "name": "Name is mandatory"
  }
}
```

---

## 12. Custom Validation (Advanced)

---

### 12.1 Custom Annotation

```java
@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {
    String message() default "Invalid date range";
}
```

---

### 12.2 Validator Implementation

```java
public class DateRangeValidator
    implements ConstraintValidator<ValidDateRange, CampaignDto> {

    public boolean isValid(CampaignDto dto, ConstraintValidatorContext ctx) {
        return dto.startDate().isBefore(dto.endDate());
    }
}
```

---

## 13. Database Constraints vs Validation

| Validation     | Database     |
| -------------- | ------------ |
| User-friendly  | Technical    |
| Early feedback | Late failure |
| API-focused    | Data-focused |

📌 Use **both**, not one or the other.

---

## 14. Common Validation Pitfalls

❌ Validating entities

❌ Skipping validation on update endpoints

❌ Overusing exceptions

❌ Mixing business logic into DTOs

❌ Returning vague error messages

---

## 15. Best Practices Summary

✅ Validate early

✅ Fail fast

✅ Keep error messages human-readable

✅ Separate concerns

✅ Combine validation and exception handling

---

## 16. Mini Reflection Exercise 🧠

> Why should validation happen *before* service logic and *not* inside repositories?

---

# Mini Project: Student Management API

### Demonstrating Input Validation with CRUD Operations

## 🎯 Project Objective

Build a simple **Student Management REST API** that allows clients to:

* **Create** a student (with validation)
* **Read** students (single & list)
* **Update** a student (with validation)
* **Delete** a student

At every stage, we enforce **input validation** to ensure data integrity and security.

---

## 🧱 Project Overview

### Entity: Student

Each student record contains:

| Field | Type    | Validation Rules             |
| ----- | ------- | ---------------------------- |
| id    | Long    | Auto-generated               |
| name  | String  | Required, 2–50 characters    |
| email | String  | Required, valid email format |
| age   | Integer | Between 10 and 100           |

---

## 📁 Project Structure

```
student-api/
 ├── controller/
 │    └── StudentController.java
 ├── dto/
 │    └── StudentDto.java
 ├── model/
 │    └── Student.java
 ├── service/
 │    └── StudentService.java
 └── StudentApiApplication.java
```

---

## 🧩 Step 1: Student Entity

```java
public class Student {

    private Long id;
    private String name;
    private String email;
    private Integer age;

    // getters and setters
}
```

---

## 🧩 Step 2: DTO with Input Validation

👉 **This is where validation happens**

```java
import jakarta.validation.constraints.*;

public class StudentDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 100, message = "Age must not exceed 100")
    private Integer age;

    // getters and setters
}
```

🔐 **Why use a DTO?**

* Prevents invalid data from reaching your model
* Separates API validation from internal logic
* Improves security and maintainability

---

## 🧩 Step 3: Service Layer (In-Memory Storage)

```java
import java.util.*;

public class StudentService {

    private final Map<Long, Student> database = new HashMap<>();
    private Long idCounter = 1L;

    public Student create(StudentDto dto) {
        Student student = new Student();
        student.setId(idCounter++);
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());

        database.put(student.getId(), student);
        return student;
    }

    public List<Student> findAll() {
        return new ArrayList<>(database.values());
    }

    public Student findOne(Long id) {
        return database.get(id);
    }

    public Student update(Long id, StudentDto dto) {
        Student student = database.get(id);
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());
        return student;
    }

    public void delete(Long id) {
        database.remove(id);
    }
}
```

---

## 🧩 Step 4: Controller with CRUD + Validation

```java
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service = new StudentService();

    // CREATE
    @PostMapping
    public Student createStudent(@Valid @RequestBody StudentDto dto) {
        return service.create(dto);
    }

    // READ ALL
    @GetMapping
    public List<Student> getAllStudents() {
        return service.findAll();
    }

    // READ ONE
    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return service.findOne(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Student updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentDto dto) {
        return service.update(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        service.delete(id);
    }
}
```

---

## 🚦 How Validation Works in This Project

| Operation          | Validation Trigger         |
| ------------------ | -------------------------- |
| POST /students     | Validates name, email, age |
| PUT /students/{id} | Validates updated values   |
| GET                | No validation needed       |
| DELETE             | No body → no validation    |

If validation fails, Spring automatically returns:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Name is required"
}
```

---

## 🧪 Example Invalid Requests

### ❌ Missing name

```json
{
  "email": "student@example.com",
  "age": 18
}
```

### ❌ Invalid email

```json
{
  "name": "John",
  "email": "not-an-email",
  "age": 18
}
```

### ❌ Age out of range

```json
{
  "name": "John",
  "email": "john@example.com",
  "age": 5
}
```

---

## 🧠 Learning Outcomes

By completing this mini project, students learn:

* How CRUD operations map to HTTP verbs
* Why input validation is critical
* How to use annotations like `@NotBlank`, `@Email`, `@Min`
* How validation protects APIs from bad data
* Real-world API design best practices

---

## 🔧 Extension Ideas (Optional Challenges)

* Add **custom validation** (e.g., email domain must be `@school.com`)
* Handle **validation errors globally** with `@ControllerAdvice`
* Persist data using **JPA + database**
* Add **PATCH** for partial updates
* Add **frontend form validation**

---

