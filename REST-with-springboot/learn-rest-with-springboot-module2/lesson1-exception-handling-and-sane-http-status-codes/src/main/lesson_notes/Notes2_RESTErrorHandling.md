

# Error Handling for REST with Spring

---

## 1. Overview

In any real-world REST API, **error handling is just as important as handling successful requests**.

A well-designed REST API must:

* Return meaningful HTTP status codes
* Provide clear, consistent error responses
* Separate **business logic** from **error-handling logic**
* Avoid leaking internal implementation details

Spring provides **multiple mechanisms** to handle exceptions in RESTful applications. While these approaches differ in flexibility and complexity, they all share one key principle:

> **Exceptions are thrown where failures occur, but handled separately.**

This separation of concerns leads to:

* Cleaner controller and service code
* Centralized error-handling policies
* Better API consistency and maintainability

---

### Key Question This Lesson Answers

> **How should a Spring REST API convert Java exceptions into proper HTTP responses?**

---

## 2. `@ExceptionHandler`

The `@ExceptionHandler` annotation allows Spring to automatically invoke a method when a specific exception is thrown.

### Core Idea

* The application throws exceptions normally
* Spring intercepts them
* A matching `@ExceptionHandler` method handles the exception
* The method builds the HTTP response

---

### 2.1 Basic Usage

The simplest exception handler only sets an HTTP status code:

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(CustomException1.class)
public void handleException1() { }
```

📌 **What happens here:**

* If `CustomException1` is thrown
* Spring returns **HTTP 400 Bad Request**
* The response body is empty

This is useful for **simple failure cases** where no additional information is needed.

---

### 2.2 Accessing Exception Details

We can declare the exception as a method parameter to access its details:

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler
public ProblemDetail handleException2(CustomException2 ex) {
    // build RFC-9457 compliant response
}
```

📌 **Important Notes:**

* `ProblemDetail` is Spring’s built-in support for **RFC 9457 (Problem Details for HTTP APIs)**
* Spring automatically sets:

  ```
  Content-Type: application/problem+json
  ```

This approach is ideal for **modern REST APIs** that need structured error responses.

---

### 2.3 Content Negotiation (Spring 6.2+)

Since Spring 6.2, exception handlers can produce **different representations** depending on the requested media type:

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(produces = MediaType.APPLICATION_JSON_VALUE)
public CustomExceptionObject handleException3Json(CustomException3 ex) {
    // JSON response
}
```

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(produces = MediaType.TEXT_PLAIN_VALUE)
public String handleException3Text(CustomException3 ex) {
    // plain text response
}
```

📌 This enables:

* API clients → JSON
* Browsers or tools → text/plain

---

### 2.4 Handling Multiple Exception Types

We can handle multiple exception classes with one method:

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler({
    CustomException4.class,
    CustomException5.class
})
public ResponseEntity<CustomExceptionObject> handleException45(Exception ex) {
    // shared handling logic
}
```

📌 Use the **shared superclass** (`Exception`, `RuntimeException`) if you need flexibility.

---

## 2.5 Local Exception Handling (Controller-Level)

Exception handlers can be placed **inside a controller**:

```java
@RestController
public class FooController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException1.class)
    public void handleException() {
        // controller-specific handling
    }
}
```

### Pros

✅ Useful when:

* The exception is specific to one controller
* Behavior differs per endpoint

### Cons

* ❌ Not reusable across controllers
* ❌ Encourages inheritance-based reuse (not ideal)

---

## 2.6 Global Exception Handling

For REST APIs, **global exception handling is preferred**.

### `@ControllerAdvice` and `@RestControllerAdvice`

* `@ControllerAdvice` → MVC-style controllers
* `@RestControllerAdvice` → REST controllers (automatic `@ResponseBody`)

```java
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException1.class)
    public void handleException() {
        // global handling
    }
}
```

📌 This handler applies to **all controllers** in the application.

---

### Extending `ResponseEntityExceptionHandler`

Spring provides a base class with predefined exception handling logic:

```java
@ControllerAdvice
public class MyCustomResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {

        String body = "This should be application specific";
        return super.handleExceptionInternal(
            ex, body, new HttpHeaders(),
            HttpStatus.CONFLICT, request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // custom behavior
    }
}
```

📌 No `@RestControllerAdvice` is required because:

* Methods return `ResponseEntity`
* Serialization is handled automatically

---

## 3. Annotating Exceptions Directly

Another approach is to annotate **custom exception classes**:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyResourceNotFoundException extends RuntimeException {
}
```

📌 Behavior:

* Spring maps the exception directly to HTTP 404
* Response body is empty

### Limitations

* ❌ No control over response body
* ❌ Only works for **custom exceptions**
* ❌ Not suitable for reusable domain exceptions

📌 Best used for **boundary-level exceptions** (controller or API layer only).

---

## 4. `ResponseStatusException`

Controllers can throw `ResponseStatusException` directly:

```java
@GetMapping("/{id}")
public Foo findById(@PathVariable Long id) {
    try {
        // ...
    } catch (MyResourceNotFoundException ex) {
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Foo Not Found",
            ex
        );
    }
}
```

### Advantages

✅ Fast prototyping
✅ One exception → multiple HTTP statuses
✅ No need for many custom exception classes
✅ Programmatic control

### Trade-offs

* ❌ No centralized policy
* ❌ Code duplication across controllers
* ❌ Not suitable for service or repository layers

📌 Best used **only in controllers**, not deeper layers.

---

## 5. `HandlerExceptionResolver`

A lower-level mechanism that resolves **any exception thrown by the application**.

---

### 5.1 Built-in Resolvers

Spring automatically registers:

1. **ExceptionHandlerExceptionResolver**

  * Powers `@ExceptionHandler`

2. **ResponseStatusExceptionResolver**

  * Powers `@ResponseStatus`

3. **DefaultHandlerExceptionResolver**

  * Maps standard Spring exceptions to HTTP status codes
  * Does **not** set a response body

---

### 5.2 Custom `HandlerExceptionResolver`

To fully control error responses (including body and content type), we can define our own resolver:

```java
@Component
public class RestResponseStatusExceptionResolver
        extends AbstractHandlerExceptionResolver {

    @Override
    protected ModelAndView doResolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        try {
            if (ex instanceof IllegalArgumentException) {
                return handleIllegalArgument(
                    (IllegalArgumentException) ex, response);
            }
        } catch (Exception handlerException) {
            logger.warn("Exception handling failed", handlerException);
        }
        return null;
    }
}
```

📌 Key Features:

* Direct access to `HttpServletRequest`
* Can inspect `Accept` header
* Can return JSON or XML dynamically

### Limitations

* ❌ Low-level API
* ❌ Uses `ModelAndView`
* ❌ Less idiomatic for modern REST APIs

---

## 6. Further Notes

### 6.1 Handling Common Exceptions

Common REST-related exceptions include:

| Exception                      | Cause                 |
| ------------------------------ | --------------------- |
| `AccessDeniedException`        | Authorization failure |
| `ConstraintViolationException` | Bean Validation       |
| `DataAccessException`          | Database issues       |

Example:

```java
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException() {
    }
}
```

---

### 6.2 Spring Boot Support

Spring Boot provides:

* **Whitelabel Error Page** (HTML)
* **JSON error response** for REST APIs

Example JSON:

```json
{
  "timestamp": "2019-01-17T16:12:45.977+0000",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error processing the request!",
  "path": "/my-endpoint-with-exceptions"
}
```

---

### Key Properties

```properties
server.error.whitelabel.enabled=false
server.error.include-stacktrace=never
server.error.include-message=always
```

---

### Custom `ErrorAttributes`

```java
@Component
public class MyCustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(
            WebRequest webRequest,
            ErrorAttributeOptions options) {

        Map<String, Object> attrs =
            super.getErrorAttributes(webRequest, options);

        attrs.put("locale", webRequest.getLocale().toString());
        attrs.remove("error");

        return attrs;
    }
}
```

---

### Custom `ErrorController`

```java
@Component
public class MyErrorController extends BasicErrorController {

    @RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Map<String, Object>> xmlError(
            HttpServletRequest request) {
        // XML-specific error handling
    }
}
```

---

## 7. Conclusion

Spring provides **multiple error-handling strategies** for REST APIs:

| Approach                   | Use Case              |
| -------------------------- | --------------------- |
| `@ExceptionHandler`        | Fine-grained handling |
| `@ControllerAdvice`        | Global policies       |
| `@ResponseStatus`          | Simple mappings       |
| `ResponseStatusException`  | Prototyping           |
| `HandlerExceptionResolver` | Low-level control     |
| Spring Boot Error Handling | Defaults & fallback   |

👉 **Best practice**:

* Use `@RestControllerAdvice` for global consistency
* Use `ProblemDetail` for modern APIs
* Combine approaches when appropriate

---

Love that idea — a **mini-project** is perfect for making exception handling *stick* 👍
Below is a **complete, classroom-ready mini-project** that showcases **realistic REST error handling with Spring**, aligned with everything in your lesson.

---

# Mini-Project: REST API Exception Handling with Spring

## Project Title

**Campaign Management REST API – Exception Handling Module**

---

## 1. Project Overview

In this mini-project, students will build a **RESTful Campaign Management API** and implement **robust, production-style exception handling** using Spring.

The focus is **not CRUD**, but how failures are:

* detected
* represented
* translated into proper HTTP responses

---

## 2. Learning Objectives

By the end of this mini-project, students will be able to:

* Explain why exception handling is critical in REST APIs
* Implement **controller-level** and **global exception handling**
* Use `@RestControllerAdvice` effectively
* Return correct **HTTP status codes**
* Produce **structured error responses** using `ProblemDetail`
* Handle validation, persistence, and security exceptions consistently

---

## 3. Functional Requirements

The API manages **Campaigns**.

### Campaign Fields

* `id` (Long)
* `code` (String, unique)
* `name` (String)
* `description` (String)

