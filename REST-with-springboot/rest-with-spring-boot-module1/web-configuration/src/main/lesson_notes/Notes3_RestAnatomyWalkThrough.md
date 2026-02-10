
---

# Spring REST Anatomy Walkthrough

## A Complete CRUD REST API in Spring Boot

---

## 1. Goal

In this lesson, we walk through the **anatomy of a complete CRUD REST API** built with Spring Boot, showing how **HTTP semantics and response strategies** shape each endpoint.

By the end, you should be able to:

* Design a full CRUD REST API in Spring
* Trace requests from client → controller → service → repository → response
* Choose the correct HTTP response strategy per CRUD operation
* Understand how REST constraints are enforced across Spring layers
* Avoid common REST and Spring anti-patterns

---

## 2. Big Picture: How Spring Implements REST

Spring does **not automatically make your API RESTful**.
It provides abstractions — **you design the REST contract**.

### High-level REST flow in Spring

```
Client
  │
  │ HTTP Request (POST /api/users)
  ▼
@RestController
  │
  ▼
@Service (Business Logic + Transactions)
  │
  ▼
@Repository (Persistence)
  │
  ▼
Database
  │
  ▲
HTTP Response (Status + Headers + Representation)
```

REST meaning is expressed primarily in the **response**, not the method body.

---

## 3. Resource-Oriented API Design

We expose a single REST resource:

```
/api/users
```

CRUD operations map directly to HTTP verbs:

| CRUD   | HTTP   | URI             |
| ------ | ------ | --------------- |
| Create | POST   | /api/users      |
| Read   | GET    | /api/users/{id} |
| Update | PUT    | /api/users/{id} |
| Delete | DELETE | /api/users/{id} |

This **uniform interface** is a core REST constraint.

---

## 4. Entry Point: The Controller Layer

### Role in REST

* Exposes **resources**
* Handles **HTTP semantics**
* Chooses the **response strategy**
* Delegates business logic

### Spring Component

`@RestController`

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}
```

Controllers are **HTTP-facing**, not business-facing.

---

## 5. CREATE — POST /api/users

### Full HTTP Control Required

### Why POST needs explicit responses

* Resource creation has strong HTTP semantics
* Must return `201 Created`
* Often includes `Location` header
* May return conflicts or validation errors

### Controller

```java
@PostMapping
public ResponseEntity<UserDto> create(@RequestBody UserDto newUser) {
    User model = UserDto.Mapper.toModel(newUser);
    User created = userService.create(model);
    UserDto response = UserDto.Mapper.toDto(created);

    return ResponseEntity
        .created(URI.create("/api/users/" + response.getId()))
        .body(response);
}
```

### Why `ResponseEntity`

* Implicit HTTP would return `200 OK` ❌
* Fixed contract cannot express conflicts ❌
* Full HTTP control matches REST intent ✅

---

## 6. READ — GET /api/users/{id}

### Implicit or Conditional HTTP

### Simple retrieval (implicit HTTP)

```java
@GetMapping("/{id}")
public UserDto getById(@PathVariable Long id) {
    return UserDto.Mapper.toDto(userService.findById(id));
}
```

* Default `200 OK`
* Clean and readable
* Acceptable when resource existence is guaranteed

---

### Conditional retrieval (full HTTP control)

```java
@GetMapping("/{id}")
public ResponseEntity<UserDto> getById(@PathVariable Long id) {
    return userService.findOptionalById(id)
        .map(user -> ResponseEntity.ok(UserDto.Mapper.toDto(user)))
        .orElse(ResponseEntity.notFound().build());
}
```

### Key REST insight

> GET is the only CRUD operation where **implicit HTTP is often acceptable**.

---

## 7. UPDATE — PUT /api/users/{id}

### Full HTTP Control Required

### REST semantics

* Resource may or may not exist
* Outcome depends on state
* Multiple valid status codes

### Controller

```java
@PutMapping("/{id}")
public ResponseEntity<UserDto> update(
        @PathVariable Long id,
        @RequestBody UserDto updatedUser) {

    if (!userService.exists(id)) {
        return ResponseEntity.notFound().build();
    }

    User model = UserDto.Mapper.toModel(updatedUser);
    User updated = userService.update(id, model);

    return ResponseEntity.ok(UserDto.Mapper.toDto(updated));
}
```

### Why not fixed status

* Update outcomes are not uniform
* REST meaning depends on resource state

---

## 8. DELETE — DELETE /api/users/{id}

### Fixed or Full HTTP Control

### Simple delete (fixed HTTP contract)

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
    userService.delete(id);
}
```

* Always `204 No Content`
* No response body
* Clean and declarative

---

