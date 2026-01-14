# Custom PermissionEvaluator in Spring Security

**Level:** Advanced Spring Security (Spring Security 6)
**Focus:** Domain-object–level authorization using a Custom `PermissionEvaluator`, demonstrated at **three levels**:

* Method Security
* Web Security (SecurityFilterChain)
* Thymeleaf UI rendering

---

## 1. Introduction: Why Custom PermissionEvaluator?

So far, method security annotations like `@PreAuthorize`, `@PostAuthorize`, `@PreFilter`, and `@PostFilter` allow us to express authorization rules using roles, authorities, parameters, and return values.

However, real-world applications often require **data-aware authorization**, such as:

* “A user can read **only their own** records”
* “A manager can edit documents **they created**”
* “A user can delete a resource **only if they have DELETE permission on that specific row**”

These rules cannot be expressed cleanly with static role checks.

➡️ **This is where `PermissionEvaluator` comes in.**

---

## 2. What is PermissionEvaluator?

`PermissionEvaluator` is a Spring Security interface that allows you to define **custom authorization logic** evaluated through SpEL expressions.

```java
public interface PermissionEvaluator {

    boolean hasPermission(
        Authentication authentication,
        Object targetDomainObject,
        Object permission
    );

    boolean hasPermission(
        Authentication authentication,
        Serializable targetId,
        String targetType,
        Object permission
    );
}
```

### Two Evaluation Modes

| Method       | When to Use                        | Typical Annotation |
| ------------ | ---------------------------------- | ------------------ |
| Object-based | You already have the domain object | `@PostAuthorize`   |
| ID + Type    | You only have the ID               | `@PreAuthorize`    |

---

## 3. Mini Project Overview

### Scenario: Student Management System

* Database: **PostgreSQL**
* Domain: `Student`
* Users:

    * Students can view/update **their own** records
    * Admins can perform **full CRUD**

### Authorization Rules

| Operation | Rule                          |
| --------- | ----------------------------- |
| Create    | Admin only                    |
| Read      | Owner OR Admin                |
| Update    | Owner OR Admin                |
| Delete    | Owner OR Admin                |
| List      | Only own records unless Admin |

---

## 4. Domain Model (JPA + PostgreSQL)

```java
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // owner

    private String fullName;

    private String email;

    // getters and setters
}
```

---

## 5. DTO Layer (Why DTOs Matter)

DTOs decouple:

* Persistence model
* API contracts
* Security exposure

```java
public class StudentDto {
    private Long id;
    private String fullName;
    private String email;
}
```

Mapper (simplified):

```java
@Component
public class StudentMapper {

    public StudentDto toDto(Student s) {
        StudentDto dto = new StudentDto();
        dto.setId(s.getId());
        dto.setFullName(s.getFullName());
        dto.setEmail(s.getEmail());
        return dto;
    }
}
```

---

## 6. Repository Layer

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByUsername(String username);
}
```

---

## 7. Implementing CustomPermissionEvaluator

```java
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final StudentRepository repository;

    public CustomPermissionEvaluator(StudentRepository repository) {
        this.repository = repository;
    }

    // Object-based evaluation (PostAuthorize)
    @Override
    public boolean hasPermission(
            Authentication auth,
            Object target,
            Object permission) {

        if (target instanceof Student student) {
            return isOwnerOrAdmin(auth, student.getUsername());
        }
        return false;
    }

    // ID-based evaluation (PreAuthorize)
    @Override
    public boolean hasPermission(
            Authentication auth,
            Serializable targetId,
            String targetType,
            Object permission) {

        if ("Student".equalsIgnoreCase(targetType)) {
            return repository.findById((Long) targetId)
                    .map(s -> isOwnerOrAdmin(auth, s.getUsername()))
                    .orElse(false);
        }
        return false;
    }

    private boolean isOwnerOrAdmin(Authentication auth, String owner) {
        return auth.getName().equals(owner)
                || auth.getAuthorities().stream()
                   .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
```

---

## 8. Registering the PermissionEvaluator

```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    @Bean
    MethodSecurityExpressionHandler expressionHandler(
            CustomPermissionEvaluator evaluator) {

        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }
}
```

---

## 9. Method Security (CRUD Demonstration)

```java
@Service
public class StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository, StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // CREATE
    @PreAuthorize("hasRole('ADMIN')")
    public StudentDto create(Student student) {
        return mapper.toDto(repository.save(student));
    }

    // READ
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public Student findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    // UPDATE
    @PreAuthorize("hasPermission(#id, 'Student', 'UPDATE')")
    public StudentDto update(Long id, StudentDto dto) {
        Student s = repository.findById(id).orElseThrow();
        s.setFullName(dto.getFullName());
        s.setEmail(dto.getEmail());
        return mapper.toDto(repository.save(s));
    }

    // DELETE
    @PreAuthorize("hasPermission(#id, 'Student', 'DELETE')")
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // LIST
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<Student> findAll() {
        return repository.findAll();
    }
}
```

---

## 10. Web Security (SecurityFilterChain)

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/students/**")
        .access("hasPermission('Student', 'READ')")
        .anyRequest().authenticated()
      )
      .formLogin();

    return http.build();
}
```

---

## 11. Thymeleaf Integration

```html
<tr th:each="student : ${students}">
  <td th:text="${student.fullName}"></td>

  <td sec:authorize="hasPermission(${student}, 'UPDATE')">
      <a th:href="@{/students/edit/{id}(id=${student.id})}">Edit</a>
  </td>

  <td sec:authorize="hasPermission(${student}, 'DELETE')">
      <a th:href="@{/students/delete/{id}(id=${student.id})}">Delete</a>
  </td>
</tr>
```

