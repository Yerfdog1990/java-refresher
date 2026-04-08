
---

# 📘 Quick Guide to MapStruct

---

## 1. Overview

**MapStruct** is a **Java Bean mapper** used to automatically map data between two Java objects.

* It generates **mapping implementations at compile time**
* Developers only define **interfaces**, not implementations
* Eliminates repetitive mapping code

👉 In simple terms:
**You define the mapping → MapStruct writes the code for you**

---

## 2. MapStruct and the DTO Pattern

### 🔹 The Problem

In most applications:

* We frequently convert:

    * **Entity ↔ DTO**
    * **POJO ↔ POJO**

This leads to:

* Boilerplate code
* Repetitive logic
* High chance of errors

---

### 🔹 The Solution

MapStruct:

* Automatically generates mapping logic
* Reduces manual effort
* Improves maintainability

👉 Especially useful in:

* Layered architectures
* API-driven applications

---

## 3. Maven Configuration

### 🔹 Dependency

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>
```

---

### 🔹 Annotation Processor

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.6.3</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

👉 This processor:

* Generates mapping code during build (`mvn clean install`)

---

## 4. Basic Mapping

---

### 🔹 4.1 Simple POJOs

```java
public class SimpleSource {
    private String name;
    private String description;
}

public class SimpleDestination {
    private String name;
    private String description;
}
```

---

### 🔹 4.2 Mapper Interface

```java
@Mapper
public interface SimpleSourceDestinationMapper {

    SimpleDestination sourceToDestination(SimpleSource source);

    SimpleSource destinationToSource(SimpleDestination destination);
}
```

✔ No implementation needed

✔ MapStruct generates it automatically

---

### 🔹 4.3 Generated Implementation

MapStruct generates:

```java
public class SimpleSourceDestinationMapperImpl 
        implements SimpleSourceDestinationMapper {

    public SimpleDestination sourceToDestination(SimpleSource source) {
        if (source == null) return null;

        SimpleDestination dest = new SimpleDestination();
        dest.setName(source.getName());
        dest.setDescription(source.getDescription());
        return dest;
    }
}
```

📍 Location:

```
/target/generated-sources/annotations/
```

---

### 🔹 4.4 Testing

```java
@Test
public void givenSourceToDestination_whenMaps_thenCorrect() {
    SimpleSource source = new SimpleSource();
    source.setName("SourceName");

    SimpleDestination dest = mapper.sourceToDestination(source);

    assertEquals(source.getName(), dest.getName());
}
```

---

## 5. Mapping with Dependency Injection

---

### 🔹 Default Way

```java
SimpleMapper mapper = Mappers.getMapper(SimpleMapper.class);
```

❌ Not ideal for Spring apps

---

### 🔹 Spring Integration (Best Practice)

```java
@Mapper(componentModel = "spring")
public interface SimpleMapper {
}
```

✔ Registers mapper as a **Spring Bean**

✔ Enables `@Autowired` injection

---

### 🔹 Injecting Services into Mapper

Use **abstract class**:

```java
@Mapper(componentModel = "spring")
public abstract class SimpleMapper {

    @Autowired
    protected SimpleService service;

    @Mapping(target = "name", 
      expression = "java(service.enrichName(source.getName()))")
    public abstract SimpleDestination sourceToDestination(SimpleSource source);
}
```

⚠️ Important:

* Injected fields must NOT be private

---

## 6. Mapping Fields with Different Names

---

### 🔹 Example POJOs

```java
public class Employee {
    private int id;
    private String name;
}

public class EmployeeDTO {
    private int employeeId;
    private String employeeName;
}
```

---

### 🔹 Mapper Configuration

```java
@Mapper
public interface EmployeeMapper {

    @Mapping(target = "employeeId", source = "entity.id")
    @Mapping(target = "employeeName", source = "entity.name")
    EmployeeDTO employeeToEmployeeDTO(Employee entity);

    @Mapping(target = "id", source = "dto.employeeId")
    @Mapping(target = "name", source = "dto.employeeName")
    Employee employeeDTOtoEmployee(EmployeeDTO dto);
}
```

✔ Handles mismatched field names

✔ Supports dot notation (`entity.id`)

---

## 7. Mapping Nested (Child) Objects

---

### 🔹 Example

```java
public class Employee {
    private Division division;
}