### Conditional delete (full HTTP control)

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!userService.exists(id)) {
        return ResponseEntity.notFound().build();
    }

    userService.delete(id);
    return ResponseEntity.noContent().build();
}
```

### REST insight

> DELETE responses are **status-driven**, not body-driven.

---

## 9. Service Layer: The REST Boundary Guardian

### Role in REST

* Owns **business logic**
* Defines **transaction boundaries**
* Loads all required data
* Keeps controllers stateless

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public User create(User user) {
        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findOptionalById(Long id) {
        return repository.findById(id);
    }
}
```

This design supports **disabling OSIV** cleanly.

---

## 10. Repository Layer: Persistence, Not REST

### Role in REST

None.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

Repositories are **internal implementation details**.
REST never exposes them.

---

## 11. Entity vs DTO: REST Representation Boundary

### Entity (Persistence)

```java
@Entity
public class User {
    @Id
    private Long id;
    private String email;
    private String password; // ❌ never expose
}
```

### DTO (REST Resource Representation)

```java
public class UserDto {
    private Long id;
    private String email;
}
```

### REST rule

> **Entities model storage. DTOs model the API contract.**

---

## 12. HTTP Status Codes as REST Feedback

Spring offers two clean mechanisms:

### Explicit responses

```java
return ResponseEntity.status(HttpStatus.CONFLICT).build();
```

