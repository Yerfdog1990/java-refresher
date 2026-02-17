# The Lifecycle of a Request (Spring REST API)

These notes explain how an HTTP request is processed in a Spring Boot REST application **before it reaches the controller and after it returns a response**, based on the lesson *“The Lifecycle of a Request”* .

---

# 1. Goal of the Lesson

The purpose of understanding the lifecycle of a request is:

* To see **what happens under the hood** before a request reaches a controller
* To understand how Spring:

    * Finds the correct controller method
    * Deserializes the request body
    * Executes the method
    * Serializes the response
    * Handles exceptions
* To be able to **debug issues effectively**

Even though minor differences may exist between Spring versions, the high-level flow remains the same .

---

# 2. High-Level Overview of the Request Lifecycle

Spring MVC follows the **Front Controller Pattern** .

At the center of this pattern is:

## The DispatcherServlet

Before a request hits your `@RestController`, it is handled by Spring’s:

> **DispatcherServlet**

This servlet:

1. Determines which controller method should handle the request
2. Deserializes the request body into Java objects
3. Invokes the controller method
4. Serializes the returned object into the HTTP response

Everything goes through this central component.

---

# 3. Step-by-Step Breakdown of the Lifecycle

We’ll follow the flow of a typical request:

* A POST request
* With a JSON body
* Returning a JSON response

---

# 4. Step 1 – DispatcherServlet Receives the Request

The lifecycle starts inside:

```
DispatcherServlet.doDispatch()
```

This method is the central coordination point.

Inside `doDispatch()`, Spring performs several key actions:

* Resolves the handler (controller method)
* Invokes it
* Handles the return value
* Manages exceptions

Think of `doDispatch()` as the orchestration engine of the entire request.

---

# 5. Step 2 – Resolving the Request Handler

### Method: `getHandler()`

The first major task is determining:

> Which controller method should handle this request?

Spring does this using **HandlerMappings**.

## How It Works

* Spring iterates over all registered `HandlerMapping` instances
* The one with the highest priority is:

    * `RequestMappingHandlerMapping`
* This component resolves methods annotated with `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.

### Matching Logic

The method `lookupHandlerMethod()`:

1. Filters endpoints by request path
2. Filters by HTTP method
3. Resolves ambiguity if multiple matches exist

Example:

```
POST /campaigns
```

Spring:

* Extracts the lookup path `/campaigns`
* Filters controller methods
* Finds the matching method (e.g., `create(CampaignDto)`)

At this point, Spring has identified the correct **HandlerMethod**.

---

# 6. Step 3 – HandlerAdapter Invokes the Method

Once the handler is resolved:

Spring uses a **HandlerAdapter** to invoke it.

Eventually, execution reaches:

```
ServletInvocableHandlerMethod.invokeAndHandle()
```

This method:

* Resolves arguments
* Invokes the controller
* Captures the return value

---

# 7. Step 4 – Deserialization (Request Body → Java Object)

Before invoking the controller, Spring must:

> Convert the HTTP request body into Java objects

### Method: `getMethodArgumentValues()`

Spring inspects the controller method signature.

Example:

```java
public CampaignDto create(CampaignDto dto)
```

Spring sees:

* One parameter
* Type: `CampaignDto`
* Annotated with `@RequestBody`

It now needs to resolve this argument.

---

## Argument Resolution

Spring uses:

```
RequestResponseBodyMethodProcessor
```

This processor:

* Determines if it can resolve the argument
* Delegates to an HTTP message converter

---

## Message Converters

The processor calls:

```
readWithMessageConverters()
```

Spring checks:

* The `Content-Type` header (e.g., `application/json`)
* The target class (`CampaignDto`)

The appropriate converter is selected:

```
MappingJackson2HttpMessageConverter
```

This converter:

* Reads JSON
* Uses Jackson
* Converts JSON → Java object

Now the request body is successfully deserialized.

---

# 8. Step 5 – Invoking the Controller Method

After argument resolution:

Spring executes:

```
doInvoke(args)
```

This triggers:

* The actual controller method
* Your business logic

Example:

```java
CampaignDto saved = campaignService.save(dto);
return saved;
```

The controller:

* Executes normally
* Returns a new `CampaignDto`

The return value is now stored and ready for processing.

---

# 9. Step 6 – Handling the Return Value

Spring now processes the return value via:

```
handleReturnValue()
```

The same `RequestResponseBodyMethodProcessor` is used again .

This time, instead of reading the body, it writes it.

---

# 10. Step 7 – Serialization (Java Object → HTTP Response)

Spring performs the reverse of deserialization.

### Process:

1. Determine expected media type

    * Based on `Accept` header
    * Example: `application/json`

2. Select a suitable message converter

    * Again: `MappingJackson2HttpMessageConverter`

3. Serialize the Java object

    * Java → JSON
    * Write to HTTP response body

The final JSON response is sent to the client.

---

# 11. Step 8 – Exception Handling

Exception handling is built into the DispatcherServlet process.

If an exception occurs:

* It is intercepted inside the dispatching mechanism
* Spring tries to resolve a proper response
* The exception is not simply propagated up the call stack

---

## Return Value Handlers for Errors

Special return value handlers manage error responses:

* `RequestResponseBodyMethodProcessor`
* `HttpEntityMethodProcessor`

These handle:

* `ProblemDetail` responses
* `ResponseEntity`
* Responses from `ResponseEntityExceptionHandler`
* Global handlers via `@ControllerAdvice`

Spring converts exceptions into structured HTTP responses.

---

# 12. Complete Lifecycle Summary (End-to-End)

Here is the complete flow:

1. Client sends HTTP request
2. DispatcherServlet receives it
3. `doDispatch()` begins processing
4. `getHandler()` finds the controller method
5. HandlerAdapter prepares to invoke it
6. Arguments are resolved
7. Request body is deserialized using message converters
8. Controller method is executed
9. Return value is captured
10. Return value handler processes response
11. Response is serialized using message converters
12. HTTP response is sent back
13. Exceptions (if any) are handled within the dispatching process

---

# ✅ 1. Diagram-Style Flow Summary

## The Lifecycle of a Request in Spring REST

Below is the full request flow represented as a structured diagram.

---

```
CLIENT (Browser / Postman / Mobile App)
        |
        |  HTTP Request
        |  (Method + URL + Headers + Body)
        v
