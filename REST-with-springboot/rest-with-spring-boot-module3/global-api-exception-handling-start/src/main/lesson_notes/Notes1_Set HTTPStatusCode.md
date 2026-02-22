# Using Spring `@ResponseStatus` to Set HTTP Status Code

## 1. Introduction

In **Spring MVC**, there are multiple ways to control the HTTP status code returned in a response. By default:

* If a controller method completes successfully,
* Spring returns **HTTP 200 (OK)**.

However, sometimes we need to:

* Return a different success code (e.g., 201 Created)
* Return a specific error code
* Convert exceptions into proper HTTP responses

One of the simplest ways to achieve this is by using the **`@ResponseStatus`** annotation.

---

# 2. Using `@ResponseStatus` on Controller Methods

The most straightforward usage is directly on a controller method.

## 2.1 Default Behavior (No Annotation)

```java
@RestController
@RequestMapping("/coffee")
public class CoffeeController {

    @GetMapping
    public String getCoffee() {
        return "Here is your coffee!";
    }
}
```

If this method executes successfully, Spring returns:

```
HTTP 200 OK
```

---

## 2.2 Setting a Custom Success Status

We can override the default status using `@ResponseStatus`.

The annotation provides two interchangeable attributes:

* `value`
* `code`

Both define the HTTP status.

### Example: Returning 418 – I'm a Teapot

```java
@RestController
@RequestMapping("/brew")
public class TeaController {

    @GetMapping
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public void teaPot() {
        // Server refuses to brew coffee because it is a teapot
    }
}
```

Response:

```
HTTP 418 I'm a Teapot
```

Spring sets the HTTP status to 418 when the method completes successfully.

---

## 2.3 Returning an Error Status with a Reason

We can also provide a custom error message using the `reason` attribute:

```java
@ResponseStatus(
    value = HttpStatus.BAD_REQUEST,
    reason = "Some parameters are invalid"
)
public void onIllegalArgumentException(IllegalArgumentException exception) {
}
```

This results in:

```
HTTP 400 Bad Request
```

Important behavior:

When we set `reason`, Spring calls:

```java
HttpServletResponse.sendError()
```

This means:

* The container generates an HTML error page.
* It is **not suitable for REST APIs**, where JSON is expected.

⚠️ Because of this behavior, using `reason` is generally discouraged in REST applications.

---

## 2.4 Important Rule

Spring only applies `@ResponseStatus` when:

* The annotated method completes successfully
* No exception is thrown

If an exception occurs, the annotation is ignored unless it is used in an error-handling context.

---

# 3. Using `@ResponseStatus` for Exception Handling

We can use `@ResponseStatus` to convert exceptions into proper HTTP responses.

There are three main approaches:

1. Using `@ExceptionHandler`
2. Using `@ControllerAdvice`
3. Marking the Exception class directly

---

## 3.1 Using `@ExceptionHandler`

We can define an exception handler inside a controller:

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public String findUser(@PathVariable Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        return "User found";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException() {
        // No body returned
    }
}
```

If `IllegalArgumentException` is thrown:

```
HTTP 400 Bad Request
```

Here:

* `@ExceptionHandler` catches the exception
* `@ResponseStatus` sets the HTTP status

---

## 3.2 Using `@ControllerAdvice` (Global Exception Handling)

Instead of defining handlers per controller, we can centralize them:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException() {
    }
}
```

Now:

* Any controller throwing `IllegalArgumentException`
* Automatically results in **HTTP 400**

This is cleaner for larger applications.

---

## 3.3 Marking the Exception Class Itself

When we don’t need dynamic error responses, the simplest approach is annotating the exception class directly.

### Example: Custom Exception

```java
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CustomException extends RuntimeException {
}
```

Now whenever this exception is thrown:

```java
@GetMapping("/test")
public String test() {
    throw new CustomException();
}
```

Spring automatically returns:

```
HTTP 400 Bad Request
```

No `@ExceptionHandler` required.

---

### Important Behavior

When we mark an exception class with `@ResponseStatus`:

* Spring **always** calls `HttpServletResponse.sendError()`
* This happens whether `reason` is set or not
* An HTML error page may be returned

Also:

* Subclasses inherit the same behavior
* Unless they are explicitly annotated with their own `@ResponseStatus`

Example:

```java
public class ExtendedCustomException extends CustomException {
}
```

This will also return:

```
HTTP 400 Bad Request
```

Unless we override it:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExtendedCustomException extends CustomException {
}
```

---

# 4. Key Differences and Considerations

## 4.1 When to Use `@ResponseStatus`

Use it when:

* You need a fixed, known status code
* You want simple exception-to-status mapping
* You do not need a dynamic response body

---

## 4.2 When NOT to Use It

Avoid it when:

* You need structured JSON error responses
* You want dynamic error messages
* You are building a REST API that must return machine-readable responses

In such cases, prefer:

* `@ExceptionHandler` returning a custom object
* `ResponseEntity`
* Global error handling with consistent JSON formatting

---

# 5. Summary

Using `@ResponseStatus`, we can:

* ✔ Set custom HTTP status codes on controller methods
* ✔ Convert exceptions into HTTP responses
* ✔ Define global exception-to-status mappings
* ✔ Annotate exception classes directly

However, remember:

* Setting `reason` triggers `sendError()` → HTML error page
* Annotating exception classes also triggers `sendError()`
* It is best suited for simple status mappings, not complex REST error handling

---

# Final Thought

`@ResponseStatus` is the simplest way to control HTTP status codes in Spring MVC.

It works well for:

* Fixed responses
* Simple exception mapping
* Clear, declarative configuration

For more advanced REST APIs, combine it with structured error bodies and centralized exception handling for professional, production-ready results.

---