✔ UI reflects the same authorization rules as backend

---

## 12. How Permission Evaluation Flows

1. User makes request
2. SpEL expression evaluated
3. Spring calls `hasPermission()`
4. Domain data consulted
5. Access granted or denied

---

## 13. Common Pitfalls

* ❌ Mixing MVC `Model` with method security
* ❌ Using `@PostAuthorize` for collections
* ❌ Hardcoding role logic instead of domain logic
* ❌ Forgetting to register the evaluator

---

## 14. Summary

A Custom `PermissionEvaluator` enables:

* Fine-grained, domain-driven authorization
* Reusable security logic
* Unified rules across backend and UI

You learned:

* How to implement and register a PermissionEvaluator
* How to secure CRUD operations with domain data
* How to apply permissions at Method, Web, and UI layers

➡️ **Next step:** Persist object-level permissions in a dedicated permissions table and load them dynamically from PostgreSQL.

---

## Deep Dive: CustomPermissionEvaluator – Ownership & Admin-Based Authorization

This section expands the mini‑project with a **realistic, ownership‑based CustomPermissionEvaluator** implementation and explains **exactly how Spring Security routes SpEL expressions to each hasPermission() method**.

### Full Implementation Example

```java
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final StudentService studentService;

    @Autowired
    public CustomPermissionEvaluator(@Lazy StudentService studentService) {
        this.studentService = studentService;
    }

    // =====================================================
    // Object-based evaluation (PostAuthorize / PostFilter)
    // =====================================================
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject,
                                 Object permission) {
        if (targetDomainObject == null) {
            return false;
        }

        // -------- URL / Web Security checks --------
        if (targetDomainObject instanceof String) {
            String target = (String) targetDomainObject;
            if (target.equals("Student")) {
                return authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            }
            return false;
        }

        // -------- Domain Object checks --------
        if (targetDomainObject instanceof Student) {
            Student student = (Student) targetDomainObject;
            return isOwnerOrAdmin(authentication, student.getUsername());
        }

        return false;
    }

    // =====================================================
    // ID-based evaluation (PreAuthorize)
    // =====================================================
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId,
                                 String targetType,
                                 Object permission) {
        if ("Student".equalsIgnoreCase(targetType)) {
            StudentDTO student = studentService.findById((Long) targetId);
            return student != null && isOwnerOrAdmin(authentication, student.getEmail());
        }
        return false;
    }

    // =====================================================
    // Shared ownership logic
    // =====================================================
    private boolean isOwnerOrAdmin(Authentication authentication, String ownerId) {
        return authentication.getName().equals(ownerId)
                || authentication.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
    }
}
```

---

## How Spring Security Routes hasPermission() Calls

Spring Security automatically selects **which hasPermission() overload to invoke** based on how the SpEL expression is written.

### 1️⃣ PostFilter – Object‑Based Evaluation

```java
@PostFilter("hasPermission(filterObject, 'READ')")
public List<Student> getAllStudents() {
    return studentRepository.findAll();
}
```

**Execution flow:**

1. Method executes and returns a collection
2. `filterObject` represents *each element* in the collection
3. Spring calls:

```java
hasPermission(authentication, filterObject, "READ")
```

**Effect:**

* Each `Student` is evaluated individually
* Only students the user **owns** or **admin users** can see are returned
* Prevents **data leakage**

---

### 2️⃣ PreAuthorize – ID‑Based Evaluation (DELETE)

```java
@PreAuthorize("hasPermission(#id, 'Student', 'DELETE')")
public void deleteStudent(Long id) {
    studentRepository.deleteById(id);
}
```

**Execution flow:**

1. Method execution is intercepted
2. `#id` resolves to the method parameter
3. Spring calls:

```java
hasPermission(authentication, id, "Student", "DELETE")
```

**Effect:**

* Student is loaded via `StudentService`
* Ownership/admin check occurs **before deletion**
* Prevents unauthorized destructive actions

---

### 3️⃣ PreAuthorize – ID‑Based Evaluation (UPDATE)

```java
@PreAuthorize("hasPermission(#id, 'Student', 'UPDATE')")
public StudentDTO updateStudent(Long id, StudentDTO dto) {
    return studentService.update(id, dto);
}
```

Same execution path as DELETE, but checks for UPDATE permission.

---

## Web Security Integration

The same evaluator works at the **HTTP layer**:

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/students/**")
    .access("hasPermission('Student', 'READ')")
);
```

➡ Calls:

```java
hasPermission(authentication, "Student", "READ")
```

This allows **route‑level protection** using the same domain rules.

---

## Thymeleaf Integration

```html
<div sec:authorize="hasPermission(student, 'DELETE')">
    <button>Delete Student</button>
</div>
```

➡ Calls:

```java
hasPermission(authentication, student, "DELETE")
```

✔ UI elements are hidden unless the user has permission
✔ Prevents accidental exposure of privileged actions

---

## Why This Pattern Is Powerful

✔ Ownership‑based security
✔ Works at **method, web, and UI layers**
✔ Prevents horizontal privilege escalation
✔ Clean separation of concerns
✔ Scales beyond roles

This is the **recommended approach** for real‑world Spring Security systems where authorization depends on *who owns the data*, not just *who has a role*.
