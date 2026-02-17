# Leverage HTTP Verbs and Semantics in REST API Operations

## 2.1 Defining REST API Operations

When building a Spring-based REST API, one of the most important design goals is to expose a **clear, consistent, and intuitive interface**.

To achieve this, we rely on:

* **HTTP verbs** (GET, POST, PUT, PATCH, DELETE)
* **Meaningful URLs**
* **Proper status codes**
* **Consistent request and response bodies**

REST itself is protocol-agnostic, but HTTP provides the semantics we need to express operations in a standardized way.

In this section, we’ll analyze how CRUD operations are implemented using HTTP semantics in a Spring controller.

---

# 2.2 Retrieve Resources – GET

Retrieving resources is the most common operation in any REST API.

### Controller Implementation

```java
@GetMapping
public List<CampaignDto> listCampaigns() {
    // …
}

@GetMapping("/{id}")
public CampaignDto findOne(@PathVariable Long id) {
    // …
}
```

### HTTP Semantics

| Aspect                | Value             |
| --------------------- | ----------------- |
| HTTP Verb             | GET               |
| URL                   | `/campaigns`      |
| URL (single resource) | `/campaigns/{id}` |
| Response Status       | 200 OK            |
| Idempotent            | Yes               |
| Safe                  | Yes               |

GET is:

* **Safe** → Does not modify server state
* **Idempotent** → Multiple identical calls produce the same effect

---

## Query Parameters and Pagination

For list operations, we often support filtering and pagination:

```java
@GetMapping
public Page<CampaignDto> listCampaigns(
        @RequestParam int page,
        @RequestParam int size) {
    // …
}
```

Key points:

* `@RequestParam` binds query parameters.
* A page is **not a resource**, but part of the **representation**.
* Provide metadata (next page, previous page, total elements).

Example:

```
GET /campaigns?page=0&size=10
```

---

# 2.3 Create – POST

## Controller Implementation

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public CampaignDto create(@RequestBody CampaignDto newCampaign) {
    // …
}
```

---

## HTTP Semantics

| Aspect          | Value        |
| --------------- | ------------ |
| HTTP Verb       | POST         |
| URL             | `/campaigns` |
| Request Body    | CampaignDto  |
| Response Status | 201 Created  |
| Idempotent      | No           |

POST is used when:

* The server determines the identity of the resource.
* A new resource is created.

---

## 📌 Extracted Request Body (Create Campaign)

From the image, the JSON used in Postman for creating a campaign is:

```json
{
  "code": "CNew",
  "name": "Campaign New",
  "description": "Description of Campaign New"
}
```

### Example Request

```
POST /campaigns
Content-Type: application/json
```

Response:

```json
{
  "id": 5,
  "code": "CNew",
  "name": "Campaign New",
  "description": "Description of Campaign New",
  "tasks": []
}
```

Status:

```
201 Created
```

---

## Why POST for Create?

According to HTTP specification:

* POST is used to create a resource **not yet identified by the server**.
* POST is **not idempotent**.

If we send the same POST request multiple times:

* Multiple resources may be created.

---

# 2.4 Update – PUT

## Controller Implementation

```java
@PutMapping("/{id}")
public CampaignDto update(@PathVariable Long id,
                          @RequestBody CampaignDto updatedCampaign) {
    // …
}
```

---

## HTTP Semantics

| Aspect          | Value               |
| --------------- | ------------------- |
| HTTP Verb       | PUT                 |
| URL             | `/campaigns/{id}`   |
| Path Variable   | id                  |
| Request Body    | Full representation |
| Response Status | 200 OK              |
| Idempotent      | Yes                 |

PUT is used for:

* **Full replacement** of a resource
* Updating a specific, identified resource

---

## 📌 Extracted Request Body (Update Campaign)

From the image, the JSON used for updating is:

```json
{
  "name": "Campaign 1 Updated",
  "description": "Description of Campaign 1 Updated"
}
```

### Example Request

```
PUT /campaigns/1
Content-Type: application/json
```

Response:

```json
{
  "id": 1,
  "code": "C1",
  "name": "Campaign 1 Updated",
  "description": "Description of Campaign 1 Updated",
  "tasks": [
    {
      "id": 1,
      "uuid": "b4816853-f15f-4907-9e4f-9bef1fdb65c1",
      "name": "Task 1",
      "description": "Task 1 Description",
      "dueDate": "2030-01-12",
      "status": "To Do",
      "campaignId": 1,
      "assignee": null
    }
  ]
}
```

Status:

```
200 OK
```

---

## Why PUT for Update?

HTTP spec defines PUT as:

> “Create or replace the state of the target resource.”

PUT is **idempotent**.

If we send the same PUT request multiple times:

* After the first update, subsequent identical calls do not change the state further.

This makes PUT ideal for full updates.

---

# PUT vs POST – Critical Difference

| Feature             | POST | PUT       |
| ------------------- | ---- | --------- |
| Creates resource    | Yes  | Sometimes |
| Replaces resource   | No   | Yes       |
| Client specifies ID | No   | Yes       |
| Idempotent          | No   | Yes       |

In our domain:

* The server generates IDs.
* Therefore, POST is required for creation.

---

# 2.5 Partial Update – PATCH

PUT is for full replacement.

For partial updates, we use:

```java
@PatchMapping("/{id}")
public CampaignDto partialUpdate(@PathVariable Long id,
                                 @RequestBody Map<String, Object> updates) {
    // …
}
```

PATCH:

* Applies partial modifications
* Does not require full resource representation
* May not be idempotent

PATCH requires more careful design and validation logic.

---

# 2.6 Delete – DELETE

Even though not all domains allow deletion, let’s examine the semantics.

## Controller Implementation

```java
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
    // …
}
```

---

## HTTP Semantics

| Aspect          | Value             |
| --------------- | ----------------- |
| HTTP Verb       | DELETE            |
| URL             | `/campaigns/{id}` |
| Path Variable   | id                |
| Response Status | 204 No Content    |
| Idempotent      | Yes               |

DELETE is idempotent.

Even if:

* First call → 204 No Content
* Second call → 404 Not Found

The **effect on the server remains the same** (resource is gone).

Other possible status codes:

* 200 OK (if returning representation)
* 202 Accepted (if deletion is asynchronous)

---

# 3. Idempotency in Practice

Understanding idempotency is critical.

### Idempotent Methods

* GET
* PUT
* DELETE

### Non-idempotent Methods

* POST
* Often PATCH

Idempotency refers to:

> The effect on the server state, not the response code.

---

# 4. Summary of REST Operations in Spring

| Operation      | HTTP Verb | URL             | Status | Idempotent     |
| -------------- | --------- | --------------- | ------ | -------------- |
| List           | GET       | /campaigns      | 200    | Yes            |
| Retrieve       | GET       | /campaigns/{id} | 200    | Yes            |
| Create         | POST      | /campaigns      | 201    | No             |
| Update         | PUT       | /campaigns/{id} | 200    | Yes            |
| Partial Update | PATCH     | /campaigns/{id} | 200    | Not guaranteed |
| Delete         | DELETE    | /campaigns/{id} | 204    | Yes            |

---

# 5. Key Takeaways

1. REST defines constraints — HTTP provides semantics.
2. HTTP verbs must match their intended meaning.
3. Use:

    * POST for creation
    * PUT for full replacement
    * PATCH for partial updates
    * DELETE for removal
    * GET for retrieval
4. Respect idempotency rules.
5. Return correct status codes.

By correctly leveraging HTTP verbs and semantics, we create APIs that are:

* Predictable
* Standardized
* Easy to consume
* Truly REST-aligned

---