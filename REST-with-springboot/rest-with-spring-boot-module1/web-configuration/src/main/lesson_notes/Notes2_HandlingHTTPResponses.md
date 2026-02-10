
---

# Returning Responses in Spring REST

## Implicit HTTP vs Fixed HTTP Contract vs Full HTTP Control

---

## 1. Goal

In this lesson, we explore **three different ways Spring REST controllers return HTTP responses** and how each approach maps to **different levels of HTTP control**.

By the end of this lesson, learners should be able to:

* Distinguish between implicit and explicit HTTP behavior
* Choose the correct response strategy for a REST endpoint
* Understand the trade-offs between simplicity and control
* Apply REST principles consistently in Spring controllers

---

## 2. Why Response Handling Matters in REST

In REST, **HTTP is part of the application contract**, not just a transport mechanism.

A REST API communicates meaning not only through:

* JSON bodies

but also through:

* HTTP status codes
* HTTP headers
* Media types
* Caching directives

Spring gives us **multiple ways** to construct responses, but they are **not equivalent** in terms of REST clarity and scalability.

---

## 3. The Three Levels of HTTP Control in Spring REST

Spring REST response handling can be understood as **three tiers**:

1. Implicit HTTP behavior
2. Fixed HTTP contract
3. Full HTTP control

Each tier has a valid use case.

---

## 4. Implicit HTTP Behavior (Returning the DTO Directly)

### Example

```java
@PostMapping
public UserDto create(@RequestBody UserDto newUser) {
    User model = UserDto.Mapper.toModel(newUser);
    User createdModel = userService.save(model);
    return UserDto.Mapper.toDto(createdModel);
}
```

### What Spring does implicitly

* Serializes `UserDto` to JSON
* Sets status code to `200 OK`
* Sets `Content-Type` automatically
* Adds no custom headers

### Characteristics

* HTTP response is inferred by Spring
* Status code is implicit
* No explicit REST intent in the method signature

### When this is acceptable

* Simple `GET` endpoints
* Default `200 OK` responses
* No headers or alternative status codes needed

```java
@GetMapping("/{id}")
public UserDto getUser(@PathVariable Long id) {
    User model = userService.findById(id);
    return UserDto.Mapper.toDto(model);
}
```

### Limitations

* Cannot express `201 Created`, `204 No Content`, etc.
* HTTP semantics are hidden
* Poor fit for write operations (`POST`, `DELETE`)

### REST takeaway

> Implicit HTTP is convenient, but REST intent is invisible.

---

## 5. Fixed HTTP Contract (`@ResponseStatus` + Return Body)

### Example

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public UserDto create(@RequestBody UserDto newUser) {
    User model = UserDto.Mapper.toModel(newUser);
    User createdModel = userService.save(model);
    return UserDto.Mapper.toDto(createdModel);
}
```

### What Spring does

* Serializes the response body
* Sets a **fixed HTTP status code**
* No servlet API involvement

### Why this is better than implicit HTTP

* HTTP status is explicit
* Clean and declarative
* No low-level HTTP manipulation

### Common and correct use cases

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
    userService.delete(id);
}
```

### Strengths

* Very readable
* Strong REST signal
* Good default for many endpoints

### Limitations

* Status code is static
* Cannot add headers
* Cannot branch response logic

### REST takeaway

> This defines a **fixed HTTP contract**: predictable and clean, but limited.

---

## 6. Full HTTP Control (`ResponseEntity<T>`)

### Example

```java
@PostMapping
public ResponseEntity<UserDto> create(@RequestBody UserDto newUser) {
    User model = UserDto.Mapper.toModel(newUser);
    User createdModel = userService.save(model);
    UserDto createdDto = UserDto.Mapper.toDto(createdModel);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdDto);
}
```

### What Spring does

* Uses exactly the status code you specify
* Includes headers you define
* Serializes the body
* Fully respects HTTP semantics

### REST advantages

#### 1. Dynamic status codes

```java
if (userAlreadyExists) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .build();
}
```

#### 2. Proper REST creation semantics

```java
return ResponseEntity
    .created(URI.create("/api/users/" + createdDto.getId()))
    .body(createdDto);
```

#### 3. Header support

* `Location`
* `ETag`
* `Cache-Control`
* Pagination headers

### Trade-off

* Slightly more verbose
* Requires understanding HTTP concepts

### REST takeaway

> `ResponseEntity` gives **full HTTP control**, which is essential for mature REST APIs.

---

## 7. What Not to Do: Servlet API Leakage

### Anti-pattern

```java
@PostMapping
public UserDto create(@RequestBody UserDto newUser,
                      HttpServletResponse response) {

    User model = UserDto.Mapper.toModel(newUser);
    User createdModel = userService.save(model);

    response.setStatus(HttpStatus.CREATED.value());
    return UserDto.Mapper.toDto(createdModel);
}
```

### Why this is discouraged

* Breaks abstraction
* Couples controller to servlet container
* Hides HTTP intent
* Harder to test and maintain

Spring REST should remain **HTTP-aware**, not **Servlet-dependent**.

