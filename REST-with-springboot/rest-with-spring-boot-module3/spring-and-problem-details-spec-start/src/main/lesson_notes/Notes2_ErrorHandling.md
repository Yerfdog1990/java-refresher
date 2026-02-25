# Error Handling in Spring

Spring Boot provides comprehensive support for handling errors in both REST APIs and traditional web applications. It includes sensible defaults, customization options, support for RFC 9457 Problem Details, controller-level handling, custom error pages, and integration with servlet containers.

---

## 1. Default Error Handling in Spring Boot

By default, Spring Boot provides an `/error` mapping that:

* Handles all errors in a sensible way.
* Is registered as a **global error page** in the servlet container.

### 1.1 Response for Different Clients

* **Machine clients**
  Receive a **JSON response** containing:

    * Error details
    * HTTP status
    * Exception message

* **Browser clients**
  Receive a **“whitelabel” error view**, which:

    * Renders the same data in HTML format.
    * Can be customized by adding a `View` that resolves to `error`.

---

## 2. Customizing Default Error Behavior

Spring Boot provides several configuration properties under:

```
spring.web.error.*
```

These properties allow you to customize the default error handling behavior. Refer to the Web Properties section of the Appendix for details.

---

## 3. Replacing the Default Error Handling

You can completely replace Spring Boot’s default behavior in two ways:

### 3.1 Implement `ErrorController`

* Implement the `ErrorController` interface.
* Register a bean definition of that type.

This replaces the default `/error` handling mechanism.

---

### 3.2 Provide a Custom `ErrorAttributes`

* Add a bean of type `ErrorAttributes`.
* Use the existing mechanism.
* Replace the contents of the error response.

This approach customizes the error data without replacing the entire infrastructure.

---

## 4. Extending `BasicErrorController`

`BasicErrorController` can serve as a base class for a custom `ErrorController`.

This is useful when:

* You want to handle a **new content type**.
* By default, it:

    * Handles `text/html` specifically.
    * Provides a fallback for everything else.

### Steps to Extend

1. Extend `BasicErrorController`.
2. Add a public method annotated with `@RequestMapping`.
3. Set the `produces` attribute to the new content type.
4. Register a bean of your custom controller.

---

## 5. RFC 9457 Problem Details Support (Spring Framework 6.0+)

As of Spring Framework 6.0, **RFC 9457 Problem Details** is supported.

Spring MVC can produce error responses using the `application/problem+json` media type.

### Example Problem Details Response

```json
{
  "type": "https://example.org/problems/unknown-project",
  "title": "Unknown project",
  "status": 404,
  "detail": "No project found for id 'spring-unknown'",
  "instance": "/projects/spring-unknown"
}
```

### Enabling Problem Details

Set the following property:

```
spring.mvc.problemdetails.enabled=true
```

This enables structured RFC 9457 error responses.

---

## 6. Customizing Errors with `@ControllerAdvice`

You can define a class annotated with `@ControllerAdvice` to:

* Customize the JSON document returned.
* Target specific controllers.
* Handle specific exception types.

### Example

```java
@ControllerAdvice(basePackageClasses = SomeController.class)
public class MyControllerAdvice extends ResponseEntityExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MyException.class)
    public ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        return new ResponseEntity<>(new MyErrorBody(status.value(), ex.getMessage()), status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.resolve(code);
        return (status != null) ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
```

### Behavior

If `MyException` is thrown by a controller in the same package as `SomeController`:

* A JSON representation of `MyErrorBody` is returned.
* Instead of the default `ErrorAttributes` representation.

---

## 7. Observability and Metrics

In some cases:

* Errors handled at the controller level
* May not be recorded by web observations or metrics infrastructure.

To ensure proper recording:

* Set the handled exception on the observation context.

This ensures visibility in monitoring and metrics systems.

---

# Custom Error Pages

Spring Boot allows you to define custom HTML error pages.

## 8. Static HTML Error Pages

Place static files under:

```
src/main/resources/public/error/
```

### Example: Custom 404 Page

```
src/
 +- main/
     +- resources/
         +- public/
             +- error/
                 +- 404.html
```

The file name must match:

* The exact status code (e.g., `404.html`)
* Or a series mask (e.g., `5xx.html`)

---

## 9. Template-Based Error Pages

Templates (e.g., FreeMarker) should be placed under:

```
src/main/resources/templates/error/
```

### Example: Handling All 5xx Errors

```
src/
 +- main/
     +- resources/
         +- templates/
             +- error/
                 +- 5xx.ftlh
```

---

## 10. Advanced Error View Mapping with `ErrorViewResolver`

For more complex mappings, implement `ErrorViewResolver`.

### Example

```java
public class MyErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request,
                                         HttpStatus status,
                                         Map<String, Object> model) {

        if (status == HttpStatus.INSUFFICIENT_STORAGE) {
            return new ModelAndView("myview");
        }

        return null;
    }
}
```

This allows:

* Custom logic based on request or status.
* Returning a specific `ModelAndView`.

---

## 11. Using Standard Spring MVC Exception Handling

You can also use:

* `@ExceptionHandler` methods
* `@ControllerAdvice`

The `ErrorController` picks up any unhandled exceptions.

---

# Error Handling Outside Spring MVC

For applications that do **not** use Spring MVC:

Use the `ErrorPageRegistrar` interface.

This works directly with the embedded servlet container and does not require a `DispatcherServlet`.

---

## 12. Example: Registering an Error Page

```java
@Configuration(proxyBeanMethods = false)
public class MyErrorPagesConfiguration {

    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return this::registerErrorPages;
    }

    private void registerErrorPages(ErrorPageRegistry registry) {
        registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/400"));
    }
}
```

---

## 13. Handling Filters with ERROR Dispatcher Type

If an `ErrorPage` path is handled by a `Filter`:

* The filter must be registered explicitly with the `ERROR` dispatcher type.

### Example

```java
@Configuration(proxyBeanMethods = false)
public class MyFilterConfiguration {

    @Bean
    public FilterRegistrationBean<MyFilter> myFilter() {
        FilterRegistrationBean<MyFilter> registration =
            new FilterRegistrationBean<>(new MyFilter());

        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }
}
```

Note: The default `FilterRegistrationBean` does **not** include the `ERROR` dispatcher type.

---

# Error Handling in WAR Deployment

When deploying as a WAR file:

* Spring Boot uses its **error page filter**.
* It forwards requests with an error status to the appropriate error page.
* This is necessary because the servlet specification does not provide an API for registering error pages.

---

## 14. Response Commitment Limitation

The error page filter can only forward the request:

* If the response has **not already been committed**.

### Special Case: WebSphere

WebSphere Application Server 8.0+:

* Commits the response upon successful completion of a servlet’s `service` method.

To disable this behavior:

```
com.ibm.ws.webcontainer.invokeFlushAfterService=false
```

---

# Summary

Spring Boot provides layered and flexible error handling through:

* Default `/error` mapping with JSON and HTML support.
* Whitelabel error page.
* `spring.web.error.*` configuration properties.
* `ErrorController` and `ErrorAttributes` customization.
* `BasicErrorController` extension for new content types.
* RFC 9457 Problem Details support via `spring.mvc.problemdetails.enabled`.
* Controller-level customization using `@ControllerAdvice`.
* Custom static and template-based error pages.
* Advanced view resolution using `ErrorViewResolver`.
* Servlet container-level registration with `ErrorPageRegistrar`.
* Special support for WAR deployments via error page filters.

This architecture allows full control over error responses for REST APIs, web applications, and embedded or external servlet container deployments.

---