# Error Responses in Spring

A common requirement for REST services is to include details in the body of error responses. The Spring Framework supports the **"Problem Details for HTTP APIs" specification (RFC 9457)** to provide a standardized structure for error responses.

See equivalent in the Reactive stack.

---

## 1. Core Abstractions for Error Responses

Spring provides several main abstractions to support RFC 9457-based error handling:

### 1.1 `ProblemDetail`

`ProblemDetail` is a representation for an RFC 9457 problem detail. It is:

* A simple container for standard fields defined in the specification.
* Also capable of holding non-standard fields.

It represents the body of an error response following the RFC 9457 format.

---

### 1.2 `ErrorResponse`

`ErrorResponse` is a contract to expose HTTP error response details including:

* HTTP status
* Response headers
* A body in the format of RFC 9457

It allows exceptions to encapsulate and expose the details of how they map to an HTTP response.

All Spring MVC exceptions implement `ErrorResponse`.

---

### 1.3 `ErrorResponseException`

`ErrorResponseException` is a basic `ErrorResponse` implementation that others can use as a convenient base class.

It simplifies the creation of custom exceptions that need to expose structured HTTP error responses.

---

### 1.4 `ResponseEntityExceptionHandler`

`ResponseEntityExceptionHandler` is a convenient base class for an `@ControllerAdvice` that:

* Handles all Spring MVC exceptions.
* Handles any `ErrorResponseException`.
* Renders an error response with a body.

This class is central for global exception handling in Spring MVC applications.

---

## 2. Rendering RFC 9457 Error Responses

See equivalent in the Reactive stack.

You can return `ProblemDetail` or `ErrorResponse` from:

* Any `@ExceptionHandler`
* Any `@RequestMapping` method

to render an RFC 9457 response.

### 2.1 Rendering Behavior

The processing works as follows:

* The `status` property of `ProblemDetail` determines the HTTP status.
* The `instance` property of `ProblemDetail` is set from the current URL path, if not already set.
* The Jackson JSON and XML codecs use:

    * `application/problem+json`
    * `application/problem+xml`

  respectively as the producible media types for `ProblemDetail`, ensuring they are favored for content negotiation.

---

### 2.2 Enabling RFC 9457 for Spring MVC Exceptions

To enable RFC 9457 responses for:

* Spring MVC exceptions
* Any `ErrorResponseException`

You must:

1. Extend `ResponseEntityExceptionHandler`.
2. Declare it as an `@ControllerAdvice` in Spring configuration.

The handler includes an `@ExceptionHandler` method that:

* Handles any `ErrorResponse` exception.
* Includes all built-in web exceptions.

You can:

* Add additional exception handling methods.
* Use a protected method to map any exception to a `ProblemDetail`.

---

### 2.3 ErrorResponse Interceptors

You can register `ErrorResponse` interceptors through the MVC configuration using a `WebMvcConfigurer`.

This allows you to:

* Intercept any RFC 9457 response.
* Perform additional actions (e.g., logging, enrichment, auditing).

---

## 3. Non-Standard Fields in ProblemDetail

See equivalent in the Reactive stack.

RFC 9457 allows extending responses with non-standard fields. Spring supports this in two ways.

---

### 3.1 Using the `properties` Map

You can insert custom fields into the `"properties"` Map of `ProblemDetail`.

When using Jackson:

* Spring registers `ProblemDetailJacksonMixin`.
* The `"properties"` Map is unwrapped and rendered as top-level JSON properties in the response.
* During deserialization, any unknown property is inserted into this Map.

This allows flexible extension without subclassing.

---

### 3.2 Subclassing `ProblemDetail`

You can extend `ProblemDetail` to define dedicated non-standard properties.

Key support:

* The copy constructor in `ProblemDetail` allows a subclass to be created from an existing `ProblemDetail`.
* This enables centralized customization.

For example:

* An `@ControllerAdvice` such as `ResponseEntityExceptionHandler` can recreate the `ProblemDetail` of an exception into a subclass with additional non-standard fields.

---

### 3.3 Spring Boot Support

In Spring Boot:

* The `spring.mvc.problemdetails.enabled` property autoconfigures a `ResponseEntityExceptionHandler`.
* It handles built-in exceptions with problem details.

If you want to override handling for a specific built-in exception:

* Create another `@ControllerAdvice`.
* Ensure it is ordered ahead of the one configured by Spring Boot.
* The default order of the Spring Boot handler is `0`.

---

## 4. Customization and Internationalization (i18n)

See equivalent in the Reactive stack.

Customizing and internationalizing error response details is a common requirement. It is also good practice to customize problem details for Spring MVC exceptions to avoid revealing implementation details.

Spring provides built-in support for this.

