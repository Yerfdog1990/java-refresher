# Error Handling for REST with Spring

## 1. Overview

Error handling is a fundamental part of designing robust REST APIs.

In Spring, exception handling follows an important principle:

> **Separation of Concerns**
> The application throws exceptions normally to indicate failure, and error handling is performed separately.

Spring provides multiple mechanisms to implement REST error handling:

1. `@ExceptionHandler`
2. `@ControllerAdvice` / `@RestControllerAdvice`
3. `@ResponseStatus` on exceptions
4. `ResponseStatusException`
5. `HandlerExceptionResolver`
6. Spring Boot’s built-in error handling

All of these can be combined depending on your architecture.

---

# 2. `@ExceptionHandler`

`@ExceptionHandler` allows us to define methods that Spring automatically invokes when a specific exception occurs.

We can:

* Declare the exception type in the annotation
* Or declare it as a method parameter
* Return:

    * An object (converted to JSON/XML)
    * A `ResponseEntity`
    * A `ProblemDetail`
    * A view (in MVC apps)
* Use `@ResponseStatus` to set the HTTP status
* Since Spring 6.2: support content negotiation in exception handlers

---

## 2.1 Simplest Example

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(CustomException1.class)
public void handleException1() {
}
```

Behavior:

* Returns HTTP 400
* No response body

---

## 2.2 Returning `ProblemDetail` (RFC-9457)

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler
public ProblemDetail handleException2(CustomException2 ex) {

    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Invalid Request");
    problem.setDetail(ex.getMessage());

    return problem;
}
```

Spring automatically sets:

```
Content-Type: application/problem+json
```

Example response:

```json
{
  "type": "about:blank",
  "title": "Invalid Request",
  "status": 400,
  "detail": "Invalid input"
}
```

---

## 2.3 Content-Type Specific Exception Handling (Spring 6.2+)

We can define multiple handlers for the same exception type based on media type.

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(produces = MediaType.APPLICATION_JSON_VALUE)
public CustomExceptionObject handleException3Json(CustomException3 ex) {
    return new CustomExceptionObject(ex.getMessage(), "JSON error");
}
```

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(produces = MediaType.TEXT_PLAIN_VALUE)
public String handleException3Text(CustomException3 ex) {
    return "Error: " + ex.getMessage();
}
```

Spring performs content negotiation based on the `Accept` header.

---

## 2.4 Handling Multiple Exception Types

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler({
    CustomException4.class,
    CustomException5.class
})
public ResponseEntity<CustomExceptionObject> handleException45(Exception ex) {

    CustomExceptionObject body =
            new CustomExceptionObject(ex.getMessage(), "Combined handler");

    return ResponseEntity.badRequest().body(body);
}
```

If you need exception details, use a shared superclass (`Exception`, `RuntimeException`, etc.).

---

# 3. Local Exception Handling (Controller-Level)

Exception handlers can be defined inside a controller.

```java
@RestController
@RequestMapping("/api/foo")
public class FooController {

    @GetMapping("/{id}")
    public String getFoo(@PathVariable Long id) {
        throw new CustomException1("Invalid ID");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException1.class)
    public ProblemDetail handleException(CustomException1 ex) {

        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setDetail(ex.getMessage());
        return problem;
    }
}
```

### When to Use

✔ Controller-specific behavior

❌ Not reusable across controllers

If reuse is needed, prefer `@ControllerAdvice`.

---

# 4. Global Exception Handling

`@ControllerAdvice` allows sharing exception handling across controllers.

For REST APIs, use:

```
@RestControllerAdvice
```

---

## 4.1 Basic Global Handler

```java
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException1.class)
    public ProblemDetail handleException(CustomException1 ex) {

        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setDetail(ex.getMessage());

        return problem;
    }
}
```

Now all controllers are covered.

---

## 4.2 Extending `ResponseEntityExceptionHandler`

Spring provides a base class with built-in MVC exception handling.

```java
@ControllerAdvice
public class MyCustomResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    ResponseEntity<Object> handleConflict(
            RuntimeException ex,
            WebRequest request) {

        String body = "Application specific conflict";

        return handleExceptionInternal(
                ex,
                body,
                new HttpHeaders(),
                HttpStatus.CONFLICT,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body("Custom media type error");
    }
}
```

Why extend this class?

* Built-in support for common Spring MVC exceptions
* Built-in `ProblemDetail` support
* Centralized customization

---

# 5. Annotating Exceptions Directly

We can annotate custom exceptions with `@ResponseStatus`.

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyResourceNotFoundException extends RuntimeException {

    public MyResourceNotFoundException(String message) {
        super(message);
    }
}
```

If thrown:

```java
throw new MyResourceNotFoundException("Resource not found");
```

Spring sets:

* HTTP 404
* Empty body

### Limitations

* Cannot control body content
* Only works for custom exceptions
* Not ideal for complex APIs

Use only for simple boundary exceptions.

---

# 6. `ResponseStatusException`

A controller can throw `ResponseStatusException`.

```java
@GetMapping("/{id}")
public Foo findById(@PathVariable Long id) {

    try {
        return service.findById(id);
    } catch (MyResourceNotFoundException ex) {

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Foo Not Found",
                ex
        );
    }
}
```

---

## Benefits

1. [ ] ✔ Excellent for prototyping
2. [ ] ✔ One exception type → multiple statuses
3. [ ] ✔ Less coupling than `@ExceptionHandler`
4. [ ] ✔ No need for many custom exception classes

---

## Tradeoffs

