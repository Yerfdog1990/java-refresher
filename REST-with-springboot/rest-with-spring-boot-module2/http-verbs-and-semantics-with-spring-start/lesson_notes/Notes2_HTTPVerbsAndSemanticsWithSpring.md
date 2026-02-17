# HTTP Verbs and Semantics with Spring

## 1. HTTP and REST: Understanding the Relationship

Before diving into Spring implementation details, it's important to clarify the relationship between:

* **REST** – an architectural style
* **HTTP** – a communication protocol

REST requires a **Uniform Interface** constraint. HTTP naturally supports this constraint through:

* HTTP methods (verbs)
* Status codes
* Headers
* URLs

REST itself is protocol-agnostic, but HTTP provides a standardized way to express RESTful semantics.

---

# 2. Core HTTP Semantics

An HTTP message communicates its meaning using three primary elements:

## 2.1 HTTP Request Method (Verb)

The request method defines the action to be performed on a resource.

Common methods used in REST APIs:

| Verb   | Purpose             | Semantics                  |
| ------ | ------------------- | -------------------------- |
| GET    | Retrieve a resource | Safe, idempotent           |
| POST   | Create or process   | Not idempotent             |
| PUT    | Replace resource    | Idempotent                 |
| PATCH  | Partial update      | Not necessarily idempotent |
| DELETE | Remove resource     | Idempotent                 |

---

## 2.2 HTTP Headers

Headers provide additional metadata:

* Content negotiation (`Content-Type`, `Accept`)
* Authentication (`Authorization`)
* Caching (`Cache-Control`)
* Custom application metadata

Headers exist in both:

* Request messages
* Response messages

---

## 2.3 HTTP Response Status Codes

Status codes communicate the result of the request.

### Classes of Status Codes

| Code Range | Meaning       |
| ---------- | ------------- |
| 1xx        | Informational |
| 2xx        | Success       |
| 3xx        | Redirection   |
| 4xx        | Client error  |
| 5xx        | Server error  |

Common REST-friendly codes:

* **200 OK** – Successful GET or PUT
* **201 Created** – Successful POST
* **204 No Content** – Successful DELETE
* **404 Not Found** – Resource does not exist
* **400 Bad Request** – Invalid input

---

# 3. Spring Web Annotations for HTTP Semantics

Spring simplifies mapping HTTP semantics to Java methods using annotations.

## 3.1 @RestController

```java
@RestController
@RequestMapping("/campaigns")
public class CampaignController {
}
```

### What @RestController Does

It combines:

* `@Controller`
* `@ResponseBody`

This means:

* The class is detected as a web component
* Return values are written directly to the HTTP response body (typically JSON)

This is ideal for REST APIs.

---

## 3.2 @RequestMapping

Maps HTTP requests to handler classes or methods.

At class level:

```java
@RequestMapping("/campaigns")
```

At method level:

```java
@RequestMapping(method = RequestMethod.GET, value = "/{id}")
```

Spring provides shortcuts for common HTTP verbs.

---

# 4. HTTP Verbs with Spring Annotations

Spring provides specialized annotations:

* `@GetMapping`
* `@PostMapping`
* `@PutMapping`
* `@PatchMapping`
* `@DeleteMapping`

Each maps directly to an HTTP verb.

---

## 4.1 GET – Retrieve Resources

### Semantics

* Safe
* Idempotent
* Does not modify server state

### Example

```java
@GetMapping
public List<CampaignDto> listCampaigns() {
    return campaignService.findAll();
}
```

Retrieve one resource:

```java
@GetMapping("/{id}")
public CampaignDto findOne(@PathVariable Long id) {
    return campaignService.findById(id);
}
```

### @PathVariable

Binds URI template variables to method parameters.

Example:

Request:

```
GET /campaigns/1
```

Spring binds:

```java
id = 1L
```

---

## 4.2 POST – Create Resource

### Semantics

* Creates new resource
* Not idempotent
* Returns 201 Created

