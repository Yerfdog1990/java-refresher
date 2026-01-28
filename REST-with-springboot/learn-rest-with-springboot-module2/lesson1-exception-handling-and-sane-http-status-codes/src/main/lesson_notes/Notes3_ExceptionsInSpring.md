
---

# Exception Handling in Spring MVC

## 1. Introduction to Exceptions in Spring

Exception handling is a fundamental part of request processing in Spring MVC. During request execution, failures may occur at various stages, such as:

* Controller method execution
* Validation
* Data access
* Security checks
* File or network operations

Spring’s exception handling mechanism is designed to ensure **separation of concerns**:

* Application code throws exceptions normally
* Controllers remain focused on request handling
* Dedicated infrastructure translates exceptions into HTTP responses

Spring MVC provides first-class support for exception handling through:

* `@ExceptionHandler`
* `@ControllerAdvice` / `@RestControllerAdvice`
* Content negotiation
* The `HandlerExceptionResolver` mechanism

> All concepts discussed here have **equivalent support in the Reactive stack (Spring WebFlux)**.

---

## 2. `@ExceptionHandler` Methods

### 2.1 Basic Concept

Controllers annotated with `@Controller` or `@RestController`, as well as advice classes annotated with `@ControllerAdvice`, may define methods annotated with `@ExceptionHandler`.

Such methods are invoked automatically when an exception occurs during the execution of a controller method.

### Example

```java
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

### Explanation

* The method handles `IOException` thrown by any handler method in the controller
* It returns a `ResponseEntity`, allowing full control over:

    * HTTP status code
    * Headers
    * Response body
* The exception is handled **after propagation**, not inline

---

## 3. Exception Mapping and Matching Rules

### 3.1 Root Exception vs Cause Exception Matching

An exception handled by Spring may be:

* A **top-level exception** (directly thrown)
* A **nested cause** inside another exception

Since Spring **5.3**, exception matching can occur at **arbitrary depth** in the cause chain.

Example scenario:

```text
IllegalStateException
 └── RuntimeException
     └── IOException
         └── FileSystemException
```

Spring can match `FileSystemException`, even if wrapped several levels deep.

---

### 3.2 Declaring Exception Types in Method Arguments

Spring recommends declaring the **target exception type** directly as a method parameter:

```java
@ExceptionHandler({ FileSystemException.class, RemoteException.class })
public ResponseEntity<String> handleIoException(IOException ex) {
    return ResponseEntity
        .internalServerError()
        .body(ex.getMessage());
}
```

Advantages:

* Precise matching
* Direct access to exception details
* Reduced ambiguity

---

### 3.3 Using Generic Method Arguments

Alternatively, exception types may be listed in the annotation while the method parameter is generic:

```java
@ExceptionHandler({ FileSystemException.class, RemoteException.class })
public ResponseEntity<String> handleExceptions(Exception ex) {
    return ResponseEntity
        .internalServerError()
        .body(ex.getMessage());
}
```

⚠ **Important distinction**

* If the exception is thrown directly, `ex` is the actual matching exception
* If wrapped, `ex` is the wrapper exception
* The root cause must be retrieved manually using `ex.getCause()`

---

### 3.4 Root vs Cause Selection Algorithm

When multiple handlers match, Spring uses `ExceptionDepthComparator` to determine the best match.

Rules:

* A **closer match** to the thrown exception is preferred
* Root exception matches are preferred over cause matches
* Matching occurs **within a single controller or advice class**

---

## 4. Best Practices for Exception Matching

Spring strongly recommends:

* Declaring **specific exception types** in method signatures
* Avoiding large, multi-exception handler methods
* Splitting handlers for clarity and predictability
* Minimizing ambiguity between root and cause exceptions

> Clear exception signatures lead to predictable resolution.

---

## 5. Multiple `@ControllerAdvice` Beans

In complex applications, multiple `@ControllerAdvice` classes may exist.

### Ordering Behavior

* Advice beans can be prioritized using `@Order`
* Resolution occurs **within the highest-priority advice first**
* A cause match in a higher-priority advice may override a root match in a lower-priority advice

### Recommendation

> Declare **root exception mappings** in the highest-priority `@ControllerAdvice`.

---

## 6. Rethrowing Exceptions from Handlers

An `@ExceptionHandler` method may choose **not to handle** a given exception by rethrowing it:

```java
@ExceptionHandler(SomeException.class)
public void handle(SomeException ex) {
    throw ex;
}
```

This allows:

* Conditional handling
* Context-specific logic
* Delegation to other handlers

Once rethrown, the exception continues through the remaining resolution chain.

---

## 7. Underlying Infrastructure

Exception handling in Spring MVC is implemented at the **DispatcherServlet level**, using the `HandlerExceptionResolver` abstraction.

Key resolvers include:

* `ExceptionHandlerExceptionResolver`
* `ResponseStatusExceptionResolver`
* `DefaultHandlerExceptionResolver`

These resolvers form a chain that determines how an exception is ultimately translated into an HTTP response.

---

## 8. Media Type Mapping in Exception Handling

In addition to exception types, `@ExceptionHandler` methods can declare **producible media types**.

This allows error responses to vary depending on the client’s `Accept` header.

### Example: JSON vs HTML Responses

```java
@ExceptionHandler(produces = "application/json")
public ResponseEntity<ErrorMessage> handleJson(IllegalArgumentException ex) {
    return ResponseEntity
        .badRequest()
        .body(new ErrorMessage(ex.getMessage(), 42));
}

