
---

# Part 2: Communication Between a Client and Server

---

## Introduction

In this part of our REST overview, we will **dive deep into how communication happens between a client and a server**.

We will:

* Break client–server communication into simple pieces
* Introduce new technical terms **before** using them
* Use a **RESTful application example** to make everything practical and easy to understand

By the end of this lesson, you should clearly understand:

* What a client sends to a server
* What a server sends back
* How REST organizes communication around resources

---

## Example Application Scenario

To make things concrete, let’s imagine we are building a **web application** that stores information about:

* **Customers**
* **Orders**
* **Items (products)**

This application can:

* Create data
* Read data
* Update data
* Delete data

These operations are often called **CRUD operations**.

---

## REST Communication at a High Level

### Core Idea

In a RESTful architecture:

* The **client** sends a request to the **server**
* The **server** processes the request
* The **server** sends back a response

---

### High-Level Communication Flow Diagram

```
Client
  |
  |  HTTP Request
  v
Server
  |
  |  HTTP Response
  v
Client
```

---

## Requests in REST

Client–server communication in REST almost always uses the **HTTP protocol**.

---

## What Is an HTTP Request?

### Definition

An **HTTP request** is a message sent by a client to a server asking it to:

* Retrieve data
* Create data
* Modify data
* Delete data

---

### Main Parts of an HTTP Request

An HTTP request usually consists of:

1. **HTTP Method**
2. **URI**
3. **Headers**
4. **Request Body** (optional)

---

### HTTP Request Structure Diagram

```
+-------------------+
| HTTP Method       |
| URI               |
| Headers           |
|                   |
| Request Body      |
+-------------------+
```

---

## Resources and URIs

---

### What Is a Resource?

**Definition:**
A **resource** is any piece of data that can be named and accessed.

Examples:

* A customer
* An order
* A product
* An image
* A document

In REST, **everything revolves around resources**.

---

### Key Idea

Client–server communication in REST is about **manipulating resources**.

---

## What Is a URI?

### Definition

A **URI (Uniform Resource Identifier)** is a string that uniquely identifies a resource.

You can think of a URI as:

> an **address** that tells the server *which resource* the client is talking about

---

### Endpoints

An **endpoint** is simply a **URI that accepts requests**.

For this lesson, we will use the terms **URI** and **endpoint** interchangeably.

---

## Resources in Our Example Application

Our application has three main resources:

* **customers**
* **orders**
* **items**

Each resource must have:

* A **unique URI**
* A clear and intuitive structure

---

## REST URI Design

### General Rule

REST URIs usually:

* Start with a **plural noun**
* Use slashes (`/`) to show hierarchy
* Use IDs to identify specific resources

---

### Customer URIs

```
/customers
/customers/23
/customers/4
```

Meaning:

* `/customers` → all customers
* `/customers/23` → customer with ID 23

---

### Nested Resources (Orders)

```
/customers/4/orders
/customers/1/orders/12
```

Meaning:

* Orders belonging to a specific customer

---

### Deeply Nested Resources (Items)

```
/customers/1/orders/12/items
```

Meaning:

* All products inside order 12 made by customer 1

---

### URI Hierarchy Diagram

```
customers
  |
  +-- {customerId}
        |
        +-- orders
              |
              +-- {orderId}
                    |
                    +-- items
```

---

## HTTP Methods

---

### What Is an HTTP Method?

**Definition:**
An **HTTP method** tells the server **what action** the client wants to perform on a resource.

---

### Common HTTP Methods in REST

| Method | Purpose                     |
| ------ | --------------------------- |
| GET    | Retrieve data               |
| POST   | Create a new resource       |
| PUT    | Update an existing resource |
| DELETE | Remove a resource           |

---

### Method Usage Diagram

```
GET     ---> Read
POST    ---> Create
PUT     ---> Update
DELETE  ---> Delete
```

---

## Headers

---

### What Are HTTP Headers?

