# Spring Method Security (Comprehensive Lesson Notes + Mini Project)

---

## 1. Introduction: Why Method Security Exists

In Spring Security, authorization can be applied at **multiple layers**:

1. **Web / Request Layer** – using `SecurityFilterChain`
2. **Method Layer** – using Method Security Annotations (MSAs)

The **filter chain** is excellent when authorization decisions depend on:

* URL patterns
* HTTP methods (GET, POST, etc.)
* Static roles or authorities

However, filters **cannot see**:

* Method arguments
* Database entities
* Return values
* Business logic context

### Example Problem

> "Only allow a user to read an entity **if they are the owner of that entity**."

This decision requires **data returned from the database**, which the filter chain does not have access to.

➡️ **Method Security solves this problem** by enforcing authorization *around method execution*.

---

## 2. Enabling Method Security

Method Security is **disabled by default**.

Add the following annotation to a configuration class:

```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
```

### Enabling JSR-250 (`@RolesAllowed`)

```java
@EnableMethodSecurity(jsr250Enabled = true)
```

> Without this flag, `@RolesAllowed` will be ignored.

---

## 3. How Method Security Works (Important Concept)

Spring Method Security is implemented using **Spring AOP (proxies)**.

### What This Means

* Spring creates a **proxy object** around your bean
* Calls that go *through the proxy* are intercepted
* Calls *within the same class* bypass security

```java
@Service
public class DemoService {

    @PreAuthorize("hasRole('ADMIN')")
    public void secured() {}

    public void insecureCall() {
        secured(); // ❌ Security is bypassed
    }
}
```

📌 **Rule:** Method security works only when methods are called from **outside the bean**.

---

## 4. Overview of Method Security Annotations

| Annotation       | Purpose                    | Blocks Execution | Uses SpEL |
| ---------------- | -------------------------- | ---------------- | --------- |
| `@PreAuthorize`  | Authorize before execution | ✅                | ✅         |
| `@PostAuthorize` | Authorize after execution  | ✅                | ✅         |
| `@PreFilter`     | Filter input collections   | ❌                | ✅         |
| `@PostFilter`    | Filter output collections  | ❌                | ✅         |
| `@RolesAllowed`  | Role-based access          | ✅                | ❌         |

---

## 5. @PreAuthorize (Most Important Annotation)

`@PreAuthorize` evaluates a **SpEL expression before the method executes**.

### Common Authorization Expressions

```java
permitAll()
denyAll()
hasRole('USER')
hasAnyRole('ADMIN', 'MANAGER')
hasAuthority('CREATE')
hasAnyAuthority('READ', 'WRITE')
hasPermission(object, 'read')
```

### Example: Role-Based Access

```java
@PreAuthorize("hasRole('USER')")
public void register(User user) {
    // only users with ROLE_USER reach here
}
```

### Accessing Method Parameters

```java
@PreAuthorize("#userId != 1")
public User findUser(Long userId) {
    return repository.findById(userId).orElseThrow();
}
```

* `#userId` references the method parameter
* Method execution is blocked **before** database access

---

## 6. @PostAuthorize (Return-Value Based Authorization)

`@PostAuthorize` runs **after the method executes but before data is returned**.

### Example: Owner-Based Access Control

```java
@PostAuthorize("returnObject.ownerUsername == authentication.name")
public Document findDocument(Long id) {
    return documentRepository.findById(id).orElseThrow();
}
```

### Key Concepts

* `returnObject` refers to the returned value
* Can access **private fields** via SpEL
* Less efficient than `@PreAuthorize`

📌 **Best Practice:** Use `@PostAuthorize` only when return data is required.

---

## 7. @PreFilter (Filtering Input Collections)

`@PreFilter` removes elements from a collection **before method execution**.

### Important Rules

* Works only with `Collection` types
* Does **not** deny method execution
* Filters **individual elements**, not the collection itself

### Example: Filtering Dangerous Commands

```java
@PreFilter(filterTarget = "commands", value = "filterObject != 'shutdown'")
public void executeCommands(List<String> commands) {
    // 'shutdown' is removed automatically
}
```

### Understanding filterObject

| Term             | Meaning                         |
| ---------------- | ------------------------------- |
| `filterObject`   | One element from the collection |
| Expression true  | Element kept                    |
| Expression false | Element removed                 |

---

## 8. @PostFilter (Filtering Output Collections)

`@PostFilter` removes elements **after the method returns**.

### Example: Hide Test Data

```java
@PostFilter("filterObject.id > 20")
public List<User> findAllUsers() {
    return repository.findAll();
}
```

* Method executes normally
* Returned list is filtered

---

## 9. @RolesAllowed (JSR-250)

```java
@RolesAllowed("ROLE_ADMIN")
public void deleteUser(Long id) {
    repository.deleteById(id);
}
```

