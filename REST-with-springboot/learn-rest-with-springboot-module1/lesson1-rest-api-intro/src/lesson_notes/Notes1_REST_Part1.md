
---

# Part 1: What is REST?

---

## Introduction

Today we will learn about a topic that is **extremely important in modern software development** and **highly demanded in the job market**: **REST**.

REST is the foundation of:

* Web APIs
* Mobile backends
* Microservices
* Cloud-based systems

If you want to build **scalable, maintainable backend systems**, REST is a must-know concept.

---

## Overview of REST

This REST overview is divided into **three parts**:

1. **Part 1** – What REST is, its history, and its architectural principles
2. **Part 2** – How REST uses the **HTTP protocol** for communication
3. **Part 3** – Building and testing a **RESTful application** using **Postman**

---

## Prerequisites

Before continuing, you should be familiar with:

* HTTP
* URL vs URI
* JSON (basic XML knowledge is helpful)
* Dependency Injection (conceptual understanding)

---

# Part 1: What is REST?

---

## Meaning of REST

**REST** stands for:

> **REpresentational State Transfer**

REST is an **architectural style**, meaning:

* It is a **set of design principles**
* It defines **how systems communicate**
* It does **not enforce specific technologies**

REST focuses on **resources** and **representations** of those resources transferred between systems.

---

## REST in Simple Terms

REST answers the question:

> *“How should different applications exchange data over a network?”*

REST says:

* Everything is a **resource**
* Resources are identified by **URIs**
* Resources are manipulated using **standard operations**
* Communication is **stateless**

---

## RESTful Applications

An application is called **RESTful** if:

* It follows the REST constraints
* It respects REST design principles
* It uses resources consistently

---

## History of REST

REST was formally introduced in **2000** by **Roy Fielding** in his PhD dissertation:

> *Architectural Styles and the Design of Network-based Software Architectures*

Roy Fielding was also one of the **main designers of HTTP**, which is why REST fits so naturally on top of HTTP.

⚠️ Important:
REST was **not invented to replace HTTP**.
REST was **inspired by how the Web already worked**.

---

## Client–Server Communication Model

REST systems rely on a **request–response** model.

### Key Roles

* **Client** – sends requests
* **Server** – processes requests and sends responses

---

### Basic Client–Server Diagram

```
+--------+        HTTP Request        +--------+
| Client |  ---------------------->   | Server |
|        |                            |        |
|        |  <----------------------   |        |
+--------+        HTTP Response       +--------+
```

---

### Important Rule

* The **request sender** is always the **client**
* The **request processor** is always the **server**

This relationship is **context-based**:

* A server can act as a client when calling another service

---

# REST Constraints (Core Principles)

REST defines **six constraints**.
If **any of them are violated**, the system is **not RESTful**.

---

## 1. Client–Server Constraint

### Explanation

This constraint enforces **separation of concerns**.

* Client → UI & user interaction
* Server → Data & business logic

---

### Diagram: Separation of Concerns

```
+-------------------+        +----------------------+
|      Client       |        |        Server        |
|-------------------|        |----------------------|
| UI                |        | Business Logic       |
| User Interaction  |        | Data Processing      |
| Input Handling    |        | Database Access      |
+-------------------+        +----------------------+
```

---

### Benefits

* Independent development
* Better scalability
* Easier maintenance
* Multiple clients for one server

---

## 2. Stateless Constraint

### Explanation

Each request must be **self-contained**.

* The server **does not remember** previous requests
* No session state is stored on the server

---

### Stateless Communication Diagram

```
Request 1 (full info)  --->  Server
Request 2 (full info)  --->  Server
Request 3 (full info)  --->  Server
```

Each request is **independent**.

---

### Why Statelessness Matters

* Easy horizontal scaling
* Servers can be restarted anytime
* No session synchronization needed

---

### Trade-offs

* Repeated data in requests
* More responsibility on the client

---

## 3. Cacheable Constraint

### Explanation

Server responses must define whether they are:

* Cacheable
* Non-cacheable

Clients can reuse cached responses.

---

### Cache Flow Diagram

```
Client
  |
  | Request
  v
Cache ----> (Hit) ----> Response
  |
 (Miss)
  |
  v
Server ----> Response ----> Cache ----> Client
```

---

### Benefits

* Faster response times
* Reduced server load
* Improved scalability

---

### Trade-off

* Potentially stale data

---

## 4. Uniform Interface Constraint

### Explanation

REST requires a **consistent interface** between client and server.

The interface defines:

* How resources are identified
* How representations are formatted
* How actions are performed

---

### Uniform Interface Diagram

```
Client
  |
  | GET /users/1
  |
  v
Server
  |
  | JSON Representation
  |
  v
Client
```

---

### Key Characteristics

* Standard methods
* Predictable URIs
* Consistent data formats

---

### Trade-off

* Less optimized for specific clients
* More generic communication

---

## 5. Layered System Constraint

### Explanation

REST allows **intermediate layers** between client and server.

Clients don’t know:

* Whether they’re talking to the final server
* Or an intermediary

---

### Layered Architecture Diagram

```
Client
  |
  v
Load Balancer
  |
  v
Cache Server
  |
  v
Application Server
  |
  v
Database
```

---

### Benefits

* Load balancing
* Distributed caching
* Improved scalability

---

### Trade-off

* Slight latency increase

---

## 6. Code on Demand (Optional)

### Explanation

Servers can send **executable code** to clients.

Examples:

* JavaScript
* Applets

---

### Diagram

```
Server ---- JavaScript Code ----> Client
```

⚠️ This constraint is optional and rarely used in REST APIs today.

---

# Advantages of RESTful Architecture

RESTful systems provide:

* Reliability
* Performance
* Scalability
* Simplicity
* Transparency
* Portability
* Easy evolution

---

# REST vs HTTP

| REST                | HTTP                           |
| ------------------- | ------------------------------ |
| Architectural style | Communication protocol         |
| Conceptual          | Standardized                   |
| Protocol-agnostic   | Defines methods & status codes |

👉 REST is often **implemented using HTTP**, but REST itself is not HTTP.

---

# REST Architectural Elements

---

## Resource

A **Resource** is anything that can be named.

Examples:

* `/users`
* `/orders/15`
* `/products/latest`

Each resource has a **unique identifier (URI)**.

---

### Resource Diagram

```
URI
 |
 v
Resource
 |
 v
Representation
```

---

## Representation

A **Representation** is the data sent over the network.

Common formats:

* JSON
* XML

---

### Representation Example Diagram

```
Resource: User
 |
 v
JSON Representation
{
  "id": 1,
  "name": "Alice"
}
```

---

## Media Types

Media Types describe **how data is structured**.

Examples:

* `application/json`
* `application/xml`

They tell the client **how to interpret the bytes**.

---

## Hypermedia (HATEOAS)

Hypermedia allows APIs to:

* Include links
* Guide client actions dynamically

---

### Hypermedia Example Diagram

```
Response
{
  "id": 1,
  "name": "Alice",
  "links": {
     "self": "/users/1",
     "orders": "/users/1/orders"
  }
}
```

⚠️ Fully hypermedia-driven APIs are rare, but the concept is important.

---

## Summary

* REST is an **architectural style**
* It defines **how systems communicate**
* REST is based on **six constraints**
* Resources are identified by **URIs**
* Data is exchanged as **representations**
* REST commonly uses **HTTP**

---

