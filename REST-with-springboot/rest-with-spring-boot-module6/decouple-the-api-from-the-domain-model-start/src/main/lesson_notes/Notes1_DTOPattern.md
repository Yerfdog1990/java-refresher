# The DTO Pattern (Data Transfer Object)

---

## 1. Overview

The DTO (Data Transfer Object) pattern is a design pattern used to carry data between processes. This tutorial covers what it is, why it exists, how to implement it, and common mistakes to avoid.

---

## 2. The Pattern

DTOs are objects that carry data between processes in order to **reduce the number of method calls**. The pattern was first introduced by Martin Fowler in his book *Patterns of Enterprise Application Architecture (EAA)*.

### Primary Purpose

Reduce roundtrips to the server by **batching multiple parameters into a single call**, thereby reducing network overhead in remote operations.

### Additional Benefits

- **Encapsulates serialisation logic** — provides a single point of change for how objects are translated into a transferable format (JSON, XML, etc.)
- **Decouples the domain model from the presentation layer** — both can change independently without affecting each other

---

## 3. How DTOs Work

DTOs are normally created as **POJOs (Plain Old Java Objects)**. They are flat data structures that contain:

- Storage (fields)
- Accessors (getters/setters)
- Optionally, methods related to serialisation or parsing

They contain **no business logic**.

### Data Flow

Data is mapped from domain models to DTOs (and vice versa) via a **mapper component** in the presentation or facade layer:

```
Client  ←→  Controller  ←→  Mapper  ←→  Domain Model  ←→  Service / Persistence
               (DTO)                   (Domain Object)
```

---

## 4. When to Use DTOs

DTOs are particularly useful in the following scenarios:

| Scenario | Why DTOs Help |
|---|---|
| Systems with remote calls | Reduce the number of round trips by batching data |
| Domain model with many objects | Present all needed data in a single response |
| Multiple client views needed | Build different representations of the same domain optimised for each client |
| Sensitive data in the domain model | Hide fields (e.g. passwords) that should not be exposed to clients |
| Reducing client-server round trips | Combine required input into a single request object |

> DTOs allow you to create different views of your domain model, optimised to client needs, without affecting the underlying domain design.

---

## 5. Use Case: User and Role

### 5.1 Domain Models vs DTOs

**Domain model — `User`:**
```java
public class User {
    private String id;
    private String name;
    private String password;
    private List<Role> roles;

    public User(String name, String password, List<Role> roles) {
        this.name = Objects.requireNonNull(name);
        this.password = this.encrypt(password);
        this.roles = Objects.requireNonNull(roles);
    }

    String encrypt(String password) {
        // encryption logic
    }
}
```

**Domain model — `Role`:**
```java
public class Role {
    private String id;
    private String name;
    // Constructors, getters and setters
}
```

The domain models contain business logic (`encrypt`) and sensitive data (`password`) that should not be exposed to clients.

---

**Response DTO — `UserDTO`:**
```java
public class UserDTO {
    private String name;
    private List<String> roles;
    // standard getters and setters
}
```

- Exposes only relevant information to the client
- **Hides the password** for security reasons
- Flattens the `Role` objects into a simple list of role name strings

---

**Request DTO — `UserCreationDTO`:**
```java
public class UserCreationDTO {
    private String name;
    private String password;
    private List<String> roles;
    // standard getters and setters
}
```

- Groups all data needed to create a user into a single object
- Sent to the server in one request, optimising API interactions
- Accepts the password on input (needed for creation) but the response DTO never returns it

---

### 5.2 The Controller Layer

The controller maps between DTOs and domain models using a `Mapper` component:

```java
@RestController
@RequestMapping("/users")
class UserController {

    private UserService userService;
    private RoleService roleService;
    private Mapper mapper;

    @GetMapping
    @ResponseBody
    public List<UserDTO> getUsers() {
        return userService.getAll()
          .stream()
          .map(mapper::toDto)
          .collect(toList());
    }

    @PostMapping
    @ResponseBody
    public UserIdDTO create(@RequestBody UserCreationDTO userDTO) {
        User user = mapper.toUser(userDTO);

        userDTO.getRoles()
          .stream()
          .map(role -> roleService.getOrCreate(role))
          .forEach(user::addRole);

        userService.save(user);

        return new UserIdDTO(user.getId());
    }
}
```

- `GET /users` → maps each `User` domain object → `UserDTO` (no password exposed)
- `POST /users` → maps `UserCreationDTO` → `User` domain object → saves → returns `UserIdDTO`

---

### 5.3 The Mapper Component

The mapper ensures that the DTO and domain model **never need to know about each other**:

```java
@Component
class Mapper {

    public UserDTO toDto(User user) {
        String name = user.getName();
        List<String> roles = user.getRoles()
          .stream()
          .map(Role::getName)
          .collect(toList());

        return new UserDTO(name, roles);
    }

    public User toUser(UserCreationDTO userDTO) {
        return new User(userDTO.getName(), userDTO.getPassword(), new ArrayList<>());
    }
}
```

The mapper is the single point responsible for translating between the two sides — keeping DTOs and domain models fully decoupled.

---

## 6. Common Mistakes

### 1. Creating a different DTO for every occasion
Leads to an explosion of classes and mappers to maintain. Evaluate whether an existing DTO can be reused before creating a new one.

### 2. Using a single DTO class for too many scenarios
Leads to large contracts where many attributes are frequently unused or irrelevant. Keep DTOs focused on a specific use case.

### 3. Adding business logic to DTOs
DTOs exist only to optimise data transfer and define the structure of API contracts. All business logic belongs in the domain layer — never in the DTO.

### 4. LocalDTOs (passing DTOs across internal domain boundaries)
Using DTOs to pass data between internal domains (not just between client and server) increases mapping code significantly and raises maintenance costs. The encapsulation benefit LocalDTOs are said to provide is better achieved by properly decoupling the domain model from the persistence model — at which point the risk of accidentally exposing internal models is already eliminated.

> For more complex scenarios requiring similar separation, consider patterns like CQRS, Data Mappers, or Command Query Separation.

---

## 7. Summary

| Aspect | Detail |
|---|---|
| Purpose | Reduce network round trips; decouple presentation from domain |
| Structure | Flat POJO — fields, getters/setters, optional serialisation methods |
| Business logic | None — belongs in the domain layer |
| Mapping | Handled by a dedicated mapper component in the presentation/facade layer |
| When to use | Remote calls, multi-object domain, client-specific views, sensitive field hiding |
| Key benefit | Domain model and presentation layer can evolve independently |
| Key risk | Too many DTOs, or too few (overloaded), or business logic creeping in |

---