### Exception-based responses

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {}
```

Centralized handling keeps controllers clean.

---

## 13. REST Constraints Enforcement in Spring

| REST Constraint   | Enforced By                     |
| ----------------- | ------------------------------- |
| Client–Server     | Controller / Service separation |
| Stateless         | No session state                |
| Uniform Interface | HTTP verbs + URIs               |
| Cacheable         | Headers                         |
| Layered System    | Filters, interceptors           |
| Code on Demand    | Optional                        |

Spring **enables** REST — it does not enforce it.

---

## 14. Common REST Anti-Patterns in CRUD APIs

🚫 Returning entities directly
🚫 Implicit `200 OK` for POST
🚫 Business logic in controllers
🚫 Servlet API leakage
🚫 Ignoring HTTP semantics
🚫 Mixing persistence and representation

---

## 15. Final Takeaway

A complete CRUD REST API is defined by **how it responds**, not just what it does.

* POST → full HTTP control
* GET → implicit or conditional
* PUT → state-driven responses
* DELETE → status-only responses

Spring gives you the tools.
REST is the discipline.

> **Good Spring REST design is intentional HTTP design.**

---

# Aligning This CRUD API to the Richardson Maturity Model (Level 2)

---

## 1. Quick Recap: The Richardson Maturity Model

The Richardson Maturity Model evaluates how well an API uses HTTP as an **application protocol**, not just as transport.

| Level   | Key Characteristics                |
| ------- | ---------------------------------- |
| Level 0 | Single endpoint, HTTP as transport |
| Level 1 | Resources identified by URIs       |
| Level 2 | Proper HTTP verbs + status codes   |
| Level 3 | Hypermedia (HATEOAS)               |

This lesson intentionally targets **Level 2**.

---

## 2. Why This API Is Level 2 (Not Level 0 or 1)

### Level 0 APIs (What We Avoid)

```http
POST /api/createUser
POST /api/updateUser
GET  /api/findUser
```

Problems:

* HTTP verbs are ignored
* URIs represent actions, not resources
* Status codes are meaningless

🚫 **This lesson does NOT do this**

---

### Level 1 APIs (Partial REST)

```http
POST /api/users/create
POST /api/users/{id}/update
GET  /api/users/{id}/find
```

Improvements:

* Resources exist
* URIs are cleaner

Still missing:

* Correct HTTP verbs
* Meaningful HTTP semantics

🚫 **This lesson goes beyond Level 1**

---

## 3. Level 2 Core Requirements

An API is **Richardson Level 2** if it:

1. Uses **resource-based URIs**
2. Uses **HTTP verbs correctly**
3. Uses **HTTP status codes meaningfully**

Let’s map those directly to the lesson.

---

## 4. Resource-Based URIs (Level 2 ✔)

This lesson exposes a single resource:

```http
/api/users
/api/users/{id}
```

* ✔ URIs identify **nouns**, not actions
* ✔ Path structure is consistent
* ✔ Resource identity is stable

This satisfies **Level 1 and above**.

---

## 5. Correct Use of HTTP Verbs (Level 2 ✔)

Each CRUD operation maps directly to HTTP semantics:

| CRUD   | HTTP   | Meaning           |
| ------ | ------ | ----------------- |
| Create | POST   | Create a new user |
| Read   | GET    | Retrieve user     |
| Update | PUT    | Replace user      |
| Delete | DELETE | Remove user       |

Examples from the lesson:

```java
@PostMapping
@PutMapping("/{id}")
@GetMapping("/{id}")
@DeleteMapping("/{id}")
```

* ✔ HTTP verbs are not overloaded
* ✔ No action-based endpoints
* ✔ Semantics are clear and predictable

This is a **defining Level 2 feature**.

---

## 6. Meaningful HTTP Status Codes (Level 2 ✔)

Level 2 requires that HTTP responses **carry meaning**.

This lesson explicitly uses:

### Creation

```http
201 Created
Location: /api/users/{id}
```

```java
ResponseEntity.created(...).body(...)
```

---

### Retrieval

```http
200 OK
404 Not Found
```

```java
ResponseEntity.notFound().build();
```

---

### Update

```http
200 OK
404 Not Found
```

---

### Deletion

```http
204 No Content
404 Not Found
```

---

* ✔ Status codes reflect outcome
* ✔ Errors are communicated via HTTP
* ✔ Clients can react without parsing bodies

This is **core Level 2 compliance**.

---

## 7. Explicit HTTP Control Is a Level 2 Enabler

The lesson’s emphasis on **response strategies** directly supports Level 2:

| Response Strategy                    | Level 2 Suitability |
| ------------------------------------ | ------------------- |
| Implicit HTTP                        | Limited (GET only)  |
| Fixed HTTP Contract                  | Acceptable          |
| Full HTTP Control (`ResponseEntity`) | Ideal               |

Key takeaway:

> **You cannot reach Richardson Level 2 with implicit HTTP alone.**

---

## 8. What This Lesson Intentionally Does NOT Do (Level 3)

### Missing Hypermedia (HATEOAS)

A Level 3 API would include:

```json
{
  "id": 42,
  "email": "user@example.com",
  "_links": {
    "self": { "href": "/api/users/42" },
    "update": { "href": "/api/users/42" },
    "delete": { "href": "/api/users/42" }
  }
}
```

This lesson:

* ❌ Does not embed navigation links
* ❌ Does not drive clients via hypermedia
* ❌ Does not use HAL or custom media types

And that is **intentional**.

---

## 9. Why Level 2 Is the Sweet Spot for Most APIs

Level 2 provides:

* ✔ Clear contracts
* ✔ HTTP-native behavior
* ✔ Client predictability
* ✔ Excellent scalability
* ✔ Lower complexity than Level 3

Most real-world APIs stop here **by design**.

---

## 10. Final Alignment Summary

| Richardson Requirement | Covered in Lesson  |
| ---------------------- | ------------------ |
| Resource URIs          | ✅                 |
| HTTP verbs             | ✅                 |
| HTTP status codes      | ✅                 |
| Headers (`Location`)   | ✅                 |
| Hypermedia             | ❌ (intentional)   |

---

## Final Takeaway

This CRUD API is a **clean, textbook Richardson Level 2 REST API**.

It:

* Treats HTTP as an application protocol
* Uses verbs and status codes correctly
* Exposes stable, resource-oriented URIs
* Avoids unnecessary complexity

> **Richardson Level 2 is where REST becomes practical.**

---

# Extending the CRUD API to Richardson Maturity Level 3

## Hypermedia-Driven REST (HATEOAS)

---

## 16. What Changes at Richardson Level 3

At **Level 2**, clients understand:

* Resource URIs
* HTTP verbs
* HTTP status codes

At **Level 3**, clients no longer hard-code workflows.

Instead, they **discover what they can do next by following links** provided by the server.

> **Level 3 adds hypermedia controls to responses.**

This is known as **HATEOAS**
(Hypermedia As The Engine Of Application State).

---

## 17. What HATEOAS Means in Practice

### Level 2 interaction (client-driven)

```text
Client knows:
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}
```

The client must be **pre-programmed** with URI rules.

---

### Level 3 interaction (server-driven)

```text
Client:
GET /api/users/42

Server response tells client:
• where this resource lives
• how it can be updated
• how it can be deleted
• where related resources are
```

The client follows **links**, not assumptions.

---

## 18. Hypermedia as a REST Constraint

In Fielding’s dissertation:

> *A REST API is not fully RESTful unless it is hypermedia-driven.*

This means:

* Clients should not construct URLs
* Clients should not assume workflows
* Server controls application state transitions

This is the **only difference between Level 2 and Level 3**.

---

## 19. Choosing a Hypermedia Format

Plain JSON is **not a hypermedia format**.

To support Level 3, we must use a format that defines link semantics.

Common options:

| Format   | Notes                 |
| -------- | --------------------- |
| HAL      | Most common in Spring |
| JSON:API | Opinionated, strict   |
| Siren    | Rich actions          |
| Custom   | Rarely worth it       |

In Spring Boot, **HAL + Spring HATEOAS** is the standard choice.

---

## 20. Introducing Spring HATEOAS

### Dependency

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

This adds:

* `EntityModel`
* `CollectionModel`
* Link builders
* HAL serialization

---

## 21. Upgrading the User Representation (DTO → Resource)

### Level 2 DTO

```java
public class UserDto {
    private Long id;
    private String email;
}
```

---

### Level 3 Resource Representation

```java
import org.springframework.hateoas.RepresentationModel;