**Definition:**
HTTP headers are **key–value pairs** that carry additional information about a request or response.

Headers help the client and server understand:

* Data format
* Authentication
* Caching rules

---

### Accept Header (Very Common)

The **Accept** header tells the server:

> what format the client expects in the response

Example:

```
Accept: application/json
```

---

## MIME Types

---

### What Is a MIME Type?

**Definition:**
A **MIME type** describes the format of data being sent.

Each MIME type has:

* A **type**
* A **subtype**

---

### Common MIME Types

| Category    | Examples                          |
| ----------- | --------------------------------- |
| Text        | text/plain, text/html             |
| Image       | image/png, image/jpeg             |
| Audio       | audio/mpeg                        |
| Video       | video/mp4                         |
| Application | application/json, application/xml |

---

## Request Body

---

### What Is a Request Body?

**Definition:**
The **request body** contains the data sent from the client to the server.

---

### When Is a Request Body Used?

| Method | Request Body |
| ------ | ------------ |
| GET    | Usually no   |
| DELETE | Usually no   |
| POST   | Yes          |
| PUT    | Yes          |

---

### Why POST and PUT Use a Body

* POST → create a resource (you must send the data)
* PUT → update a resource (you must send new values)

---

## JSON Request Body Example

Suppose we want to create a new customer.

### Customer Fields:

* Name
* Email
* Phone number

### JSON Body

```
{
  "name": "Amigo",
  "email": "amigo@jr.com",
  "phone": "+1 (222) 333-4444"
}
```

---

## Putting Requests Together (Examples)

---

### Example 1: Get a Customer

```
GET /customers/23
Accept: application/json
```

**Description:**
Retrieve customer number 23 in JSON format.

---

### Example 2: Create a Customer

```
POST /customers
```

```
{
  "name": "Amigo",
  "email": "amigo@jr.com",
  "phone": "+1 (222) 333-4444"
}
```

**Description:**
Create a new customer.

---

### Example 3: Update a Customer

```
PUT /customers/1
```

```
{
  "name": "Ben",
  "email": "bigben@jr.com",
  "phone": "+86 (868) 686-8686"
}
```

**Description:**
Update customer number 1.

---

### Example 4: Delete an Order

```
DELETE /customers/12/orders/6
```

**Description:**
Delete order number 6 for customer number 12.

---

## Responses

---

### What Is an HTTP Response?

An **HTTP response** is the message sent by the server back to the client.

---

### Parts of an HTTP Response

1. **Status code**
2. **Headers**
3. **Response body**

---

### Response Structure Diagram

```
+-------------------+
| Status Code       |
| Headers           |
|                   |
| Response Body     |
+-------------------+
```

---

## HTTP Response Codes

---

### What Is an HTTP Status Code?

**Definition:**
An HTTP status code is a **three-digit number** that tells the client the result of its request.

Example:

```
201 Created
401 Unauthorized
404 Not Found
```

---

### Why Status Codes Matter

They allow the client to:

* Understand what happened
* Decide what to do next

---

## Status Code Categories

| Category | Meaning       |
| -------- | ------------- |
| 1XX      | Informational |
| 2XX      | Success       |
| 3XX      | Redirection   |
| 4XX      | Client error  |
| 5XX      | Server error  |

---

### Common Examples

| Code | Meaning               |
| ---- | --------------------- |
| 200  | OK                    |
| 201  | Created               |
| 400  | Bad Request           |
| 401  | Unauthorized          |
| 404  | Not Found             |
| 500  | Internal Server Error |

---

## Full Communication Flow Diagram

```
Client
  |
  |  POST /customers
  |  JSON Body
  v
Server
  |
  |  201 Created
  |  JSON Response
  v
Client
```

---

## Summary

* REST communication uses **HTTP**
* Clients manipulate **resources**
* Resources are identified by **URIs**
* HTTP methods define **actions**
* Headers define **metadata**
* Bodies carry **data**
* Status codes explain **results**

---

