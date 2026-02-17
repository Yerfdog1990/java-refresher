# Good URI Practices
## 1. Introduction: URI vs URL in API Design

When designing a web application, one of the most important aspects is the **URIs** it exposes.

* **URI (Uniform Resource Identifier)**: A string that identifies a resource by name or location.
* **URL (Uniform Resource Locator)**: A specific type of URI that identifies a resource by its network location.

In web APIs:

* An **endpoint** is defined by:

    * Its **URL** (address)
    * Its **HTTP method** (GET, POST, PATCH, DELETE, etc.)
    * Additional metadata (headers, request body, etc.)

The URL is the “address” part of the endpoint and forms the visible structure of your API. Good URL design leads to:

* Better user experience
* Easier maintenance
* Improved consistency
* Reduced long-term complexity

---

## 2. Characteristics of a Good API

A well-designed API should be:

1. **Compliant with existing specifications**
   Follow HTTP and REST standards.

2. **Intuitively clear**
   Developers should easily understand what an endpoint does.

3. **Predictable**
   Similar resources follow similar patterns.

4. **Consistent**
   Naming conventions and structure should not vary randomly.

5. **Concise**
   Keep URLs short and readable.

These principles guide all URI design decisions.

---

# Core Principles of Good URI Design

## 3. Naming Conventions

### Best Practices

A good URL should be:

* Short
* Human-readable
* Lowercase
* Free of underscores (use hyphens if needed)
* Structured as `{resource}/{id}`

### Examples

**GOOD:**

```
/workers/1
```

**BAD:**

```
/Workers/1          (uppercase)
/worker_info/1      (underscore)
/information-about-worker/1   (too long)
```

The good example:

* Is concise
* Uses lowercase
* Clearly represents a single worker resource

### Use URL Structure to Express Relationships

Instead of overloading one endpoint with too much data, use hierarchical semantics:

**GOOD:**

```
/workers/1/tasks
```

This clearly indicates:

* We are accessing tasks
* That belong to worker with ID = 1

This improves:

* Readability
* Performance
* Logical separation of resources

### Key Goal of Naming

Create URLs that:

* Are easy to read
* Clearly express what they return
* Avoid unnecessary complexity

---

## 4. Verbs vs Nouns

### REST Principle: Everything Is a Resource

In REST:

* URLs represent **resources (nouns)**
* HTTP methods represent **actions (verbs)**

### Correct Approach

**GOOD:**

```
/tasks/5
```

**BAD:**

```
/retrieve/tasks/5
/retrieve-tasks/5
/get/tasks/1
/tasks/1/update-status
```

### Why Avoid Verbs in URLs?

1. **Redundant Information**
   The HTTP method already specifies the action:

    * GET → retrieve
    * POST → create
    * PATCH → update
    * DELETE → remove

2. **Inflexibility**
   You may want the same URL for different operations:

```
GET   /tasks/1/status    → retrieve status
PATCH /tasks/1/status    → update status
```

If the verb is embedded in the URI, you would need separate endpoints.

### Rule of Thumb

If you see a verb in the URI, it’s usually a design smell and may indicate poor domain modeling.

---

## 5. Plural vs Singular

There is no universally correct choice between singular and plural.

You may use:

```
/worker/1
```

or

```
/workers/1
```

### The Real Rule: Be Consistent

**Consistent Plural Example:**

```
/workers/1
/campaigns/1
/tasks/1
/workers/1/tasks
```

**Consistent Singular Example:**

```
/worker/1
/campaign/1
/task/1
/worker/1/task
```

**BAD (Mixed Forms):**

```
/workers/1
/campaign/1
/task/1
/worker/1/tasks
```

### Why Plural Is Often Preferred

According to REST principles:

* A resource maps to a **set of entities**
* `/workers` represents the collection
* `/workers/1` represents an element within that collection

Still, consistency is more important than the specific choice.

---