public class UserResource extends RepresentationModel<UserResource> {

    private Long id;
    private String email;

    // getters
}
```

Key difference:

> The representation now **carries links**.

---

## 22. Adding Hypermedia Links

### Controller with Links

```java
@GetMapping("/{id}")
public EntityModel<UserResource> getById(@PathVariable Long id) {

    User user = userService.findById(id);
    UserResource resource = UserMapper.toResource(user);

    return EntityModel.of(
        resource,
        linkTo(methodOn(UserController.class).getById(id)).withSelfRel(),
        linkTo(methodOn(UserController.class).update(id, null)).withRel("update"),
        linkTo(methodOn(UserController.class).delete(id)).withRel("delete")
    );
}
```

### What this achieves

* Client discovers allowed actions
* URIs are no longer hard-coded
* Server controls workflow

---

## 23. Example Level 3 Response (HAL)

```json
{
  "id": 42,
  "email": "user@example.com",
  "_links": {
    "self": {
      "href": "/api/users/42"
    },
    "update": {
      "href": "/api/users/42"
    },
    "delete": {
      "href": "/api/users/42"
    }
  }
}
```

This response **is the contract**.

---

## 24. Hypermedia for Collections

### GET /api/users

```java
@GetMapping
public CollectionModel<EntityModel<UserResource>> getAll() {

    List<EntityModel<UserResource>> users =
        userService.findAll().stream()
            .map(user -> {
                UserResource resource = UserMapper.toResource(user);
                return EntityModel.of(
                    resource,
                    linkTo(methodOn(UserController.class)
                        .getById(user.getId())).withSelfRel()
                );
            })
            .toList();

    return CollectionModel.of(
        users,
        linkTo(methodOn(UserController.class).getAll()).withSelfRel()
    );
}
```

Clients can now:

* Navigate the collection
* Follow links to individual resources
* Discover structure dynamically

---

## 25. CRUD Operations with HATEOAS

| Operation | Hypermedia Role               |
| --------- | ----------------------------- |
| POST      | Response includes `self` link |
| GET       | Returns navigable links       |
| PUT       | Linked as an available action |
| DELETE    | Linked as an available action |

Example creation response:

```java
@PostMapping
public ResponseEntity<EntityModel<UserResource>> create(
        @RequestBody UserDto newUser) {

    User created = userService.create(UserDto.Mapper.toModel(newUser));
    UserResource resource = UserMapper.toResource(created);

    return ResponseEntity
        .created(URI.create("/api/users/" + created.getId()))
        .body(
            EntityModel.of(
                resource,
                linkTo(methodOn(UserController.class)
                    .getById(created.getId())).withSelfRel()
            )
        );
}
```

---

## 26. How Level 3 Changes the Client

### Level 2 client

```text
if user exists:
  PUT /api/users/{id}
```

---

### Level 3 client

```text
GET /api/users/{id}
Follow link with rel="update"
```

Clients become:

* Less brittle
* More future-proof
* Decoupled from URI structure

---

## 27. Trade-offs of Richardson Level 3

### Advantages

* ✔ Strong decoupling
* ✔ Evolvable APIs
* ✔ Self-describing workflows
* ✔ True REST compliance

### Costs

* ❌ More code
* ❌ More concepts
* ❌ Harder to teach
* ❌ Limited client tooling support

---

## 28. Why Many APIs Stop at Level 2

Most real-world APIs:

* Are consumed by known clients
* Have stable URI structures
* Prefer simplicity over purity

Level 3 is often **overkill** unless:

* You build long-lived public APIs
* You expect clients you don’t control
* You want server-driven workflows

---

## 29. Level 2 vs Level 3 — Final Comparison

| Aspect          | Level 2 | Level 3 |
| --------------- | ------- |---------|
| Resource URIs   | ✅      | ✅      |
| HTTP verbs      | ✅      | ✅      |
| Status codes    | ✅      | ✅      |
| Hypermedia      | ❌      | ✅      |
| Client coupling | Medium  | Low     |
| Complexity      | Low     | High    |

---

## 30. Final Takeaway

Richardson Level 3 is **not “better REST”** — it is **more REST**.

* Level 2 is practical and sufficient for most APIs
* Level 3 is powerful, but costly
* Spring supports both — **you choose deliberately**

> **REST maturity is a design choice, not a badge.**

---