@ExceptionHandler(produces = "text/html")
public String handleHtml(IllegalArgumentException ex, Model model) {
    model.addAttribute("error", new ErrorMessage(ex.getMessage(), 42));
    return "errorView";
}
```

### Key Points

* Methods are **not considered duplicates**
* Content negotiation determines the chosen handler
* Multiple media types can be declared per handler

---

## 9. Supported Method Arguments

`@ExceptionHandler` methods support a wide range of arguments:

| **Method Argument**                                  | **Description**                                                                                                                                                                                                               |
| ---------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Exception (or specific exception type)**           | Provides access to the raised exception that triggered the handler. You can use this to inspect the error message, cause, or stack trace.                                                                                     |
| **HandlerMethod**                                    | Gives access to the controller method that raised the exception, including method name, parameters, and annotations. Useful for debugging or logging.                                                                         |
| **WebRequest / NativeWebRequest**                    | Provides generic access to request parameters, headers, and request/session attributes without directly depending on the Servlet API.                                                                                         |
| **jakarta.servlet.ServletRequest / ServletResponse** | Allows access to low-level request and response objects. You may also use more specific types such as `HttpServletRequest`, `HttpServletResponse`, `MultipartRequest`, or `MultipartHttpServletRequest`.                      |
| **jakarta.servlet.http.HttpSession**                 | Enforces the presence of an HTTP session. This argument is never `null`. ⚠️ Session access is not thread-safe; consider enabling `synchronizeOnSession` in `RequestMappingHandlerAdapter` when concurrent access is possible. |
| **java.security.Principal**                          | Represents the currently authenticated user. Can be a specific implementation if the security setup provides one.                                                                                                             |
| **HttpMethod**                                       | Indicates the HTTP method of the request (e.g., GET, POST, PUT, DELETE).                                                                                                                                                      |
| **java.util.Locale**                                 | The locale of the current request, resolved by the most specific configured `LocaleResolver` or `LocaleContextResolver`.                                                                                                      |
| **java.util.TimeZone / java.time.ZoneId**            | The time zone associated with the current request, as resolved by a `LocaleContextResolver`.                                                                                                                                  |
| **java.io.OutputStream / java.io.Writer**            | Provides direct access to the raw response body, as exposed by the Servlet API. Useful for writing custom error responses.                                                                                                    |
| **java.util.Map / Model / ModelMap**                 | Access to the model used for an error response. This model is always empty in exception handling scenarios.                                                                                                                   |
| **RedirectAttributes**                               | Used to specify attributes for redirects, including query parameters and flash attributes that survive a redirect.                                                                                                            |
| **@SessionAttribute**                                | Allows access to specific session attributes (not model attributes stored via `@SessionAttributes`).                                                                                                                          |
| **@RequestAttribute**                                | Allows access to attributes stored in the current request scope.                                                                                                                                                              |

⚠ Session access is not thread-safe by default.

### Return values

| **Return Value**                                | **Description**                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| ----------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **@ResponseBody**                               | The return value is converted using registered `HttpMessageConverter` instances and written directly to the HTTP response body. Commonly used in REST APIs to return JSON or XML error responses.                                                                                                                                                                                                                                                               |
| **HttpEntity<B>, ResponseEntity<B>**            | Indicates that the full HTTP response should be created, including headers, status code, and body. The body is converted via `HttpMessageConverter` instances. `ResponseEntity` is the most commonly used variant.                                                                                                                                                                                                                                              |
| **ErrorResponse, ProblemDetail**                | Used to produce standardized error responses compliant with **RFC 9457**, with structured error details included in the response body. See Spring’s error response support.                                                                                                                                                                                                                                                                                     |
| **String**                                      | Interpreted as a logical view name. The view is resolved using `ViewResolver` implementations and rendered together with the implicit model, which is determined from command objects and `@ModelAttribute` methods. The model can also be enriched programmatically by declaring a `Model` parameter.                                                                                                                                                          |
| **View**                                        | A `View` instance used directly for rendering, together with the implicit model determined from command objects and `@ModelAttribute` methods. The handler may also add attributes to the model via a `Model` parameter.                                                                                                                                                                                                                                        |
| **java.util.Map, org.springframework.ui.Model** | Attributes are added to the implicit model. The view name is implicitly determined by a `RequestToViewNameTranslator`.                                                                                                                                                                                                                                                                                                                                          |
| **@ModelAttribute**                             | The return value is added as a model attribute. The view name is implicitly determined by a `RequestToViewNameTranslator`. Note that `@ModelAttribute` is optional—see *Any other return value* below.                                                                                                                                                                                                                                                          |
| **ModelAndView**                                | Specifies both the view and the model attributes explicitly, and may also include an HTTP response status.                                                                                                                                                                                                                                                                                                                                                      |
| **void**                                        | A method with a `void` return type (or a `null` return value) is considered to have fully handled the response if it declares a `ServletResponse` or `OutputStream` parameter, or is annotated with `@ResponseStatus`. The same applies if the controller has performed a positive ETag or `lastModified` check. If none of these apply, `void` may indicate **no response body** for REST controllers or **default view name selection** for HTML controllers. |
| **Any other return value**                      | If the return value does not match any of the above and is **not a simple type** (as determined by `BeanUtils#isSimpleProperty`), it is treated as a model attribute and added to the model. If it *is* a simple type, it remains unresolved.                                                                                                                                                                                                                   |

---

## 10. Supported Return Values

`@ExceptionHandler` methods can return:

### REST Responses

* `@ResponseBody`
* `ResponseEntity<T>`
* `HttpEntity<T>`
* `ErrorResponse`
* `ProblemDetail` (RFC 9457)

### MVC Responses

* `String` (view name)
* `View`
* `ModelAndView`
* `Model`
* `Map`

### Special Cases

* `void` (response fully handled)
* Any non-simple object → treated as a model attribute

---

## 11. RFC 9457 – Problem Details

Spring supports **RFC 9457 Problem Details**, enabling standardized error responses:

* Machine-readable
* Consistent across APIs
* Automatically sets:

  ```
  Content-Type: application/problem+json
  ```

Recommended for modern REST APIs.

---

## 12. Summary

Spring MVC provides a **powerful and flexible exception-handling model**:

* Precise exception matching
* Root and cause resolution
* Media-type–aware error responses
* Rich method signatures
* Integration with REST and MVC
* Centralized handling via advice classes

### Final Recommendation

> Use **specific `@ExceptionHandler` methods in a global `@ControllerAdvice`**,
> leverage **content negotiation**,
> and rely on **ProblemDetail** for REST APIs.

---