---

## 8. Comparison Summary

| Approach                | HTTP Status | Headers | REST Clarity|  Scalability |
| ----------------------- | ----------- | ------- |-------------|--------------|
| Return `UserDto`        | Implicit    | ❌      | Low         | Low          |
| `@ResponseStatus` + DTO | Fixed       | ❌      | Medium      | Medium       |
| `ResponseEntity<DTO>`   | Full        | ✅      | High        | High         |

---

## 9. Choosing the Right Response Approach by CRUD Operation

Choosing how to return a response in Spring REST should be driven by **what the endpoint does**, not personal preference.

Each CRUD operation has **different HTTP semantics**, and Spring’s response mechanisms align naturally with them.

---

## 9.1 CREATE (`POST`)

### REST semantics

* Creates a new resource
* Should return `201 Created`
* Often includes a `Location` header
* May return different statuses (`400`, `409`, `422`)

### Recommended approach

✅ **Full HTTP control (`ResponseEntity<T>`)**

```java
@PostMapping
public ResponseEntity<UserDto> create(@RequestBody UserDto newUser) {
    User model = UserDto.Mapper.toModel(newUser);
    User created = userService.save(model);
    UserDto dto = UserDto.Mapper.toDto(created);

    return ResponseEntity
        .created(URI.create("/api/users/" + dto.getId()))
        .body(dto);
}
```

### Why not implicit or fixed?

* Implicit always returns `200 OK` ❌
* Fixed contract cannot handle conflicts or headers ❌

---

## 9.2 READ (`GET`)

### REST semantics

* Retrieves an existing resource
* Usually returns `200 OK`
* No headers required in simple cases

### Recommended approaches

✅ **Implicit HTTP** (simple case)

```java
@GetMapping("/{id}")
public UserDto getById(@PathVariable Long id) {
    return UserDto.Mapper.toDto(userService.findById(id));
}
```

✅ **Full HTTP control** (conditional cases)

```java
@GetMapping("/{id}")
public ResponseEntity<UserDto> getById(@PathVariable Long id) {
    return userService.findOptionalById(id)
        .map(user -> ResponseEntity.ok(UserDto.Mapper.toDto(user)))
        .orElse(ResponseEntity.notFound().build());
}
```

### Key insight

> GET is the **only CRUD operation** where implicit HTTP is often acceptable.

---

## 9.3 UPDATE (`PUT` / `PATCH`)

### REST semantics

* Modifies an existing resource
* Possible outcomes:

    * `200 OK`
    * `204 No Content`
    * `404 Not Found`
    * `409 Conflict`

### Recommended approach

✅ **Full HTTP control (`ResponseEntity<T>`)**

```java
@PutMapping("/{id}")
public ResponseEntity<UserDto> update(
        @PathVariable Long id,
        @RequestBody UserDto updatedUser) {

    if (!userService.exists(id)) {
        return ResponseEntity.notFound().build();
    }

    User model = UserDto.Mapper.toModel(updatedUser);
    User saved = userService.update(id, model);

    return ResponseEntity.ok(UserDto.Mapper.toDto(saved));
}
```

### Why not fixed HTTP?

* Update outcomes are rarely uniform
* REST semantics depend on state

---

## 9.4 DELETE (`DELETE`)

### REST semantics

* Removes a resource
* Common responses:

    * `204 No Content`
    * `404 Not Found`

### Recommended approaches

✅ **Fixed HTTP contract** (simple delete)

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
    userService.delete(id);
}
```

✅ **Full HTTP control** (conditional delete)

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

### Key insight

> DELETE is often **body-less**, making HTTP status the primary response signal.

---

## 9.5 Summary: CRUD vs Response Strategy

| CRUD Operation | Typical HTTP Statuses | Recommended Response Approach |
| -------------- | --------------------- | ----------------------------- |
| CREATE (POST)  | 201, 400, 409         | `ResponseEntity<T>`           |
| READ (GET)     | 200, 404              | Implicit or `ResponseEntity`  |
| UPDATE (PUT)   | 200, 204, 404, 409    | `ResponseEntity<T>`           |
| DELETE         | 204, 404              | Fixed or `ResponseEntity`     |

---

## 9.6 Decision Cheat Sheet

### Use implicit HTTP when

* Simple `GET`
* Always `200 OK`
* No headers needed

### Use fixed HTTP contract when

* Status is always the same
* No headers required
* Clean, declarative REST is desired
* Common for simple `DELETE`

### Use full HTTP control when

* Creating resources (`POST`)
* Updating resources (`PUT`, `PATCH`)
* Conditional responses are required
* Headers matter (`Location`, `ETag`)
* API is public or evolving

---

## Final REST Design Rule

> **CRUD defines HTTP needs. HTTP needs define the response strategy.**

---

## 10. Final Takeaway

Returning responses in Spring REST is not just a technical choice—it is an **architectural decision**.

> The more your API grows, the more explicit your HTTP should become.

Spring gives you flexibility.
Good REST design is about choosing the **right level of control**, not the shortest code.

---


