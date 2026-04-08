
---

# 📘 MapStruct

## 1. What is MapStruct?

**MapStruct** is a **code generator** that simplifies the implementation of mappings between Java bean types (e.g., Entity ↔ DTO).

* It follows a **convention-over-configuration** approach
* Generates mapping code at **compile time**
* Uses **plain Java method calls** (no reflection)

👉 In simple terms:
**MapStruct automatically writes your mapping code for you.**

---

## 2. Key Characteristics

### ✅ Compile-time code generation

* Uses annotation processing during compilation
* Generates real Java classes (not proxies)

### ✅ High performance

* No reflection
* Same speed as handwritten code

### ✅ Type safety

* Errors detected at compile time
* Safer than runtime mapping tools

### ✅ Readable output

* Generated code is simple and easy to debug

---

## 3. Why Use MapStruct?

### 🔹 Problem

In multi-layered applications:

* We often map between:

    * Entities ↔ DTOs
    * Domain models ↔ API models

👉 Writing this mapping manually is:

* Tedious
* Repetitive
* Error-prone

---

### 🔹 Solution (MapStruct)

MapStruct:

* Automates mapping logic
* Reduces boilerplate code
* Improves maintainability

---

### 🔹 Advantages Over Other Frameworks

| Feature      | MapStruct    | Other Mappers (e.g., ModelMapper) |
| ------------ | ------------ | --------------------------------- |
| Mapping time | Compile-time | Runtime                           |
| Performance  | 🚀 Fast      | Slower                            |
| Type safety  | ✅ Strong     | ❌ Weak                            |
| Debugging    | Easy         | Hard                              |

---

## 4. How MapStruct Works

### 🔹 Annotation Processor

* Integrated into Java compiler
* Works with:

    * Maven
    * Gradle
    * IDEs

### 🔹 Workflow

1. Define a **mapper interface**
2. Annotate with `@Mapper`
3. Declare mapping methods
4. MapStruct generates implementation automatically

---

### 🔹 Sensible Defaults

* Maps fields with **same names automatically**
* Handles **basic type conversions**
* Allows customization when needed

---

## 5. Example: MapStruct in Action

---

### 🔹 Domain Model

```java
public class Car {
    private String make;
    private int numberOfSeats;
    private CarType type;
}
```

---

### 🔹 DTO

```java
public class CarDto {
    private String make;
    private int seatCount;
    private String type;
}
```

---

### 🔹 Mapper Interface

```java
@Mapper
public interface CarMapper {

    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    @Mapping(source = "numberOfSeats", target = "seatCount")
    CarDto carToCarDto(Car car);
}
```

---

### 🔍 Explanation

* `@Mapper` → Marks interface for MapStruct processing
* `@Mapping` → Handles different field names
* `INSTANCE` → Provides access to generated implementation

---

### 🔹 Using the Mapper

```java
Car car = new Car("Morris", 5, CarType.SEDAN);

CarDto dto = CarMapper.INSTANCE.carToCarDto(car);
```

---

### 🔹 Output

* `numberOfSeats → seatCount`
* `CarType enum → String`

✔ MapStruct handles conversions automatically when possible

---

## 6. Latest Features & Updates

### 🚀 MapStruct 1.7.0 (Beta, 2026)

* Native **Optional** support
* Improved **Kotlin** support
* Support for **Java 21 Sequenced Collections**
* Ability to ignore multiple fields at once

---

### 🔹 Spring Extensions (2.0.0)

* Requires:

    * Java 17+
    * Spring 6+
* Uses `jakarta.annotation.PostConstruct`
* Integrates with **Spring ConversionService**
* Generates bridge classes for better Spring integration

---

## 7. Complete Spring Boot Project Structure (DTO + MapStruct)

---

## 📁 Recommended Project Structure

```id="s0r91u"
com.example.app
│
├── controller
│   └── UserController.java
│
├── service
│   ├── UserService.java
│   └── impl
│       └── UserServiceImpl.java
│
├── repository
│   └── UserRepository.java
│
├── entity
│   └── User.java
│
├── dto
│   ├── UserDTO.java
│   └── UserCreationDTO.java
│
├── mapper
│   └── UserMapper.java
│
└── config
    └── MapperConfig.java
```

---

## 🔹 7.1 Entity

```java
@Entity
public class User {
    @Id
    private String id;
    private String name;
    private String password;
}
```

---

## 🔹 7.2 DTOs

```java
public class UserDTO {
    private String name;
}
```

```java
public class UserCreationDTO {
    private String name;
    private String password;
}
```

---

## 🔹 7.3 Mapper

```java
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserCreationDTO dto);
}
```

👉 `componentModel = "spring"`

* Registers mapper as a **Spring Bean**

---

## 🔹 7.4 Repository

```java
@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
```

---

## 🔹 7.5 Service Layer

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public User save(User user) {
        return repository.save(user);
    }
}
```

---

## 🔹 7.6 Controller

```java
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<UserDTO> getUsers() {
        return service.getAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PostMapping
    public UserDTO create(@RequestBody UserCreationDTO dto) {
        User user = mapper.toEntity(dto);
        User saved = service.save(user);
        return mapper.toDto(saved);
    }
}
```

---

## 🔥 Flow Summary

```id="d6c8v0"
Client → Controller → DTO → Mapper → Entity → Repository → Database
                                   ↑
                              Mapper (back)
                                   ↓
Client ← DTO ← Controller ← Service ← Entity
```

---

## 8. Best Practices with MapStruct

### ✅ DO:

* Use **DTOs for API communication**
* Use `componentModel = "spring"`
* Keep mapping logic inside mappers
* Use multiple DTOs for different use cases

---

### ❌ DON’T:

* Put business logic in mappers
* Expose entities directly in controllers
* Overcomplicate mappings unnecessarily

---

## 9. Key Takeaways

* **MapStruct = compile-time mapper**
* Eliminates boilerplate mapping code
* Ensures:

    * High performance
    * Type safety
    * Clean architecture

👉 Best used with:

* DTO pattern
* Layered Spring Boot applications

---