---

### 4.1 Message Codes in ErrorResponse

An `ErrorResponse` exposes message codes for:

* `"type"`
* `"title"`
* `"detail"`

It also exposes message code arguments for the `"detail"` field.

`ResponseEntityExceptionHandler`:

* Resolves these codes using a `MessageSource`.
* Updates the corresponding `ProblemDetail` fields accordingly.

---

### 4.2 Default Message Code Strategy

The default strategy for message codes is:

* `"type"`:
  `problemDetail.type.[fully qualified exception class name]`

* `"title"`:
  `problemDetail.title.[fully qualified exception class name]`

* `"detail"`:
  `problemDetail.[fully qualified exception class name][suffix]`

An `ErrorResponse` may expose more than one message code, typically by adding a suffix to the default message code.

---

### 4.3 Message Codes and Arguments for Spring MVC Exceptions

Below is a structured summary of message codes and arguments:

| Exception                                   | Message Code              | Message Code Arguments                                                                                                                       |
| ------------------------------------------- | ------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| AsyncRequestTimeoutException                | (default)                 | —                                                                                                                                            |
| ConversionNotSupportedException             | (default)                 | {0} property name, {1} property value                                                                                                        |
| HandlerMethodValidationException            | (default)                 | {0} list all validation errors. Message codes and arguments for each error are also resolved via MessageSource.                              |
| HttpMediaTypeNotAcceptableException         | (default)                 | {0} list of supported media types                                                                                                            |
| HttpMediaTypeNotAcceptableException         | (default) + ".parseError" | —                                                                                                                                            |
| HttpMediaTypeNotSupportedException          | (default)                 | {0} the media type that is not supported, {1} list of supported media types                                                                  |
| HttpMediaTypeNotSupportedException          | (default) + ".parseError" | —                                                                                                                                            |
| HttpMessageNotReadableException             | (default)                 | —                                                                                                                                            |
| HttpMessageNotWritableException             | (default)                 | —                                                                                                                                            |
| HttpRequestMethodNotSupportedException      | (default)                 | {0} the current HTTP method, {1} the list of supported HTTP methods                                                                          |
| MethodArgumentNotValidException             | (default)                 | {0} the list of global errors, {1} the list of field errors. Message codes and arguments for each error are also resolved via MessageSource. |
| MissingRequestHeaderException               | (default)                 | {0} the header name                                                                                                                          |
| MissingServletRequestParameterException     | (default)                 | {0} the request parameter name                                                                                                               |
| MissingMatrixVariableException              | (default)                 | {0} the matrix variable name                                                                                                                 |
| MissingPathVariableException                | (default)                 | {0} the path variable name                                                                                                                   |
| MissingRequestCookieException               | (default)                 | {0} the cookie name                                                                                                                          |
| MissingServletRequestPartException          | (default)                 | {0} the part name                                                                                                                            |
| NoHandlerFoundException                     | (default)                 | —                                                                                                                                            |
| NoResourceFoundException                    | (default)                 | {0} the request path (or portion of) used to find a resource                                                                                 |
| TypeMismatchException                       | (default)                 | {0} property name, {1} property value, {2} simple name of required type                                                                      |
| UnsatisfiedServletRequestParameterException | (default)                 | {0} the list of parameter conditions                                                                                                         |

---

### 4.4 Special Case: Validation Exceptions

Unlike other exceptions:

* `MethodArgumentNotValidException`
* `HandlerMethodValidationException`

Their message arguments are based on a list of `MessageSourceResolvable` errors.

These errors can also be customized through a `MessageSource` resource bundle.

See Customizing Validation Errors for more details.

---

## 5. Client Handling of Error Responses

See equivalent in the Reactive stack.

Client applications can decode RFC 9457 error responses as follows:

* When using `WebClient`, catch `WebClientResponseException`.
* When using `RestTemplate`, catch `RestClientResponseException`.

Both provide `getResponseBodyAs` methods to:

* Decode the error response body.
* Convert it to a target type such as:

    * `ProblemDetail`
    * A subclass of `ProblemDetail`

This enables structured client-side handling of standardized error responses.

---

# Summary

Spring provides comprehensive support for RFC 9457-based error responses through:

* `ProblemDetail` for standardized response bodies.
* `ErrorResponse` and `ErrorResponseException` for exception-to-response mapping.
* `ResponseEntityExceptionHandler` for centralized handling.
* Jackson integration for content negotiation.
* Extension mechanisms for non-standard fields.
* MessageSource-based customization and internationalization.
* Built-in Spring Boot auto-configuration support.
* Structured client-side decoding with WebClient and RestTemplate.

Together, these features enable consistent, extensible, and internationalized error handling in Spring-based REST APIs.

---