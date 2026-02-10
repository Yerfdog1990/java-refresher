
---

# The Anatomy of a REST API

## 1. Goal

In this theory lesson, we learn the **foundational anatomy of REST**, its **core constraints**, and what it truly means to design a **RESTful API**.
By the end of this lesson, learners should be able to:

* Explain what REST is and what it is not
* Understand why REST is commonly implemented over HTTP
* Identify REST constraints and their trade-offs
* Correctly use REST terminology such as *resource*, *representation*, and *media type*
* Understand how HTTP methods, URIs, and status codes fit into REST
* Recognize different maturity levels of REST APIs

---

## 2. Lesson Notes

There is **no project code required** to follow along in this lesson. The focus is on **conceptual understanding**.

---

## 2.1 What is REST?

**REST** stands for **Representational State Transfer**.

It is an **architectural style**, not:

* a framework
* a protocol
* a specification
* a standard like SOAP

REST defines **a set of constraints** that guide how distributed systems should communicate.

REST was first introduced by **Roy Fielding** in his doctoral dissertation, where he described the architectural principles behind the World Wide Web.

### Core idea

> Any system that follows REST constraints gains **scalability, simplicity, visibility, and evolvability**.

---

## 2.2 REST and HTTP

In theory:

* REST is **protocol-agnostic**
* It can be implemented over any protocol

In practice:

* REST is almost always implemented over **HTTP**

Why?

* HTTP already provides:

    * resource identifiers (URIs)
    * request methods (GET, POST, PUT, DELETE)
    * headers and content negotiation
    * caching semantics
    * status codes

So instead of using HTTP as **just a transport layer**, REST uses HTTP as an **application protocol**, fully embracing its semantics.

---

## 2.3 REST Constraints

A system is only considered RESTful if it adheres to **these constraints**.

---

### 1. Client–Server

This constraint enforces **separation of concerns**.

* Client: user interface, user experience
* Server: business logic, data storage

They evolve **independently**.

#### Benefits

* Scalability
* Simpler systems
* Multiple clients (web, mobile, IoT) using the same API

---

### 2. Stateless

Each request must be **self-contained**.

* The server stores **no client session state**
* Every request includes all required context
* Authentication, authorization, and data must be sent every time

#### Diagram: Stateless Interaction

```
Client ── Request #1 (all data) ──▶ Server
Client ── Request #2 (all data) ──▶ Server
Client ── Request #3 (all data) ──▶ Server
```

The server does **not remember** previous requests.

#### Benefits

* Visibility
* Reliability
* Horizontal scalability

#### Trade-offs

* Larger request payloads
* Less server-side control over workflows

---

### 3. Cacheable

Responses must explicitly declare whether they are cacheable.

* Cacheable responses may be reused
* Non-cacheable responses must always hit the server

This allows:

* Browsers
* Proxies
* CDNs
  to improve performance.

#### Diagram: Cache Layer

```
Client ─▶ Cache ─▶ Server
         ▲
         └── Reused Response
```

#### Trade-off

* Risk of serving stale data

---

### 4. Uniform Interface (Uniform Contract)

All clients interact with the server through **one consistent interface**.

This includes:

* Resource identification via URIs
* Standard HTTP methods
* Self-descriptive messages
* Consistent response formats

#### Benefits

* Loose coupling
* Client and server evolve independently

#### Trade-off

* Less optimized, more generic interactions

---

### 5. Layered System

A REST system can have **multiple layers**:

* API Gateway
* Authentication service
* Business service
* Cache
* Database

Clients should not know whether they are connected to:

* the final server
* a proxy
* a load balancer

#### Diagram: Layered Architecture

```
Client
  │
  ▼
API Gateway
  │
  ▼
Service Layer
  │
  ▼
Database
```

#### Trade-off

* Slight latency
* More complexity

---

### 6. Code on Demand (Optional)

The server may send executable code to the client (e.g. JavaScript).

This constraint is:

* optional
* rarely used in REST APIs

---

## 2.4 REST Architectural Elements and Terminology

Understanding REST requires **precise language**.

---

### Resource

A **resource** is a conceptual mapping to information.

* Not the database entity
* Not the representation

Examples:

* `/users`
* `/users/42`
* `/reports/latest`

A resource is identified by a **URI**.

> Any information that can be named can be a resource.

---

### Representation

A **representation** is:

* a snapshot of a resource
* in a specific format
* sent as bytes + metadata

Common formats:

* JSON
* XML
* HTML

The same resource can have **multiple representations**.

---

### Media Types and Hypermedia

The **media type** (Content-Type) describes how to interpret a representation.

Examples:

* `application/json`
* `application/xml`
* `text/html`

#### Hypermedia (HATEOAS)

A fully RESTful API is driven by **hypermedia**:

* responses include links
* clients navigate dynamically

Example:

```json
{
  "id": 123,
  "name": "Alice",
  "links": {
    "self": "/users/123",
    "orders": "/users/123/orders"
  }
}
```

Most real-world APIs **do not fully implement** this constraint.

---

## 3. HTTP Protocol in REST

REST commonly uses HTTP’s request–response model.

---

### HTTP Request Structure

```
METHOD /resource HTTP/1.1
Headers
(empty line)
Optional body
```

---

### URI Structure

```
scheme://host:port/path?query#fragment
```

Example:

```
https://api.example.com/users/42
```

---

## 3.1 HTTP Methods

| Method | Purpose           | Notes          |
| ------ | ----------------- | -------------- |
| GET    | Retrieve resource | Safe, no body  |
| POST   | Create resource   | Not idempotent |
| PUT    | Replace resource  | Idempotent     |
| DELETE | Remove resource   | Idempotent     |

Idempotent means:

> Multiple identical requests produce the same result.

---

## 3.2 HTTP Status Codes

| Category | Meaning       |
| -------- | ------------- |
| 1xx      | Informational |
| 2xx      | Success       |
| 3xx      | Redirection   |
| 4xx      | Client errors |
| 5xx      | Server errors |

Examples:

* 200 OK
* 201 Created
* 400 Bad Request
* 401 Unauthorized
* 404 Not Found
* 500 Internal Server Error

---

## 4. Richardson Maturity Model

This model evaluates **how RESTful an API is**.

---

### Level 0 – Swamp of POX

* Uses HTTP as transport only
* Mostly POST
* No proper resources

```
POST /createUser
POST /updateUser
```

---

### Level 1 – Resources

* Resources introduced
* Still poor HTTP usage

```
POST /users/create
POST /users/42/update
```

---

### Level 2 – HTTP Verbs and Status Codes

* Proper HTTP methods
* Meaningful status codes

```
POST /users
GET /users/42
PUT /users/42
DELETE /users/42
```

Most production APIs stop here.

---

### Level 3 – Hypermedia (HATEOAS)

* Responses contain navigable links
* Fully RESTful

```
GET /users/42
```

Returns links to related resources.

---

## 5. REST API Flow Diagram

```
Client
  │  HTTP Request (GET /users/42)
  ▼
API Layer
  │
  ▼
Service Layer
  │
  ▼
Database
  │
  ▲
Response (JSON + Status Code)
```

---

## 6. Conclusion

REST is:

* simple in concept
* powerful in design
* flexible in implementation

By following REST constraints, APIs gain:

* scalability
* reliability
* evolvability
* clarity

Understanding the **anatomy of REST** is essential before writing any REST API code—otherwise, developers risk building APIs that merely *look* RESTful but violate its core principles.

---

