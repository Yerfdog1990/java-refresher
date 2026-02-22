# Spring Exceptions in Spring MVC

In Spring MVC, exception handling is integrated into the request-processing lifecycle and built on top of the **`DispatcherServlet`** and the **`HandlerExceptionResolver`** mechanism.

Spring provides powerful and flexible support for:

* Handling exceptions locally within a controller
* Handling exceptions globally with `@ControllerAdvice`
* Matching root vs nested causes
* Performing media type–based error negotiation
* Returning structured REST error responses (`ResponseEntity`, `ProblemDetail`)
* Rendering HTML error views

---

# 1. Basic Exception Handling with `@ExceptionHandler`

Both `@Controller` and `@ControllerAdvice` classes can define methods annotated with `@ExceptionHandler`.

These methods are invoked when an exception is thrown from a controller method.

---

## Example: Local Exception Handler in a Controller

```java
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class SimpleController {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handle() {
        return ResponseEntity
                .internalServerError()
                .body("Could not read file storage");
    }
}
```

### What Happens Here?

* If an `IOException` is thrown by any method in `SimpleController`,
* The `handle()` method is invoked.
* A `500 Internal Server Error` response is returned.
* The response body is written using `HttpMessageConverters`.

---

# 2. Exception Mapping (Root vs Cause Matching)

Spring can match exceptions in two ways:

1. **Root exception** (directly thrown)
2. **Nested cause** (wrapped inside another exception)

### Example:

```java
throw new IllegalStateException(new IOException("Disk error"));
```

As of Spring 5.3:

* Spring can match at **any cause depth**
* Not only the immediate cause

---

## Matching Rules

When multiple exception handlers match:

* A **root match** is preferred over a cause match
* Within the same class, Spring uses `ExceptionDepthComparator`
* It chooses the exception closest in the inheritance hierarchy

---

## Recommended Practice

✔ Be as specific as possible in the method argument.

❌ Avoid overly generic signatures unless intentional.

---

# 3. Multiple Exception Types

You can narrow matching through the annotation:

---

## Example: Narrowing in Annotation

```java
import java.nio.file.FileSystemException;
import java.rmi.RemoteException;

@ExceptionHandler({FileSystemException.class, RemoteException.class})
public ResponseEntity<String> handleIoException(IOException ex) {
    return ResponseEntity
            .internalServerError()
            .body(ex.getMessage());
}
```

Here:

* The handler only matches `FileSystemException` and `RemoteException`
* The method argument is `IOException`
* Since both extend `IOException`, this works

---

## More Generic Variant

```java
@ExceptionHandler({FileSystemException.class, RemoteException.class})
public ResponseEntity<String> handleExceptions(Exception ex) {
    return ResponseEntity
            .internalServerError()
            .body(ex.getMessage());
}
```

### Important Distinction

* If the exception is wrapped:

    * `handle(IOException)` may receive the wrapper if it is also an `IOException`
    * `handle(Exception)` will always receive the wrapper
* The actual cause must then be accessed via:

```java
ex.getCause();
```

---

## Best Practice Recommendation

Prefer:

```java
@ExceptionHandler(FileSystemException.class)
public ResponseEntity<String> handleFileSystem(FileSystemException ex)
```

Instead of:

```java
@ExceptionHandler(Exception.class)
```

Be precise to avoid ambiguity between root and cause matching.

---

# 4. Global Exception Handling with `@ControllerAdvice`

For centralized error handling:

```java
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
```

Now:

* All controllers are covered
* Exception handling logic is centralized

---

## Ordering Multiple `@ControllerAdvice`

If you define multiple advice classes:

```java
@ControllerAdvice
@Order(1)
public class PrimaryExceptionHandler { }

@ControllerAdvice
@Order(2)
public class SecondaryExceptionHandler { }
```

Important:

* A **cause match in a higher-priority advice**
  is preferred over
* A **root match in a lower-priority advice**

---

# 5. Rethrowing Exceptions

An `@ExceptionHandler` method may rethrow the exception:

```java
@ExceptionHandler(IOException.class)
public ResponseEntity<String> handle(IOException ex) throws IOException {

    if (!isRootLevel(ex)) {
        throw ex; // back out
    }

    return ResponseEntity.internalServerError().body("Root IO error");
}
```

This allows:

* Selective handling
* Delegation to other resolvers
* Context-based matching

The exception continues through the resolution chain as if it had never matched.

---

# 6. Media Type Mapping (Content Negotiation)

Spring allows multiple handlers for the same exception type
based on the **Accept header**.

---

## Example: JSON vs HTML Error Response

```java
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

@ExceptionHandler(value = IllegalArgumentException.class, produces = "application/json")
public ResponseEntity<ErrorMessage> handleJson(IllegalArgumentException ex) {

    return ResponseEntity
            .badRequest()
            .body(new ErrorMessage(ex.getMessage(), 42));
}

@ExceptionHandler(value = IllegalArgumentException.class, produces = "text/html")
public String handleHtml(IllegalArgumentException ex, Model model) {

    model.addAttribute("error",
            new ErrorMessage(ex.getMessage(), 42));

    return "errorView";
}
```

### Behavior

* `Accept: application/json` → JSON response
* `Accept: text/html` → HTML error view

Spring performs content negotiation during error handling.

---