public class EmployeeDTO {
    private DivisionDTO division;
}
```

---

### 🔹 Mapper

```java
DivisionDTO divisionToDivisionDTO(Division entity);

Division divisionDTOtoDivision(DivisionDTO dto);
```

✔ MapStruct:

* Detects and uses these methods automatically
* Supports nested object mapping

---

## 8. Mapping with Type Conversion

---

### 🔹 Example: Date Conversion

```java
@Mapping(target="employeeStartDt", 
         source="entity.startDt",
         dateFormat="dd-MM-yyyy HH:mm:ss")
```

✔ Converts:

* `Date → String`
* `String → Date`

---

### 🔹 Test Example

```java
assertEquals(
    format.parse(dto.getEmployeeStartDt()).toString(),
    entity.getStartDt().toString()
);
```

---

## 9. Mapping with Abstract Class (Custom Logic)

---

### 🔹 Use Case

When:

* Mapping logic is complex
* Requires transformation

---

### 🔹 Example

```java
@Mapper
abstract class TransactionMapper {

    public TransactionDTO toTransactionDTO(Transaction t) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTotalInCents(
            t.getTotal().multiply(new BigDecimal("100")).longValue()
        );
        return dto;
    }

    public abstract List<TransactionDTO> toTransactionDTO(
        Collection<Transaction> transactions);
}
```

✔ Custom logic + auto-generated methods

✔ MapStruct reuses your custom method

---

## 10. Before & After Mapping Hooks

---

### 🔹 Annotations

* `@BeforeMapping`
* `@AfterMapping`
* `@MappingTarget`

---

### 🔹 Example

```java
@BeforeMapping
protected void enrich(Car car, @MappingTarget CarDTO dto) {
    if (car instanceof ElectricCar) {
        dto.setFuelType(FuelType.ELECTRIC);
    }
}

@AfterMapping
protected void transform(@MappingTarget CarDTO dto) {
    dto.setName(dto.getName().toUpperCase());
}
```

---

### 🔹 Behavior

Generated code:

```java
enrichDTOWithFuelType(car, dto);
dto.setName(car.getName());
convertNameToUpperCase(dto);
```

✔ Hooks wrap around mapping logic

---

## 11. Lombok Support

---

### 🔹 Configuration

Add:

* `lombok`
* `lombok-mapstruct-binding`

---

### 🔹 Example

```java
@Getter
@Setter
public class Car {
    private int id;
    private String name;
}
```

✔ Works seamlessly with MapStruct

---

## 12. Default Values & Expressions

---

### 🔹 defaultExpression

```java
@Mapping(target = "id",
  defaultExpression = "java(UUID.randomUUID().toString())")
```

✔ Generates value if source is null

---

### 🔹 defaultValue

```java
@Mapping(target = "name",
  defaultValue = "anonymous")
```

✔ Assigns fallback value

---

### 🔹 Example Test

```java
assertEquals("anonymous", personDto.getName());
```

---

## 13. Key Features Summary

✔ Compile-time mapping

✔ No reflection (high performance)

✔ Type-safe

✔ Supports:

* Nested mapping
* Type conversion
* Custom logic
* DI (Spring/CDI)
* Lombok
* Default values

---

## 14. Conclusion

MapStruct is a **powerful and efficient mapping framework** that:

* Eliminates boilerplate mapping code
* Improves performance (compile-time generation)
* Enhances maintainability and readability

👉 Best suited for:

* DTO ↔ Entity mapping
* Spring Boot applications
* Clean architecture systems

---

## 🧠 Final Insight

Think of MapStruct as:

> “A compile-time assistant that writes your mapping code exactly the way you would—but faster, safer, and without mistakes.”

---

Here’s a **complete, production-style Spring Boot REST API example** using:

* ✅ DTO Pattern
* ✅ MapStruct
* ✅ Validation (`jakarta.validation`)
* ✅ Pagination (Spring Data)

This is the kind of setup you’d use in a **real-world project**.

---

# 🚀 1. Project Structure

```plaintext
com.example.demo
│
├── controller
│   └── UserController.java
│
├── service
│   ├── UserService.java
│   └── impl/UserServiceImpl.java
│
├── repository
│   └── UserRepository.java
│
├── entity
│   └── User.java
│
├── dto
│   ├── UserDTO.java
│   ├── UserCreateDTO.java
│   └── UserResponseDTO.java
│
├── mapper
│   └── UserMapper.java
│
└── exception
    └── GlobalExceptionHandler.java