---

## 4. API Endpoints

| Method | Endpoint              | Description           |
| ------ | --------------------- | --------------------- |
| GET    | `/api/campaigns/{id}` | Get campaign by ID    |
| POST   | `/api/campaigns`      | Create a new campaign |
| DELETE | `/api/campaigns/{id}` | Delete campaign       |

---

## 5. Exception Scenarios to Handle

Students must handle **all of the following cases**:

| Scenario                | Exception                         | HTTP Status               |
| ----------------------- | --------------------------------- | ------------------------- |
| Campaign not found      | `CampaignNotFoundException`       | 404 NOT FOUND             |
| Duplicate campaign code | `DuplicateCampaignCodeException`  | 409 CONFLICT              |
| Invalid request data    | `MethodArgumentNotValidException` | 400 BAD REQUEST           |
| Illegal delete attempt  | `IllegalStateException`           | 409 CONFLICT              |
| Unexpected error        | `Exception`                       | 500 INTERNAL SERVER ERROR |

---

## 6. Project Structure

```text
com.example.campaignapi
│
├── controller
│   └── CampaignController.java
│
├── dto
│   └── CampaignDto.java
│
├── exception
│   ├── CampaignNotFoundException.java
│   ├── DuplicateCampaignCodeException.java
│
├── handler
│   └── GlobalExceptionHandler.java
│
├── service
│   └── CampaignService.java
│
└── repository
    └── CampaignRepository.java
```

---

## 7. Step-by-Step Implementation

---

### Step 1: Custom Exceptions

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(Long id) {
        super("Campaign with id " + id + " not found");
    }
}
```

```java
public class DuplicateCampaignCodeException extends RuntimeException {
    public DuplicateCampaignCodeException(String code) {
        super("Campaign code already exists: " + code);
    }
}
```

📌 Note:

* Only one exception uses `@ResponseStatus`
* The other will be handled globally

---

### Step 2: Service Layer (Throwing Exceptions)

```java
@Service
public class CampaignService {

    public Campaign findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));
    }

    public Campaign create(Campaign campaign) {
        if (repository.existsByCode(campaign.getCode())) {
            throw new DuplicateCampaignCodeException(campaign.getCode());
        }
        return repository.save(campaign);
    }

    public void delete(Long id) {
        Campaign campaign = findById(id);
        if (!campaign.getTasks().isEmpty()) {
            throw new IllegalStateException(
                "Cannot delete campaign with active tasks"
            );
        }
        repository.delete(campaign);
    }
}
```

📌 This keeps **business logic clean** and exception-focused.

---

### Step 3: Controller (No Error Logic)

```java
@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    @GetMapping("/{id}")
    public CampaignDto getById(@PathVariable Long id) {
        return CampaignDto.Mapper.toDto(service.findById(id));
    }

    @PostMapping
    public CampaignDto create(
            @Valid @RequestBody CampaignDto dto) {
        Campaign model = CampaignDto.Mapper.toModel(dto);
        return CampaignDto.Mapper.toDto(service.create(model));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
```

📌 Controllers **do not**:

* catch exceptions
* decide HTTP status codes

---

## 8. Global Exception Handler

### Step 4: `@RestControllerAdvice`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CampaignNotFoundException.class)
    public ProblemDetail handleNotFound(CampaignNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Campaign Not Found");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(DuplicateCampaignCodeException.class)
    public ProblemDetail handleDuplicate(DuplicateCampaignCodeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Duplicate Campaign Code");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException ex) {

        ProblemDetail pd =
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation Failed");

        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));

        pd.setDetail(errors);
        return pd;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Illegal Operation");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail pd =
            ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal Server Error");
        pd.setDetail("An unexpected error occurred");
        return pd;
    }
}
```

---

## 9. Sample Error Responses

### 404 – Campaign Not Found

```json
{
  "type": "about:blank",
  "title": "Campaign Not Found",
  "status": 404,
  "detail": "Campaign with id 99 not found"
}
```

---

### 409 – Duplicate Campaign Code

```json
{
  "title": "Duplicate Campaign Code",
  "status": 409,
  "detail": "Campaign code already exists: CMP-2026"
}
```

---

### 400 – Validation Error

```json
{
  "title": "Validation Failed",
  "status": 400,
  "detail": "code: must not be blank, name: must not be blank"
}
```

---

## 10. Extension Tasks (Optional)

💡 For stronger students:

1. Add **error codes** (`ERR-404-01`)
2. Support **XML error responses**
3. Log exceptions with SLF4J
4. Add **Spring Security AccessDeniedException handling**
5. Add unit tests for exception handlers

---

## 11. Assessment Criteria

| Criterion                    | Marks |
| ---------------------------- | ----- |
| Correct HTTP status codes    | 20    |
| Global exception handler     | 20    |
| Clean controller logic       | 20    |
| Structured error responses   | 20    |
| Code readability & structure | 20    |

---

## 12. Key Takeaway for Students

> **Exceptions represent failures in business logic,
> error handlers translate failures into HTTP language.**

---