### Example

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public CampaignDto create(@RequestBody CampaignDto newCampaign) {
    return campaignService.create(newCampaign);
}
```

### @RequestBody

* Maps HTTP request body to Java object
* Uses automatic JSON deserialization

Example request body:

```json
{
  "name": "Summer Sale",
  "budget": 10000
}
```

---

## 4.3 PUT – Replace Resource

### Semantics

* Replaces entire resource
* Idempotent
* Returns 200 OK (or 204 No Content)

### Example

```java
@PutMapping("/{id}")
public CampaignDto update(@PathVariable Long id,
                          @RequestBody CampaignDto updatedCampaign) {
    return campaignService.update(id, updatedCampaign);
}
```

PUT replaces the full representation of the resource.

---

## 4.4 PATCH – Partial Update

### Semantics

* Partially modifies resource
* May not be idempotent

### Example

```java
@PatchMapping("/{id}")
public CampaignDto partialUpdate(@PathVariable Long id,
                                 @RequestBody Map<String, Object> updates) {
    return campaignService.patch(id, updates);
}
```

PATCH allows updating only specific fields.

Example request body:

```json
{
  "budget": 15000
}
```

---

## 4.5 DELETE – Remove Resource

### Semantics

* Idempotent
* Removes resource
* Returns 204 No Content

### Example

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
    campaignService.delete(id);
}
```

If the resource does not exist, return:

```java
throw new ResponseStatusException(HttpStatus.NOT_FOUND);
```

---

# 5. Semantic Best Practices in Spring

## 5.1 Use Nouns for URIs

Correct:

```
/users
/users/{id}
```

Incorrect:

```
/getUser/{id}
/deleteUser/{id}
```

HTTP verbs should define the action — not the URI.

---

## 5.2 Return Proper Status Codes

| Operation        | Status         |
| ---------------- | -------------- |
| GET              | 200 OK         |
| POST             | 201 Created    |
| PUT              | 200 OK         |
| DELETE           | 204 No Content |
| Missing resource | 404 Not Found  |

Example:

```java
@GetMapping("/{id}")
public ResponseEntity<CampaignDto> findOne(@PathVariable Long id) {
    return campaignService.findByIdOptional(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

---

## 5.3 Exception Handling with @ControllerAdvice

Centralized error handling improves HTTP semantics.

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(ResourceNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}
```

This ensures consistent error responses.

---

# 6. Complete Example Controller

```java
@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public List<CampaignDto> listCampaigns() {
        return campaignService.findAll();
    }

    @GetMapping("/{id}")
    public CampaignDto findOne(@PathVariable Long id) {
        return campaignService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto create(@RequestBody CampaignDto newCampaign) {
        return campaignService.create(newCampaign);
    }

    @PutMapping("/{id}")
    public CampaignDto update(@PathVariable Long id,
                              @RequestBody CampaignDto updatedCampaign) {
        return campaignService.update(id, updatedCampaign);
    }

    @PatchMapping("/{id}")
    public CampaignDto partialUpdate(@PathVariable Long id,
                                     @RequestBody Map<String, Object> updates) {
        return campaignService.patch(id, updates);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        campaignService.delete(id);
    }
}
```

---

# 7. Testing HTTP Semantics with WebTestClient

Spring provides `WebTestClient` for end-to-end testing:

```java
webTestClient.get()
    .uri("/campaigns/1")
    .exchange()
    .expectStatus().isOk()
    .expectBody()
    .jsonPath("$.id").isEqualTo(1);
```

This ensures correct HTTP behavior and status codes.

---

# 8. Final Takeaways

1. HTTP verbs define resource semantics.
2. Spring maps these verbs directly using specialized annotations.
3. Proper use of:

    * Status codes
    * URI design
    * Headers
    * Exception handling
4. Ensures your API is:

    * Predictable
    * REST-compliant
    * Easily consumable by clients

Spring doesn’t invent new HTTP semantics — it gives you a clean and structured way to implement them correctly.

---