```

---

# 🧱 2. Entity (Database Model)

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String email;

    private String password;

    // getters and setters
}
```

---

# 📦 3. DTOs

---

## 🔹 Request DTO (Validation)

```java
import jakarta.validation.constraints.*;

public class UserCreateDTO {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    private String password;

    // getters and setters
}
```

---

## 🔹 Response DTO

```java
public class UserDTO {

    private String id;
    private String name;
    private String email;

    // getters and setters
}
```

---

## 🔹 Paginated Response Wrapper

```java
public class UserResponseDTO {

    private List<UserDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    // getters and setters
}
```

---

# 🔄 4. MapStruct Mapper

```java
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserCreateDTO dto);

    UserDTO toDto(User user);

    List<UserDTO> toDtoList(List<User> users);
}
```

---

# 🗄️ 5. Repository

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
```

---

# ⚙️ 6. Service Layer

---

## 🔹 Interface

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User create(UserCreateDTO dto);

    Page<User> getAll(Pageable pageable);
}
```

---

## 🔹 Implementation

```java
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User create(UserCreateDTO dto) {
        User user = mapper.toEntity(dto);
        return repository.save(user);
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
```

---

# 🌐 7. Controller (REST API)

```java
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // ✅ Create User
    @PostMapping
    public UserDTO createUser(@Valid @RequestBody UserCreateDTO dto) {
        return mapper.toDto(service.create(dto));
    }

    // ✅ Get Users with Pagination
    @GetMapping
    public UserResponseDTO getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));

        Page<User> userPage = service.getAll(pageable);

        UserResponseDTO response = new UserResponseDTO();
        response.setContent(
            userPage.getContent().stream()
                .map(mapper::toDto)
                .toList()
        );
        response.setPage(userPage.getNumber());
        response.setSize(userPage.getSize());
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());

        return response;
    }
}
```

---

# ❗ 8. Global Exception Handling (Validation Errors)

```java
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
          .forEach(error ->
              errors.put(error.getField(), error.getDefaultMessage())
          );

        return errors;
    }
}
```

---

# 🔁 9. API Examples

---

## ✅ Create User

### Request

```http
POST /api/users
Content-Type: application/json
```

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

---

### Response

```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "john@example.com"
}
```

---

## ✅ Get Users (Pagination)

### Request

```http
GET /api/users?page=0&size=2
```

---

### Response

```json
{
  "content": [
    {
      "id": "1",
      "name": "John",
      "email": "john@example.com"
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 10,
  "totalPages": 5
}
```

---

# 🔥 10. Flow Summary

```plaintext
Client → Controller → DTO → Mapper → Entity → Repository → DB
                                   ↑
                              Mapper (back)
                                   ↓
Client ← DTO ← Controller ← Service ← Entity
```

---

# 🧠 11. Best Practices Applied

### ✅ DTO Pattern

* Separate request & response DTOs
* Hide password

### ✅ MapStruct

* Clean mapping layer
* No manual conversion

### ✅ Validation

* Input validation with annotations
* Centralized error handling

### ✅ Pagination

* Uses `Pageable` + `Page`
* Efficient large dataset handling

---

# 🏁 Final Takeaway

This setup gives you:

* 🔹 Clean architecture
* 🔹 High performance mapping (MapStruct)
* 🔹 Safe API contracts (DTO + Validation)
* 🔹 Scalable data access (Pagination)

---

Let’s extend the previous example into a **complete CRUD REST API** with:

* ✅ Create
* ✅ Read (single + paginated list)
* ✅ Update
* ✅ Delete
* ✅ DTO + MapStruct + Validation + Pagination

---

# 🚀 1. Updated API Endpoints (CRUD)

| Operation | Method | Endpoint          | Description         |
| --------- | ------ | ----------------- | ------------------- |
| Create    | POST   | `/api/users`      | Create new user     |
| Get All   | GET    | `/api/users`      | Get paginated users |
| Get One   | GET    | `/api/users/{id}` | Get user by ID      |
| Update    | PUT    | `/api/users/{id}` | Update user         |
| Delete    | DELETE | `/api/users/{id}` | Delete user         |