-------------------------------------------------
|             DispatcherServlet               |
|     (Front Controller Pattern)              |
-------------------------------------------------
        |
        | 1️⃣  doDispatch()
        v
-------------------------------------------------
|  HandlerMapping                             |
|  - RequestMappingHandlerMapping             |
|  - Finds matching controller method         |
-------------------------------------------------
        |
        | 2️⃣  getHandler()
        v
-------------------------------------------------
|  HandlerAdapter                             |
|  - Invokes resolved controller method       |
-------------------------------------------------
        |
        | 3️⃣  Resolve Method Arguments
        v
-------------------------------------------------
|  Argument Resolvers                         |
|  - @PathVariable                            |
|  - @RequestParam                            |
|  - @RequestBody                             |
-------------------------------------------------
        |
        | 4️⃣  Deserialize Request Body
        v
-------------------------------------------------
|  HttpMessageConverters                      |
|  - MappingJackson2HttpMessageConverter      |
|    (JSON → Java Object)                     |
-------------------------------------------------
        |
        | 5️⃣  Invoke Controller Method
        v
-------------------------------------------------
|  @RestController                            |
|  - Business Logic                           |
|  - Calls Service Layer                      |
-------------------------------------------------
        |
        | 6️⃣  Return Value Handling
        v
-------------------------------------------------
|  ReturnValueHandlers                        |
|  - RequestResponseBodyMethodProcessor       |
-------------------------------------------------
        |
        | 7️⃣  Serialize Response Body
        v
-------------------------------------------------
|  HttpMessageConverters                      |
|  - MappingJackson2HttpMessageConverter      |
|    (Java Object → JSON)                     |
-------------------------------------------------
        |
        | 8️⃣  HTTP Response (Status + Body)
        v
CLIENT
```

---

## 🧠 Where Exceptions Are Handled

If an exception occurs at ANY stage:

```
Exception
    ↓
HandlerExceptionResolver
    ↓
@ControllerAdvice / @ExceptionHandler
    ↓