# 7. Supported Method Arguments

`@ExceptionHandler` methods can accept many types.

---

## Common Arguments

| Argument             | Purpose                     |
| -------------------- | --------------------------- |
| `Exception`          | Access thrown exception     |
| `HandlerMethod`      | Access controller method    |
| `WebRequest`         | Generic request access      |
| `HttpServletRequest` | Direct servlet access       |
| `HttpSession`        | Access session (never null) |
| `Principal`          | Authenticated user          |
| `HttpMethod`         | HTTP verb                   |
| `Locale`             | Request locale              |
| `ZoneId`             | Time zone                   |
| `OutputStream`       | Raw response output         |
| `Model`              | Model attributes            |
| `RedirectAttributes` | Redirect + flash attributes |
| `@SessionAttribute`  | Session attribute           |
| `@RequestAttribute`  | Request attribute           |

---

## Example: Using Multiple Arguments

```java
@ExceptionHandler(IllegalStateException.class)
public ResponseEntity<String> handle(
        IllegalStateException ex,
        HttpServletRequest request,
        Principal principal) {

    String message = "User: " + principal.getName() +
                     " failed at URI: " + request.getRequestURI();

    return ResponseEntity.status(500).body(message);
}
```

---

# 8. Supported Return Types

Spring supports many return types from `@ExceptionHandler`.

---

## 1. `@ResponseBody`

```java
@ExceptionHandler(CustomException.class)
@ResponseBody
public ErrorMessage handle(CustomException ex) {
    return new ErrorMessage(ex.getMessage(), 1001);
}
```

---

## 2. `ResponseEntity`

```java
@ExceptionHandler(CustomException.class)
public ResponseEntity<ErrorMessage> handle(CustomException ex) {
    return ResponseEntity
            .status(422)
            .body(new ErrorMessage(ex.getMessage(), 1001));
}
```

---

## 3. `ProblemDetail` (RFC 9457)

Modern structured REST error:

```java
import org.springframework.http.ProblemDetail;

@ExceptionHandler(IllegalArgumentException.class)
public ProblemDetail handle(IllegalArgumentException ex) {

    ProblemDetail problem = ProblemDetail
            .forStatus(400);

    problem.setTitle("Invalid Request");
    problem.setDetail(ex.getMessage());

    return problem;
}
```

Spring automatically renders:

```json
{
  "type": "about:blank",
  "title": "Invalid Request",
  "status": 400,
  "detail": "Invalid parameter"
}
```

---

## 4. Returning a View

```java
@ExceptionHandler(IllegalArgumentException.class)
public String handle(IllegalArgumentException ex, Model model) {

    model.addAttribute("error", ex.getMessage());

    return "errorPage";
}
```

---

## 5. `ModelAndView`

```java
@ExceptionHandler(Exception.class)
public ModelAndView handle(Exception ex) {

    ModelAndView mav = new ModelAndView("errorView");
    mav.addObject("error", ex.getMessage());
    mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

    return mav;
}
```

---

## 6. `void`

If you directly write to response:

```java
@ExceptionHandler(IOException.class)
public void handle(IOException ex, HttpServletResponse response) throws IOException {

    response.setStatus(500);
    response.getWriter().write("IO error occurred");
}
```

If none of the special cases apply:

* For REST → no body
* For HTML → default view name selection

---

# 9. Default Behavior for Other Return Types

If the return value:

* Is **not simple**
* Is **not matched to known types**

Then Spring treats it as a **model attribute**.

If it is a simple type (String, int, etc.) and not matched, it remains unresolved.

---

# 10. Full REST Example (Best Practice Pattern)

Here is a realistic REST API exception strategy:

---

## Custom Exception

```java
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

---

## Controller

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {

        if (id == 99) {
            throw new ResourceNotFoundException("User not found");
        }

        return new UserDto(id, "John");
    }
}
```

---

## Global Exception Handler

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            ResourceNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(404);
        problem.setTitle("Resource Not Found");
        problem.setDetail(ex.getMessage());

        return ResponseEntity.status(404).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {

        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Unexpected error occurred");

        return ResponseEntity.status(500).body(problem);
    }
}
```

---

# 11. Architecture Summary

Spring exception handling:

* Operates at `DispatcherServlet` level
* Uses `HandlerExceptionResolver`
* Supports:

    * Local handling
    * Global handling
    * Media-type negotiation
    * Root vs cause resolution
    * RFC 9457 error responses
    * View-based or REST-based error strategies

---

# 12. Best Practices for REST APIs

1. [ ] ✔ Use `@RestControllerAdvice` for APIs
2. [ ] ✔ Return `ProblemDetail` for structured errors
3. [ ] ✔ Be specific in exception signatures
4. [ ] ✔ Avoid catching `Exception` unless necessary
5. [ ] ✔ Use `ResponseEntity` when full HTTP control is required
6. [ ] ✔ Order multiple `@ControllerAdvice` beans explicitly

---

# Final Takeaway

Spring exception handling is:

* Type-safe
* Flexible
* Content-negotiation aware
* Deeply integrated into the MVC pipeline

It allows you to design:

* Clean REST error contracts
* Consistent API error responses
* Layered and prioritized exception strategies
* Production-grade, HTTP-aware failure handling

This is a core part of building robust RESTful APIs in Spring MVC.

---