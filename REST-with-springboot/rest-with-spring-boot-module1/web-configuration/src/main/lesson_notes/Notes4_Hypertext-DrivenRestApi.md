
---

# REST APIs Must Be Hypertext-Driven (HATEOAS)

## 1. Why This Lesson Matters

Many APIs use HTTP and JSON and call themselves “REST APIs”.
Roy T. Fielding, the author of REST, strongly disagrees.

> If the engine of application state is not driven by hypertext, then it is **not RESTful**.

This lesson explains **what hypertext-driven really means**, why it matters, and how to **implement it in Spring REST APIs**.

---

## 2. What “Hypertext-Driven” Means (In Plain Terms)

A **hypertext-driven API** is one where:

* Clients **do not hardcode URLs**
* Clients **do not guess what to do next**
* Every response tells the client:

    * **What this resource is**
    * **What actions are possible next**
    * **Which HTTP method to use**
    * **Where to go**

In short:

> The response itself tells the client how to continue.

This is called **HATEOAS**
(Hypermedia As The Engine Of Application State)

---

## 3. Non-REST vs RESTful Interaction

### ❌ RPC-style (Not REST)

```http
POST /users/create
POST /users/update
POST /users/delete
```

Problems:

* Action-based URIs
* Client must know endpoints in advance
* Tight coupling
* No hypertext

---

### ✅ RESTful, Hypertext-Driven

```http
GET /users/42
```

Response:

```json
{
  "id": 42,
  "name": "Alice",
  "_links": {
    "self": { "href": "/users/42" },
    "update": { "href": "/users/42", "method": "PUT" },
    "delete": { "href": "/users/42", "method": "DELETE" }
  }
}
```

Now:

* Client discovers valid actions dynamically
* Server controls evolution
* No out-of-band instructions

---

## 4. Key REST Rules Related to Hypertext (Fielding)

### Rule 1: Clients Must Not Rely on Fixed URIs

Bad:

```text
PUT /users/{id}/activate
```

Good:

```json
{
  "status": "INACTIVE",
  "_links": {
    "activate": { "href": "/users/42/activation", "method": "POST" }
  }
}
```

➡️ Server decides the URI
➡️ Client follows links

---

### Rule 2: Media Types Drive Behavior

Clients should depend on:

* Media type
* Link relations

Not on:

* Domain-specific object types
* Controller method names

HTML works this way.
JSON APIs must **explicitly add links**.

---

### Rule 3: Entry Point Requires No Prior Knowledge

A REST API should be usable starting from **one URI only**.

```http
GET /api
```

Response:

```json
{
  "_links": {
    "users": { "href": "/users" },
    "orders": { "href": "/orders" }
  }
}
```

No documentation required to navigate.

---

## 5. Richardson Maturity Model Alignment

| Level   | Description               | Hypertext  |
| ------- | ------------------------- | ---------- |
| Level 0 | RPC over HTTP             | ❌         |
| Level 1 | Resources                 | ❌         |
| Level 2 | HTTP verbs + status codes | ❌         |
| Level 3 | Hypermedia (HATEOAS)      | ✅         |

➡️ **Hypertext is the defining feature of Level 3**

---

## 6. Implementing Hypertext in Spring (HATEOAS)

Spring provides **Spring HATEOAS**.

### Example: User Resource with Links

```java
@GetMapping("/{id}")
public EntityModel<UserDto> getUser(@PathVariable Long id) {
    UserDto user = userService.findById(id);

    return EntityModel.of(user,
        linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
        linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"),
        linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete")
    );
}
```

Response:

```json
{
  "id": 42,
  "name": "Alice",
  "_links": {
    "self": { "href": "/users/42" },
    "update": { "href": "/users/42" },
    "delete": { "href": "/users/42" }
  }
}
```

---

## 7. Hypertext for CRUD Operations

### CREATE (POST)

```http
POST /users
```

Response:

```json
{
  "id": 42,
  "_links": {
    "self": { "href": "/users/42" }
  }
}
```

➡️ Client does not guess the new URI
➡️ Server provides it

---

### READ (GET)

```http
GET /users/42
```

Include links to:

* Update
* Delete
* Related resources

---

### UPDATE (PUT / PATCH)

Only allowed if link exists:

```json
"_links": {
  "update": { "href": "/users/42", "method": "PUT" }
}
```

---

### DELETE (DELETE)

```json
"_links": {
  "delete": { "href": "/users/42", "method": "DELETE" }
}
```

➡️ No link = action not allowed

---

## 8. HTTP Responses Still Matter

Hypertext does **not replace HTTP semantics**.

| Operation   | Typical Status |
| ----------- | -------------- |
| GET         | 200            |
| POST create | 201 + Location |
| PUT         | 200 / 204      |
| DELETE      | 204            |

Hypertext tells **what to do next**
HTTP tells **what just happened**

---

## 9. Why This Matters Long-Term

### Without Hypertext

* Clients break when URIs change
* Versioning explodes
* Documentation becomes mandatory
* Tight coupling

### With Hypertext

* Clients adapt automatically
* APIs evolve safely
* Fewer breaking changes
* True REST

> REST is about **longevity**, not convenience.

---

## 10. Key Takeaways

* REST ≠ JSON over HTTP
* Hypertext is a **core constraint**, not optional
* HATEOAS enables:

    * Loose coupling
    * API evolution
    * True REST maturity
* Spring supports this via **Spring HATEOAS**
* Level 3 REST starts where most APIs stop

---

# Spring Boot REST + HATEOAS Mini-Project

**Richardson Maturity Model Level 3**

---

## 1. Project Goal

Build a RESTful **User Management API** that:

* Uses correct HTTP verbs and status codes
* Returns appropriate HTTP responses per CRUD operation
* Evolves from Level 2 to **Level 3 (HATEOAS)**
* Exposes navigation links so clients can discover actions dynamically

---

## 2. Richardson Maturity Model Recap

| Level       | Description                              |
| ----------- | ---------------------------------------- |
| Level 0     | Single endpoint, RPC-style               |
| Level 1     | Multiple resources                       |
| Level 2     | Correct HTTP verbs + status codes        |
| **Level 3** | **HATEOAS: links guide client behavior** |

This project **starts at Level 2** and **extends to Level 3**.

---

## 3. Domain Model

### Entity: User

```java
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // getters and setters
}
```

---

## 4. DTO Design

We pass `UserDto` directly (no `CreateUserRequest`) to avoid confusion and keep symmetry across CRUD.

```java
public class UserDto {

    private Long id;
    private String name;
    private String email;

    public static class Mapper {
        public static User toModel(UserDto dto) {
            User user = new User();
            user.setId(dto.getId());
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            return user;
        }

        public static UserDto toDto(User model) {
            UserDto dto = new UserDto();
            dto.setId(model.getId());
            dto.setName(model.getName());
            dto.setEmail(model.getEmail());
            return dto;
        }
    }
}
```

---

## 5. Repository Layer

```java
public interface UserRepository extends JpaRepository<User, Long> {
}
```

---

