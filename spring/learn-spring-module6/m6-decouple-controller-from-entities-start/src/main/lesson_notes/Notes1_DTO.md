
---

# **Lesson: Decoupling the Controller from Entities**

---

## **1. Lesson Overview**

This lesson explores the principle of **decoupling controllers from entities** in Spring-based web applications.
In enterprise-grade systems, it is a best practice to avoid exposing **database entities** directly to the client. Instead, we use **Data Transfer Objects (DTOs)** to mediate between the persistence and presentation layers.

Decoupling enhances **security**, **maintainability**, **performance**, and **API design flexibility** — all key aspects of a clean and scalable architecture.

---

## **2. Learning Objectives**

By the end of this lesson, you should be able to:

* Explain why Spring controllers should not expose entities directly.
* Define and implement **Data Transfer Objects (DTOs)**.
* Convert between entities and DTOs manually and automatically using **ModelMapper**.
* Use **Java records** as immutable DTOs.
* Understand how this approach fits into the **Spring MVC/REST architecture**.

---

## **3. Architectural Context**

Before delving into code, it is important to understand **where and why** decoupling occurs in the Spring MVC or REST pipeline.

### **3.1. Data Flow in a Decoupled Architecture**

Below is a conceptual diagram of the data flow:

```
         +-------------------+
         |      CLIENT       |
         | (Browser / App)   |
         +--------+----------+
                  |
                  v
         +-------------------+
         |   CONTROLLER      | <-- Handles HTTP requests/responses
         | (Receives DTOs)   |
         +--------+----------+
                  |
          DTO <-> ENTITY Conversion
                  |
         +-------------------+
         |     SERVICE       | <-- Business logic layer
         +--------+----------+
                  |
         +-------------------+
         |     REPOSITORY    | <-- Data access layer (JPA/Hibernate)
         +--------+----------+
                  |
         +-------------------+
         |      DATABASE     |
         +-------------------+
```

In this architecture:

* The **controller** communicates only via **DTOs**, not entities.
* The **service** layer mediates between DTOs and entities, handling conversion and business logic.
* The **repository** layer interacts with the database using JPA entities.
* The **client** (web app, mobile app, or API consumer) only receives or sends data as JSON (DTO form).

---

## **4. Why Decouple Controllers from Entities**

While returning entities directly is simple in small prototypes, in production-grade systems it leads to several challenges.

### **4.1. Entities Are Not API Resources**

* Entities describe how data is stored in the database.
* DTOs describe how data is represented and shared externally.
  Keeping these separate prevents persistence-layer details from leaking into API design.

### **4.2. Security**

Exposing entire entities may unintentionally leak sensitive information (e.g., user passwords, internal IDs, or audit metadata).

### **4.3. Maintainability**

Changing entity fields could break clients if entities are exposed directly. DTOs provide **API stability** — internal changes do not affect external contracts.

### **4.4. Performance**

DTOs enable fine-grained control over which data is transmitted, improving network efficiency and serialization performance.

---

## **5. Implementation Example**

### **5.1. Entity Classes**

```java
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks;
}
```

```java
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;
}
```

---

### **5.2. DTO Classes**

```java
public class ProjectDto {
    private Long id;
    private String name;
    private LocalDate dateCreated;
    private Set<TaskDto> tasks;
}
```

```java
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
}
```

---

### **5.3. Conversion Methods**

```java
protected ProjectDto convertToDto(Project entity) {
    ProjectDto dto = new ProjectDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setDateCreated(entity.getDateCreated());
    dto.setTasks(entity.getTasks()
        .stream()
        .map(this::convertTaskToDto)
        .collect(Collectors.toSet()));
    return dto;
}

protected Project convertToEntity(ProjectDto dto) {
    Project project = new Project();
    project.setName(dto.getName());
    project.setDateCreated(dto.getDateCreated());
    return project;
}
```

---

### **5.4. Controller Example**

```java
@RestController
@RequestMapping("/projects")
public class ControllerClass {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable Long id) {
        Project project = projectService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToDto(project);
    }

    @PostMapping
    public void createProject(@RequestBody ProjectDto projectDto) {
        Project project = convertToEntity(projectDto);
        projectService.save(project);
    }
}
```

---

## **6. Using Java Records for DTOs**

Modern Java versions (16+) introduce **records**, which are ideal for immutable DTOs.

```java
public record TaskDto(
    Long id,
    String name,
    String description,
    LocalDate dueDate,
    TaskStatus status
) {}
```

> ✅ **Note:** Records work best in REST APIs, not MVC, since they are immutable and cannot be modified by templating engines like Thymeleaf.

---

## **7. Automating Conversion with ModelMapper**

Manual mapping can be repetitive. The **ModelMapper** library automates conversions.

### **7.1. Add Dependency**

```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.2.0</version>
</dependency>
```

### **7.2. Configuration**

```java
@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```

### **7.3. Using ModelMapper**

```java
@Autowired
private ModelMapper modelMapper;

private PostDto convertToDto(Post post) {
    return modelMapper.map(post, PostDto.class);
}

private Post convertToEntity(PostDto postDto) {
    return modelMapper.map(postDto, Post.class);
}
```

---

## **8. Testing DTO Conversion**

```java
@Test
public void whenConvertPostEntityToDto_thenCorrect() {
    Post post = new Post(1L, "Test", "www.example.com");
    PostDto dto = modelMapper.map(post, PostDto.class);

    assertEquals(post.getId(), dto.getId());
    assertEquals(post.getTitle(), dto.getTitle());
}
```

---

## **9. Summary**

| Aspect              | Exposing Entities     | Using DTOs                |
| ------------------- | --------------------- | ------------------------- |
| **Security**        | Exposes internal data | Hides sensitive fields    |
| **Maintainability** | Coupled to database   | Independent and flexible  |
| **Performance**     | Heavy payload         | Lightweight and efficient |
| **Extensibility**   | Hard to evolve        | Easy to extend and adapt  |

---

## **10. Exercises**

1. Explain why it is considered poor practice to expose JPA entities directly through a REST API.
2. Implement a `UserDto` and `User` entity pair, and demonstrate conversion methods between them.
3. Configure ModelMapper in a Spring Boot project and test the conversion between a `Book` entity and `BookDto`.
4. Discuss how using Java records as DTOs enhances immutability and reduces boilerplate code.
5. Draw your own diagram showing how data flows from a REST client to a database and back in a Spring application.

---