---

# 📦 2. DTOs

---

## 🔹 Create DTO (Request)

```java
public class UserCreateDTO {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    private String password;
}
```

---

## 🔹 Update DTO

```java
public class UserUpdateDTO {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}
```

---

## 🔹 Response DTO

```java
public class UserDTO {

    private String id;
    private String name;
    private String email;
}
```

---

## 🔹 Pagination Wrapper

```java
public class UserResponseDTO {

    private List<UserDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
```

---

# 🔄 3. MapStruct Mapper (Enhanced)

```java
@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserCreateDTO dto);

    UserDTO toDto(User user);

    List<UserDTO> toDtoList(List<User> users);

    // 🔥 Update existing entity
    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User entity);
}
```

✔ `@MappingTarget` → updates existing entity instead of creating new one

---

# 🗄️ 4. Repository

```java
public interface UserRepository extends JpaRepository<User, String> {
}
```

---

# ⚙️ 5. Service Layer

---

## 🔹 Interface

```java
public interface UserService {

    User create(UserCreateDTO dto);

    Page<User> getAll(Pageable pageable);

    User getById(String id);

    User update(String id, UserUpdateDTO dto);

    void delete(String id);
}
```

---

## 🔹 Implementation

```java
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User create(UserCreateDTO dto) {
        User user = mapper.toEntity(dto);
        return repository.save(user);
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public User getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User update(String id, UserUpdateDTO dto) {
        User user = getById(id);
        mapper.updateUserFromDto(dto, user);
        return repository.save(user);
    }

    @Override
    public void delete(String id) {
        User user = getById(id);
        repository.delete(user);
    }
}
```

---

# 🌐 6. Controller (Full CRUD)

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // ✅ CREATE
    @PostMapping
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return mapper.toDto(service.create(dto));
    }

    // ✅ READ (Paginated)
    @GetMapping
    public UserResponseDTO getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));

        Page<User> userPage = service.getAll(pageable);

        UserResponseDTO response = new UserResponseDTO();
        response.setContent(
            userPage.getContent().stream()
                .map(mapper::toDto)
                .toList()
        );
        response.setPage(userPage.getNumber());
        response.setSize(userPage.getSize());
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());

        return response;
    }

    // ✅ READ (Single)
    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable String id) {
        return mapper.toDto(service.getById(id));
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public UserDTO update(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateDTO dto
    ) {
        return mapper.toDto(service.update(id, dto));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
```

---

# ❗ 7. Improved Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> handleNotFound(RuntimeException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
          .forEach(error ->
              errors.put(error.getField(), error.getDefaultMessage())
          );

        return errors;
    }
}
```

---

# 🔁 8. API Examples

---

## ✅ 1. Create User

```http
POST /api/users
```

```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "password": "secret123"
}
```

---

## ✅ 2. Get All (Pagination)

```http
GET /api/users?page=0&size=3
```

---

## ✅ 3. Get One

```http
GET /api/users/{id}
```

---

## ✅ 4. Update User

```http
PUT /api/users/{id}
```

```json
{
  "name": "Alice Updated",
  "email": "alice.new@example.com"
}
```

---

## ✅ 5. Delete User

```http
DELETE /api/users/{id}
```

---

# 🔥 9. Full Flow (CRUD + DTO + MapStruct)

```plaintext
CREATE:
Client → DTO → Mapper → Entity → DB → Entity → DTO → Client

READ:
Client → Controller → Service → DB → Entity → DTO → Client

UPDATE:
Client → DTO → Mapper (@MappingTarget) → Entity → DB → DTO → Client

DELETE:
Client → Service → DB
```

---

# 🧠 10. Key Concepts Reinforced

### ✅ DTO Pattern

* Separate request & response models
* Protect sensitive data

### ✅ MapStruct

* Handles:

    * Entity creation
    * Entity updates (`@MappingTarget`)
    * List mapping

### ✅ Validation

* Ensures clean input
* Prevents invalid data

### ✅ Pagination

* Efficient for large datasets
* Uses `Pageable` + `Page`

---

# 🏁 Final Insight

This is a **production-ready CRUD design** that demonstrates:

* Clean separation of concerns
* High performance mapping
* Scalable API design
* Maintainable codebase

---