Proper HTTP Response (e.g., 400, 404, 500)
```

Spring never just crashes — it converts exceptions into meaningful HTTP responses.

---

# 🎯 Mental Model Summary

Think of Spring like this:

> DispatcherServlet = Traffic Controller

> HandlerMapping = GPS (find correct method)

> ArgumentResolvers = Translators

> HttpMessageConverters = JSON Translators

> Controller = Your Business Logic

> ReturnValueHandlers = Response Processors

---

---

# 🛠 2. Step-by-Step Debug Checklist

Use this whenever:

* Controller is not being hit
* JSON is not mapping
* Wrong status code returned
* 404 / 415 / 406 errors appear
* Serialization problems occur

---

# 🔎 DEBUG CHECKLIST

---

## ✅ STEP 1 — Is the Request Reaching Spring?

Check:

* Is the application running?
* Is the correct port used?
* Is the URL correct?

Test with:

```
curl -v http://localhost:8080/campaigns
```

If not reaching:

* Check server logs
* Check firewall / port conflicts

---

## ✅ STEP 2 — Is the URL Mapping Correct?

Common Problem: 404 Not Found

Check:

```java
@RestController
@RequestMapping("/campaigns")
```

and

```java
@GetMapping("/{id}")
```

Full path should be:

```
/campaigns/1
```

Check for:

* Missing leading slash
* Wrong HTTP method
* Typo in path

---

## ✅ STEP 3 — Is the HTTP Method Correct?

Sending:

```
POST /campaigns/1
```

but controller expects:

```java
@PutMapping("/{id}")
```

You’ll get:

```
405 Method Not Allowed
```

Always verify:

* GET vs POST vs PUT vs DELETE vs PATCH

---

## ✅ STEP 4 — Is JSON Being Deserialized Properly?

Common Problem: 400 Bad Request

Causes:

* Invalid JSON
* Missing required fields
* Type mismatch
* No default constructor
* No getters/setters

Example Issue:

```json
{
  "id": "abc"   // but id is Long
}
```

Check:

* DTO structure
* Field names match JSON
* Jackson dependency present

---

## ✅ STEP 5 — Content-Type Header Correct?

If you see:

```
415 Unsupported Media Type
```

Ensure request includes:

```
Content-Type: application/json
```

Without this header, Spring won't pick the JSON converter.

---

## ✅ STEP 6 — Accept Header Correct?

If you see:

```
406 Not Acceptable
```

Client might be requesting:

```
Accept: application/xml
```

But your API only produces JSON.

Fix:

* Use `Accept: application/json`
* Or configure message converters

---

## ✅ STEP 7 — Is the Controller Actually Executing?

Add temporary logging:

```java
System.out.println("Controller hit");
```

Or use:

```java
@Slf4j
log.info("Inside create method");
```

If not printed:

* Mapping failed
* Security blocking request
* CORS issue

---

## ✅ STEP 8 — Is an Exception Being Thrown?

If you see:

```
500 Internal Server Error
```

Check:

* Stack trace in logs
* NullPointerException?
* Database constraint error?

Add:

```java
@ControllerAdvice
```

to handle cleanly.

---

## ✅ STEP 9 — Is Response Being Serialized Properly?

Common issues:

* Infinite recursion (bidirectional JPA relationships)
* LazyInitializationException
* No getters

Fix recursion using:

```java
@JsonManagedReference
@JsonBackReference
```

or

```java
@JsonIgnore
```

---

## ✅ STEP 10 — Check Security (If Using Spring Security)

If you get:

```
401 Unauthorized
403 Forbidden
```

Check:

* JWT present?
* CSRF disabled for APIs?
* Correct roles?

---

# 🚨 Common Errors & Where They Occur in Lifecycle

| Error | Lifecycle Stage      | Cause                |
| ----- | -------------------- | -------------------- |
| 404   | HandlerMapping       | No matching endpoint |
| 405   | Method Matching      | Wrong HTTP verb      |
| 400   | Argument Resolution  | JSON mapping failure |
| 415   | Message Converter    | Wrong Content-Type   |
| 406   | Return Conversion    | Wrong Accept header  |
| 500   | Controller / Service | Runtime exception    |

---

# 🎓 Advanced Debug Tip

Enable debug logging:

```properties
logging.level.org.springframework.web=DEBUG
```

You’ll see:

* Which handler was matched
* Which converter was used
* Why matching failed

---

# 🏁 Final Summary

When debugging, always think in this order:

1. Did DispatcherServlet receive the request?
2. Did HandlerMapping find a method?
3. Did ArgumentResolvers succeed?
4. Did MessageConverter deserialize correctly?
5. Did controller execute?
6. Did return value serialize correctly?
7. Did exception handling transform errors properly?

---

# 13. Key Concepts to Remember for Exams

### 1. Front Controller Pattern

All requests go through **DispatcherServlet**.

### 2. HandlerMapping

Resolves which controller method should handle the request.

### 3. HandlerAdapter

Invokes the resolved method.

### 4. Argument Resolvers

Convert HTTP data into method parameters.

### 5. HttpMessageConverters

* Deserialize request body
* Serialize response body

### 6. RequestResponseBodyMethodProcessor

Handles both:

* `@RequestBody`
* `@ResponseBody`

### 7. Exception Handling

Integrated into the dispatch lifecycle.

---

# 14. Why This Matters

Understanding the lifecycle helps you:

* Debug issues (e.g., “Why isn’t my controller called?”)
* Diagnose deserialization errors
* Fix `415 Unsupported Media Type`
* Fix `406 Not Acceptable`
* Understand custom converters
* Customize error handling
* Implement custom argument resolvers

Without this knowledge, Spring can feel like “magic.”

With it, you understand the machinery behind REST in Spring.

---

If you'd like, I can now:

* Turn this into a **diagram-style flow summary**
* Create a **step-by-step debug checklist**
* Provide **exam-style questions**
* Or simplify it into a **one-page revision sheet**