### Key Differences from @PreAuthorize

| Feature      | @RolesAllowed | @PreAuthorize |
| ------------ | ------------- | ------------- |
| SpEL support | ❌             | ✅             |
| ROLE_ prefix | Required      | Automatic     |
| Flexibility  | Low           | High          |

📌 **Recommendation:** Prefer `@PreAuthorize` in modern Spring applications.

---

## 10. Mini Project: Secure User Management (Full CRUD + All Method Security Annotations)

This mini project demonstrates **all five method security annotations** applied across standard CRUD operations.

### Goal

Implement a `UserService` that supports **Create, Read, Update, Delete, and List** operations, each secured using a *different* method security annotation.

Annotations covered:

* `@PreAuthorize`
* `@PostAuthorize`
* `@PreFilter`
* `@PostFilter`
* `@RolesAllowed`

---

## 10.1 Domain Model

```java
public class AppUser {
    private Long id;
    private String username;
    private String ownerUsername;
    private Set<String> roles;

    // getters and setters
}
```

* `ownerUsername` → user who created the record
* Used for ownership-based authorization

---

## 10.2 Repository (Mocked for Learning)

```java
@Repository
public class UserRepository {

    private final Map<Long, AppUser> store = new HashMap<>();

    public AppUser save(AppUser user) {
        store.put(user.getId(), user);
        return user;
    }

    public Optional<AppUser> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<AppUser> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
```

---

## 10.3 Service Layer (All 5 Method Security Annotations)

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // ---------------- CREATE ----------------
    // Uses @PreAuthorize
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public AppUser create(AppUser user) {
        user.setOwnerUsername(authentication().getName());
        return repository.save(user);
    }

    // ---------------- READ ----------------
    // Uses @PostAuthorize
    @PostAuthorize("returnObject.ownerUsername == authentication.name")
    public AppUser read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ---------------- UPDATE ----------------
    // Uses @PreFilter
    @PreAuthorize("hasRole('ADMIN')")
    @PreFilter(
        filterTarget = "roles",
        value = "filterObject != null && !filterObject.isBlank()"
    )
    public AppUser updateRoles(Long id, Set<String> roles) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRoles(roles);
        return repository.save(user);
    }

    // ---------------- LIST ----------------
    // Uses @PostFilter
    @PostFilter("filterObject.ownerUsername == authentication.name")
    public List<AppUser> findAll() {
        return repository.findAll();
    }

    // ---------------- DELETE ----------------
    // Uses @RolesAllowed
    @RolesAllowed("ROLE_ADMIN")
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Helper method for SpEL access
    private Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
```

---

## 10.4 Annotation-to-CRUD Mapping

| CRUD Operation | Method          | Annotation Used  | Purpose                            |
| -------------- | --------------- | ---------------- | ---------------------------------- |
| Create         | `create()`      | `@PreAuthorize`  | Blocks execution unless authorized |
| Read           | `read()`        | `@PostAuthorize` | Checks ownership after loading     |
| Update         | `updateRoles()` | `@PreFilter`     | Cleans role input before update    |
| List           | `findAll()`     | `@PostFilter`    | Hides other users’ data            |
| Delete         | `delete()`      | `@RolesAllowed`  | Admin-only operation               |

---

## 10.5 Execution Walkthrough Example

### Scenario: ADMIN updates user roles

1. ADMIN calls `updateRoles()`
2. `@PreAuthorize` verifies ADMIN role
3. `@PreFilter` removes blank/null roles
4. Roles are safely persisted

### Scenario: USER lists all users

1. USER calls `findAll()`
2. Repository returns all users
3. `@PostFilter` removes users not owned by caller
4. USER sees only their own records

---

## 10.6 Why This Design Is Correct

* All security rules live in the **service layer**
* Each annotation demonstrates its **intended purpose**
* CRUD operations reflect **real enterprise patterns**
* Avoids controller-level security mistakes

---

❌ Using `@PreFilter` on controllers
❌ Expecting `filterObject` to represent the whole list
❌ Relying on MSAs inside the same class
❌ Using MSAs for simple validation instead of authorization

---

## 12. Best Practices Summary

✅ Prefer `@PreAuthorize` over `@PostAuthorize`
✅ Apply method security at the **service layer**
✅ Keep controllers thin
✅ Use filters for authorization-based filtering
❌ Do not rely on MSAs alone for core business invariants

---

## 13. Final Summary

Spring Method Security provides **fine-grained, expressive authorization** at the method level.

You learned:

* How MSAs work internally
* When and why to use each annotation
* How to correctly use `@PreFilter` and `filterObject`
* How to build a secure, real-world registration flow

➡️ **Next lesson:** Custom `PermissionEvaluator` and custom security expressions