1. [ ] ❌ No global enforcement
2. [ ] ❌ Code duplication across controllers
3. [ ] ❌ Wrapping may be required in layered architecture

Best used at controller boundary.

---

# 7. `HandlerExceptionResolver`

Lowest-level customization mechanism.

Allows full control over:

* Status
* Headers
* Body
* Media type

---

## 7.1 Existing Resolvers (Enabled by Default)

1. `ExceptionHandlerExceptionResolver`
   → powers `@ExceptionHandler`

2. `ResponseStatusExceptionResolver`
   → powers `@ResponseStatus` and `ResponseStatusException`

3. `DefaultHandlerExceptionResolver`
   → maps common Spring exceptions to status codes (4xx, 5xx)
   → does NOT populate body

---

## 7.2 Custom `HandlerExceptionResolver`

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
                        (IllegalArgumentException) ex,
                        request,
                        response
                );
            }
        } catch (Exception handlerException) {
            logger.warn("Exception handling failed", handlerException);
        }

        return null;
    }

    private ModelAndView handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setStatus(HttpServletResponse.SC_CONFLICT);

        String accept = request.getHeader(HttpHeaders.ACCEPT);

        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"" + ex.getMessage() + "\"}");
        }

        return new ModelAndView();
    }
}
```

### Advantages

1. [ ] ✔ Full control
2. [ ] ✔ Can inspect `Accept` header
3. [ ] ✔ Uniform error structure

### Limitations

1. [ ] ❌ Low-level API
2. [ ] ❌ Uses `ModelAndView`
3. [ ] ❌ More complex than `@ControllerAdvice`

Use only when necessary.

---

# 8. Handling Common REST Exceptions

## 8.1 AccessDeniedException (Spring Security)

```java
@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException() {

        ProblemDetail problem = ProblemDetail.forStatus(403);
        problem.setTitle("Access Denied");
        problem.setDetail("You do not have permission.");

        return problem;
    }
}
```

---

## 8.2 Validation Exceptions

Handle:

* `ConstraintViolationException`
* `MethodArgumentNotValidException`

Example:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ProblemDetail> handleValidation(
        MethodArgumentNotValidException ex) {

    ProblemDetail problem = ProblemDetail.forStatus(400);
    problem.setTitle("Validation Failed");

    return ResponseEntity.badRequest().body(problem);
}
```

---

## 8.3 Data Layer Exceptions

Handle:

* `DataAccessException`
* `PersistenceException`

---

# 9. Spring Boot Support

Spring Boot provides a default `ErrorController`.

For REST requests:

```json
{
  "timestamp": "2019-01-17T16:12:45.977+0000",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error processing the request!",
  "path": "/endpoint"
}
```

---

## 9.1 Configuration Properties

```properties
server.error.whitelabel.enabled=false
server.error.include-stacktrace=always
server.error.include-message=always
```

---

## 9.2 Custom Error Attributes

```java
@Component
public class MyCustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(
            WebRequest webRequest,
            ErrorAttributeOptions options) {

        Map<String, Object> errorAttributes =
                super.getErrorAttributes(webRequest, options);

        errorAttributes.put("locale",
                webRequest.getLocale().toString());

        errorAttributes.remove("error");

        return errorAttributes;
    }
}
```

---

## 9.3 Custom `ErrorController`

```java
@Component
public class MyErrorController extends BasicErrorController {

    public MyErrorController(
            ErrorAttributes errorAttributes,
            ServerProperties serverProperties) {

        super(errorAttributes, serverProperties.getError());
    }

    @RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Map<String, Object>> xmlError(
            HttpServletRequest request) {

        Map<String, Object> body = getErrorAttributes(
                request,
                ErrorAttributeOptions.defaults()
        );

        return ResponseEntity.status(500).body(body);
    }
}
```

---

# 10. Comparison of Approaches

| Approach                   | Scope          | Body Control | Best For               |
| -------------------------- | -------------- | ------------ | ---------------------- |
| `@ExceptionHandler`        | Local/Global   | Full         | Most REST APIs         |
| `@ControllerAdvice`        | Global         | Full         | Production APIs        |
| `@ResponseStatus`          | Per Exception  | None         | Simple cases           |
| `ResponseStatusException`  | Per Controller | Limited      | Prototyping            |
| `HandlerExceptionResolver` | Global         | Full         | Advanced customization |
| Spring Boot Default        | Global         | Limited      | Quick setup            |

---

# 11. Recommended REST Strategy

For production REST APIs:

1. [ ] ✔ Use `@RestControllerAdvice` globally
2. [ ] ✔ Return `ProblemDetail`
3. [ ] ✔ Use `ResponseEntity` for full control
4. [ ] ✔ Handle:

* Validation errors
* Security exceptions
* Data access exceptions
  ✔ Avoid leaking stack traces

---

# 12. Final Thoughts

Spring provides layered error handling mechanisms:

* Annotation-driven (`@ExceptionHandler`)
* Declarative (`@ResponseStatus`)
* Programmatic (`ResponseStatusException`)
* Low-level (`HandlerExceptionResolver`)
* Boot auto-configured (`ErrorController`)

You can combine approaches:

Example:

* Global `@RestControllerAdvice`
* Occasional `ResponseStatusException` in controllers
* Custom `ErrorAttributes` in Boot

The key is consistency.

A well-designed REST API:

* Returns meaningful status codes
* Uses structured error responses (RFC 9457)
* Hides internal details
* Maintains separation of concerns
* Provides predictable error contracts

That is production-grade REST error handling with Spring.

---