## 6. Service Layer

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
```

---

## 7. Response Strategy per CRUD Operation

| Operation | HTTP Verb | Response Strategy   |
| --------- | --------- | ------------------- |
| Read all  | GET       | Implicit HTTP       |
| Read one  | GET       | Full HTTP control   |
| Create    | POST      | Full HTTP control   |
| Update    | PUT       | Fixed HTTP contract |
| Delete    | DELETE    | Full HTTP control   |

---

## 8. Level 2 Controller (Before HATEOAS)

```java
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // GET – implicit HTTP (always 200 OK)
    @GetMapping
    public List<UserDto> findAll() {
        return service.findAll()
                .stream()
                .map(UserDto.Mapper::toDto)
                .toList();
    }

    // GET by id – full HTTP control
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findOne(@PathVariable Long id) {
        return service.findById(id)
                .map(user -> ResponseEntity.ok(UserDto.Mapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST – full HTTP control
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto newUser) {
        User model = UserDto.Mapper.toModel(newUser);
        User created = service.save(model);

        URI location = URI.create("/users/" + created.getId());
        return ResponseEntity
                .created(location)
                .body(UserDto.Mapper.toDto(created));
    }

    // PUT – fixed HTTP contract
    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto updatedUser) {
        User model = UserDto.Mapper.toModel(updatedUser);
        model.setId(id);
        return UserDto.Mapper.toDto(service.save(model));
    }

    // DELETE – full HTTP control
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

At this point, the API is **Richardson Level 2**.

---

## 9. Moving to Level 3 – HATEOAS

### Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

---

## 10. HATEOAS Representation Model

```java
public class UserModel extends RepresentationModel<UserModel> {

    private Long id;
    private String name;
    private String email;

    public static UserModel from(User user) {
        UserModel model = new UserModel();
        model.id = user.getId();
        model.name = user.getName();
        model.email = user.getEmail();
        return model;
    }
}
```

---

## 11. HATEOAS Controller (Level 3)

```java
@RestController
@RequestMapping("/users")
public class UserHateoasController {

    private final UserService service;

    public UserHateoasController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserModel> create(@RequestBody UserDto newUser) {

        User model = UserDto.Mapper.toModel(newUser);
        User created = service.save(model);

        UserModel resource = UserModel.from(created);
        resource.add(linkTo(methodOn(UserHateoasController.class)
                .findOne(created.getId())).withSelfRel());

        return ResponseEntity
                .created(URI.create("/users/" + created.getId()))
                .body(resource);
    }

    @GetMapping
    public CollectionModel<UserModel> findAll() {

        List<UserModel> users = service.findAll().stream()
                .map(user -> {
                    UserModel model = UserModel.from(user);
                    model.add(linkTo(methodOn(UserHateoasController.class)
                            .findOne(user.getId())).withSelfRel());
                    return model;
                })
                .toList();

        return CollectionModel.of(
                users,
                linkTo(methodOn(UserHateoasController.class).findAll()).withSelfRel(),
                linkTo(methodOn(UserHateoasController.class).create(null)).withRel("create")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> findOne(@PathVariable Long id) {

        return service.findById(id)
                .map(user -> {
                    UserModel model = UserModel.from(user);

                    model.add(linkTo(methodOn(UserHateoasController.class)
                            .findOne(id)).withSelfRel());

                    model.add(linkTo(methodOn(UserHateoasController.class)
                            .delete(id)).withRel("delete"));

                    model.add(linkTo(methodOn(UserHateoasController.class)
                            .update(id, null)).withRel("update"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserModel> update(
            @PathVariable Long id,
            @RequestBody UserDto dto) {

        User model = UserDto.Mapper.toModel(dto);
        model.setId(id);
        User updated = service.save(model);

        return ResponseEntity.ok(UserModel.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 12. Sample HATEOAS Response

```json
{
  "id": 1,
  "name": "Alice",
  "email": "alice@example.com",
  "_links": {
    "self": { "href": "/users/1" },
    "update": { "href": "/users/1" },
    "delete": { "href": "/users/1" }
  }
}
```

This is **true Level 3 REST**:

* Clients don’t guess URLs
* Server drives navigation
* API can evolve safely

---

## 13. Key Teaching Takeaways

* **Implicit HTTP** keeps controllers clean for simple reads
* **Fixed HTTP contracts** work when responses are predictable
* **Full HTTP control** is essential for lifecycle operations
* **HATEOAS decouples clients from URL knowledge**
* Richardson Level 3 is about **discoverability, not complexity**

---

## 14. Suggested Student Exercise

1. Add a `disable` link only when a user is active
2. Add pagination links to `findAll`
3. Hide `delete` for read-only roles

---