## 6. ID vs UUID

Resources must be uniquely identifiable.

Two common approaches:

### 1. Numeric ID

```
/workers/1
```

Response:

```
{id: 1, name: "John"}
```

### 2. UUID

```
/workers/3b4e34ae-07ed-11ed-8e37-831e56f1f17b
```

Response:

```
{id: "3b4e34ae-07ed-11ed-8e37-831e56f1f17b", name: "John"}
```

---

### Comparison

| Aspect         | ID                         | UUID              |
| -------------- | -------------------------- | ----------------- |
| URL Length     | Short                      | Long              |
| Payload Size   | Small                      | Larger            |
| Availability   | Built-in (DB primary key)  | Must be generated |
| Security       | Guessable                  | Hard to guess     |
| Collision Risk | Higher across environments | Very low          |

### When to Use ID

* Internal systems
* Low security sensitivity
* Simpler applications

### When to Use UUID

* Public APIs
* Sensitive data (financial, personal)
* Multi-environment deployments
* Security-critical systems

Choice depends on business requirements and security needs.

---

## 7. Query Parameters

Query parameters are used for:

* Filtering
* Sorting
* Formatting
* Pagination

### BAD (Path-Based Formatting)

```
/workers/sort/name/dir/asc
```

Problems:

* Too verbose
* Requires multiple endpoints
* Hard to maintain

### GOOD (Query Parameters)

```
/workers?sort=name&dir=asc
```

Advantages:

* Cleaner URLs
* Flexible
* Order of parameters doesn’t matter

---

### Avoid Sending Complex Objects in Query Parameters

Example:

```
/workers?name=John&email=john@email.com
```

#### Why This Is Risky

1. **Security Risks**

    * URLs are plain text
    * May expose sensitive data
    * Logged in browser history and server logs

2. **Length Limitations**

    * Encoded objects can become very long
    * Browsers may truncate long URLs
    * Can cause unpredictable failures

### Best Practice

* Use query parameters for simple filtering
* Use request body for complex objects

Treat URLs as limited-length structures with predefined parameters.

---

## 8. Response Format

Do NOT specify format in the URL:

**BAD:**

```
/tasks/json
/tasks?format=json
/tasks?format=html
```

### Correct Approach: Use HTTP Headers

```
GET /tasks
Accept: application/json
```

The `Accept` header defines the expected response media type.

This:

* Keeps URLs clean
* Follows HTTP standards
* Improves flexibility

---

## 9. Other Important Considerations

### 1. Security

Design early for:

* Authentication
* Authorization
* Role-based access
* Data exposure restrictions

Questions to ask:

* Can anyone access this endpoint?
* Should all users have the same permissions?

---

### 2. Correct Use of HTTP Methods

Ensure semantic correctness:

* GET → must NOT modify state
* POST → create
* PUT/PATCH → update
* DELETE → remove

Using verbs incorrectly breaks REST principles and causes confusion.

---

### 3. Proper HTTP Status Codes

Use meaningful status codes:

* 200 → OK
* 201 → Created
* 400 → Bad Request
* 401 → Unauthorized
* 404 → Not Found
* 500 → Internal Server Error

Status codes should clearly communicate what happened.

---

### 4. Error Handling

A good API:

* Handles errors gracefully
* Provides useful error messages
* Does NOT expose internal implementation details
* Maintains security

---

# Summary of Good URI Practices

To design clear, maintainable, and RESTful URIs:

* Use short, lowercase, human-readable names
* Use nouns, not verbs
* Be consistent with singular or plural
* Choose ID or UUID based on business needs
* Use query parameters for filtering and formatting
* Use headers for response formats
* Respect HTTP semantics
* Handle errors properly
* Consider security from the start

Good URI design improves:

* Developer experience
* System scalability
* Long-term maintainability
* Security and reliability

A well-designed API is not accidental — it is the result of deliberate, consistent architectural decisions.

---