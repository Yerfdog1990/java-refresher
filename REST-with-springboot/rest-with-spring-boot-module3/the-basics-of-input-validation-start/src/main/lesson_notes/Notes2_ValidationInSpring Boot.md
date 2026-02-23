# Validation in Spring Boot

---

## 1. Overview

Validating user input is a **critical responsibility** in any application — especially in REST APIs where data enters the system from external clients.

Spring Boot provides **strong out-of-the-box support** for validation. While it allows integration with custom validators, the **de-facto standard** is:

* **Hibernate Validator** — the reference implementation of Jakarta Bean Validation (JSR 380)

Spring Boot automatically integrates Hibernate Validator when the proper dependency is included, making validation:

* Declarative
* Clean
* Automatic
* Easy to test

In this guide, we’ll validate domain objects inside a Spring Boot REST application and persist them into an in-memory database.

---

# 2. Maven Dependencies

We’ll build a simple REST application that:

1. Accepts a `User` object
2. Validates it
3. Persists it to an in-memory database (H2)

### Core Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.5.7</version>
</dependency>

<dependency> 
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>3.5.7</version>
</dependency>

<dependency> 
    <groupId>com.h2database</groupId> 
    <artifactId>h2</artifactId>
    <version>2.3.232</version> 
    <scope>runtime</scope>
</dependency>
```

### Why These?

| Dependency                     | Purpose                  |
| ------------------------------ | ------------------------ |
| `spring-boot-starter-web`      | Build REST controllers   |
| `spring-boot-starter-data-jpa` | JPA + repository support |
| `h2`                           | In-memory database       |

---

### Important: Validation Dependency

Since Spring Boot 2.3, validation must be explicitly added:

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-validation</artifactId> 
    <version>3.5.7</version>
</dependency>
```

This starter pulls in:

* Hibernate Validator
* Jakarta Validation API

Once included, validation works automatically.

---

# 3. A Simple Domain Class

Let’s define a simple JPA entity:

```java
@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @NotBlank(message = "Name is mandatory")
    private String name;
    
    @NotBlank(message = "Email is mandatory")
    private String email;
    
    // constructors / getters / setters
}
```

---

## Understanding the Validation

We used:

```java
@NotBlank(message = "Name is mandatory")
```

`@NotBlank` ensures:

* Value is NOT null
* Value is NOT empty
* Value is NOT whitespace only

So:

* `null` ❌
* `""` ❌
* `"   "` ❌
* `"Bob"` ✅

The `message` attribute defines the error message returned when validation fails.

---

## Other Available Constraints

Bean Validation provides many additional constraints:

* `@NotNull`
* `@Email`
* `@Size`
* `@Min`
* `@Max`
* `@Positive`
* `@Past`
* etc.

These can be combined for richer validation rules.

---

# 4. Repository Layer

We use Spring Data JPA for persistence:

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {}
```

This automatically provides:

* save()
* findById()
* findAll()
* delete()
* etc.

No implementation required.

---

# 5. Implementing the REST Controller

Now let’s create a REST endpoint to accept and validate users.

```java
@RestController
public class UserController {

    @PostMapping("/users")
    ResponseEntity<String> addUser(@Valid @RequestBody User user) {
        // persist user
        return ResponseEntity.ok("User is valid");
    }
}
```

---

## The Most Important Part: `@Valid`

```java
@Valid @RequestBody User user
```

This tells Spring Boot:

1. Deserialize JSON → `User`
2. Validate the object using Hibernate Validator
3. If validation fails → throw exception

When validation fails, Spring throws:

```
MethodArgumentNotValidException
```

This happens automatically — no manual validation code required.

---

# 6. Handling Validation Errors

By default, Spring returns a generic error response.

To customize error handling, we use:

```java
@ExceptionHandler
```

---

## Custom Exception Handler

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(MethodArgumentNotValidException.class)
public Map<String, String> handleValidationExceptions(
  MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
    });

    return errors;
}
```

---

## What This Does

1. Intercepts `MethodArgumentNotValidException`
2. Extracts:

    * Field name
    * Error message
3. Returns structured JSON

Example response:

```json
{
  "name": "Name is mandatory",
  "email": "Email is mandatory"
}
```

This produces clean, client-friendly validation responses.

---

# 7. Testing the REST Controller

We test only the web layer using:

```java
@WebMvcTest
@AutoConfigureMockMvc
```

---

## Test Setup

```java
@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @MockBean
    private UserRepository userRepository;
    
    @Autowired
    private MockMvc mockMvc;
}
```

`@WebMvcTest`:

* Loads only the web layer
* Avoids full application startup

---

## Test Valid User

```java
@Test
public void whenPostRequestToUsersAndValidUser_thenCorrectResponse() throws Exception {

    String user = "{\"name\": \"bob\", \"email\" : \"bob@domain.com\"}";

    mockMvc.perform(post("/users")
      .content(user)
      .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk());
}
```

Expected:

* HTTP 200 OK

---

## Test Invalid User

```java
@Test
public void whenPostRequestToUsersAndInValidUser_thenCorrectResponse() throws Exception {

    String user = "{\"name\": \"\", \"email\" : \"bob@domain.com\"}";

    mockMvc.perform(post("/users")
      .content(user)
      .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.name", Is.is("Name is mandatory")));
}
```

Expected:

* HTTP 400 Bad Request
* JSON validation message

---

# 8. Running the Application

Main application:

```java
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandLineRunner run(UserRepository userRepository) {
        return args -> {
            User user1 = new User("Bob", "bob@domain.com");
            User user2 = new User("Jenny", "jenny@domain.com");

            userRepository.save(user1);
            userRepository.save(user2);

            userRepository.findAll().forEach(System.out::println);
        };
    }
}
```

---

## Testing the Endpoint

### Valid Request

POST:

```
http://localhost:8080/users
```

Body:

```json
{
  "name": "Bob",
  "email": "bob@domain.com"
}
```

Response:

```
User is valid
```

---

### Invalid Request

```json
{
  "name": "",
  "email": ""
}
```

Response:

```json
{
  "name": "Name is mandatory",
  "email": "Email is mandatory"
}
```

---

# 9. Complete Validation Flow

Here’s what happens internally:

1. Client sends JSON
2. Spring deserializes JSON → `User`
3. `@Valid` triggers Hibernate Validator
4. If valid:

    * Controller executes normally
5. If invalid:

    * `MethodArgumentNotValidException` thrown
    * `@ExceptionHandler` processes it
    * 400 response returned

All of this requires **no manual validation logic**.

---

# 10. Key Advantages of Validation in Spring Boot

* ✔ Declarative (annotation-based)
* ✔ Automatic execution
* ✔ Clean error handling
* ✔ Standardized (JSR 380)
* ✔ Easy to test
* ✔ Fully integrated with REST

---

# 11. Conclusion

Spring Boot makes validation:

* Simple to implement
* Cleanly separated from business logic
* Automatically triggered via `@Valid`
* Easily customizable via `@ExceptionHandler`

By combining:

* Hibernate Validator
* Spring MVC
* Spring Data JPA
* H2 database

we get a fully working validation pipeline with minimal configuration.

Validation in Spring Boot is powerful, concise, and production-ready — making it essential for building robust REST APIs.

---