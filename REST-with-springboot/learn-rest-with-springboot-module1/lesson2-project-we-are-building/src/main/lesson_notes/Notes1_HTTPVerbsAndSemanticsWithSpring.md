
---

# HTTP Verbs and Semantics with Spring

## Understanding HTTP and REST

### 1. HTTP vs REST: How They Relate

REST (Representational State Transfer) is an **architectural style**, not a protocol.
HTTP (HyperText Transfer Protocol) is a **communication protocol**.

👉 **Key idea:**
REST does **not** depend on HTTP, but HTTP fits REST *perfectly* because it already provides:

* Standard methods (verbs)
* Clear semantics
* Status codes
* Headers
* Resource-based URLs

REST requires a **Uniform Interface**, and HTTP gives us one “out of the box”.

---

## 2. How HTTP Communicates Semantics

An HTTP request/response expresses its **intent** using three main elements:

1. **HTTP Request Method (Verb)**
2. **HTTP Headers**
3. **HTTP Response Status Codes**

Together, these make REST APIs predictable and easy to consume.

---

## 3. HTTP Request Methods (Verbs)

The HTTP method is the **primary source of meaning** in a request.

### Common RESTful HTTP Verbs

| Verb       | Purpose          | REST Semantics             |
| ---------- | ---------------- | -------------------------- |
| **GET**    | Retrieve data    | Read-only, no side effects |
| **POST**   | Submit data      | Create or process          |
| **PUT**    | Replace resource | Full update                |
| **PATCH**  | Modify resource  | Partial update             |
| **DELETE** | Remove resource  | Delete                     |

### Example Resource

```
/campaigns
```

| Action             | HTTP Method | Endpoint        |
| ------------------ | ----------- | --------------- |
| List all campaigns | GET         | /campaigns      |
| Get one campaign   | GET         | /campaigns/{id} |
| Create campaign    | POST        | /campaigns      |
| Update campaign    | PUT         | /campaigns/{id} |
| Delete campaign    | DELETE      | /campaigns/{id} |

---

## 4. Important HTTP Semantics

### 4.1 Safe Methods

* **GET** is *safe* → it must NOT change server state

### 4.2 Idempotent Methods

Calling the same request multiple times produces the same result.

| Method | Idempotent? |
| ------ | ----------- |
| GET    | ✅           |
| PUT    | ✅           |
| DELETE | ✅           |
| POST   | ❌           |
| PATCH  | ❌ (usually) |

---

## 5. HTTP Headers

Headers provide **metadata** about the request or response.

### Request Headers (Client → Server)

* `Content-Type`
* `Accept`
* `Authorization`
* `User-Agent`

### Response Headers (Server → Client)

* `Location`
* `Content-Type`
* `Cache-Control`

Headers help control:

* Data format
* Authentication
* Caching
* Redirection

---

## 6. HTTP Response Status Codes

Every response includes a **3-digit status code**.

### Status Code Classes

| Range | Meaning       |
| ----- | ------------- |
| 1xx   | Informational |
| 2xx   | Success       |
| 3xx   | Redirection   |
| 4xx   | Client Error  |
| 5xx   | Server Error  |

### Common REST Status Codes

| Code                      | Meaning            |
| ------------------------- | ------------------ |
| 200 OK                    | Request successful |
| 201 Created               | Resource created   |
| 204 No Content            | Success, no body   |
| 400 Bad Request           | Invalid request    |
| 404 Not Found             | Resource missing   |
| 500 Internal Server Error | Server issue       |

---

## 7. Implementing HTTP Semantics in Spring

Spring Web makes REST implementation **annotation-driven and expressive**.

---

## 8. The `@RestController` Annotation

```java
@RestController
@RequestMapping("/campaigns")
public class CampaignController {
}
```

### What does `@RestController` do?

It is a **composed annotation**:

* `@Controller`
* `@ResponseBody`

### Why is this important?

* Every method returns data (JSON/XML)
* No view resolution (no JSP/Thymeleaf)
* Perfect for REST APIs

📌 This matches REST’s **resource-focused** design.

---

## 9. The `@RequestMapping` Annotation

Maps HTTP requests to handlers.

```java
@RequestMapping("/campaigns")
```

* Can be applied at **class level**
* Can be applied at **method level**
* Can define:

    * Path
    * HTTP method
    * Headers
    * Media types

---

## 10. Shortcut Mapping Annotations

Spring provides **HTTP-verb-specific annotations**:

| Annotation       | HTTP Method |
| ---------------- | ----------- |
| `@GetMapping`    | GET         |
| `@PostMapping`   | POST        |
| `@PutMapping`    | PUT         |
| `@DeleteMapping` | DELETE      |
| `@PatchMapping`  | PATCH       |

### Example

```java
@GetMapping("/{id}")
public CampaignDto findOne(@PathVariable Long id) {
    return service.findById(id);
}
```

These are shortcuts for:

```java
@RequestMapping(method = RequestMethod.GET, value = "/{id}")
```

---

## 11. The `@PathVariable` Annotation

Used for **URI template variables**.

```java
@GetMapping("/{id}")
public CampaignDto findOne(@PathVariable Long id) {
}
```

Request:

```
GET /campaigns/1
```

Result:

* `id = 1`

---

## 12. The `@RequestBody` Annotation

Maps the **request body** to a Java object.

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public CampaignDto create(@RequestBody CampaignDto dto) {
}
```

Spring automatically:

* Reads JSON
* Deserializes it
* Binds it to a Java object

---

## 13. The `@ResponseStatus` Annotation

Overrides the default status code.

```java
@ResponseStatus(HttpStatus.CREATED)
```

Why?

* REST semantics matter
* Creating a resource should return **201 Created**, not 200

---

## 14. REST Endpoint Example (Full)

```java
@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    @GetMapping
    public List<CampaignDto> listCampaigns() { }

    @GetMapping("/{id}")
    public CampaignDto findOne(@PathVariable Long id) { }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto create(@RequestBody CampaignDto dto) { }

    @PutMapping("/{id}")
    public CampaignDto update(
        @PathVariable Long id,
        @RequestBody CampaignDto dto) { }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { }
}
```

---

## 15. Key Takeaways

* REST defines **principles**, HTTP provides **tools**
* HTTP verbs express **intent**
* Status codes express **outcome**
* Spring annotations map HTTP semantics cleanly
* A well-designed REST API is:

    * Predictable
    * Consistent
    * Self-descriptive

---

## 16. Further Reading (Resources)

* Difference Between REST and HTTP
* Spring `@RequestMapping` Shortcut Annotations
* **RFC 9110** – HTTP Semantics
* **RFC 5789** – PATCH Method

---


