
---

# **Good URI Practices**

## **Characteristics of a Good API**

When designing a web application, one of the most important decisions we make is **what URIs (URLs) our application will expose to users**. These URIs form the visible surface of our API and heavily influence usability, clarity, and maintainability.

In this lesson, we focus mainly on **URLs**, which are a specific subtype of **URIs**.

---

## **1. URI vs URL**

A **URI (Uniform Resource Identifier)** is a string that identifies a resource either by:

* **Location**, or
* **Name**

When a URI identifies a resource by its **network location**, it is commonly referred to as a **URL (Uniform Resource Locator)**.

👉 In web applications, we almost always deal with **URLs**.

---

## **2. URLs as Part of an API**

A web API is composed of multiple **endpoints**, each defined by:

* A **URL (address)**
* An **HTTP method** (GET, POST, PUT, PATCH, DELETE, etc.)
* Additional metadata (headers, authentication, etc.)

Think of the URL as the **address part** of the endpoint.

📌 The URLs we expose are the **main interface** users interact with.
Well-designed URLs lead to:

* Better developer experience
* Easier maintenance
* Cleaner evolution of the API over time

---

## **3. What Makes a Good API?**

A good API should strive to be:

* **Compliant with existing specifications**
* **Intuitively clear**
* **Predictable**
* **Consistent**
* **Concise**

While these qualities are hard to measure precisely, keeping them in mind helps guide better design decisions.

---

## **4. Core Principles for Good URI Design**

We now explore **seven key principles** that help create clear, consistent, and standards-aligned URLs.

---

## **2.2 Naming**

### **Guidelines**

Good URLs should be:

* Short
* Human-readable
* Lowercase
* Free of underscores (`_`)
* Use **hyphens (`-`)** if word separation is needed

### **Examples**

✅ **GOOD**

```
/workers/1
```

❌ **BAD**

```
/Workers/1
/worker_info/1
/information-about-worker/1
```

The good example:

* Is concise
* Uses lowercase
* Follows the common pattern `{resource}/{id}`

### **Semantic Clarity**

`/workers/1` clearly suggests:

* Fetching information about a single worker
* Likely returns name, email, and basic profile data

If we want **related but more detailed information**, such as tasks assigned to the worker, we should use URL structure to express that relationship:

✅ **GOOD**

```
/workers/1/tasks
```

This avoids bloated responses and improves performance.

🎯 **Goal:** URLs should clearly communicate **what they represent and what they return**.

---

## **2.3 Verbs vs. Nouns**

In **REST**, everything is modeled as a **resource**, not an action.

### **Rule**

➡️ **Use nouns in URLs, not verbs**

### **Examples**

✅ **GOOD**

```
/tasks/5
```

❌ **BAD**

```
/retrieve/tasks/5
/retrieve-tasks/5
/get/tasks/1
/tasks/1/update-status
```

### **Why Avoid Verbs in URLs?**

1. **Redundancy**
   The HTTP method already tells us the action:

    * GET → retrieve
    * POST → create
    * PATCH → update

2. **Flexibility**
   The same URL can support multiple operations:

```
GET    /tasks/1/status   → retrieve status
PATCH  /tasks/1/status   → update status
```

📌 **Rule of thumb:**

> If your URI contains a verb, it’s a design smell and suggests incomplete domain modeling.

---

## **2.4 Plural vs. Singular**

Should resource names be **singular** or **plural**?

Both approaches are valid. The key principle is **consistency**.

### **Consistent Plural Usage**

```
/workers/1
/campaigns/1
/tasks/1
/workers/1/tasks
```

### **Consistent Singular Usage**

```
/worker/1
/campaign/1
/task/1
/worker/1/task
```

### **Inconsistent (BAD)**

```
/workers/1
/campaign/1
/task/1
/worker/1/tasks
```

### **REST Perspective**

Roy Fielding defines a resource as:

> “A conceptual mapping to a set of entities.”

This suggests plural naming can feel more natural:

* Collections → `/workers`
* Individual item → `/workers/1`

Still, **either choice is fine**—just be consistent.

---

## **2.5 ID vs. UUID**

Resources must be uniquely identifiable. Two common approaches:

### **Numeric ID**

```
/workers/1
```

### **UUID**

```
/workers/3b4e34ae-07ed-11ed-8e37-831e56f1f17b
```

### **Response Examples**

Using ID:

```json
{ "id": 1, "name": "John" }
```

Using UUID:

```json
{ "id": "3b4e34ae-07ed-11ed-8e37-831e56f1f17b", "name": "John" }
```

### **Trade-offs**

| Approach | Pros                                              | Cons                                                           |
| -------- | ------------------------------------------------- | -------------------------------------------------------------- |
| **ID**   | Short URLs, smaller payloads, built-in DB support | Guessable, weaker security, collision risk across environments |
| **UUID** | Hard to guess, better security, fewer collisions  | Longer URLs, larger payloads, must be generated & stored       |

📌 **Decision depends on the domain**

* Public or low-risk apps → IDs may be fine
* Sensitive or financial data → UUIDs are safer

---

## **2.6 Query Parameters**

Query parameters are ideal for:

* Filtering
* Sorting
* Pagination
* Formatting output

### **Avoid Encoding Logic in the Path**

❌ **BAD**

```
/workers/sort/name/dir/asc
```

### **Preferred Approach**

✅ **GOOD**

```
/workers?sort=name&dir=asc
```

✔ Order of query parameters does **not** matter.

### **Avoid Complex Objects in Query Params**

❌ **BAD**

```
/workers?name=John&email=john@email.com
```

#### **Why?**

* URLs are plain text → security risks
* Encoded objects grow very large
* Long URLs may break browsers or servers

📌 **Rule:**
Use **query parameters for simple filters**
Use the **request body** for complex objects

---

## **2.7 Response Format**

Do **not** specify response formats in the URL.

❌ **BAD**

```
/tasks/json
/tasks?format=json
/tasks?format=html
```

### **Correct Approach: HTTP Headers**

✅ **GOOD**

```
/tasks
```

**Headers**

```
Accept: application/json
```

This follows HTTP standards and keeps URLs clean.

---

## **2.8 Other Important Considerations**

Good URI design is only part of a good API.

Also consider:

### **Security**

* Who can access which resources?
* Role-based and permission-based access

### **HTTP Semantics**

* GET must not change server state
* Use POST, PUT, PATCH, DELETE correctly

### **HTTP Status Codes**

* 200 OK
* 201 Created
* 400 Bad Request
* 401 Unauthorized
* 404 Not Found
* 500 Internal Server Error

### **Error Handling**

* Clear error messages
* No internal implementation leaks
* Helpful but secure feedback for users

---

## **Summary**

Good URI practices lead to:

* Clear APIs
* Better developer experience
* Easier maintenance
* More secure systems

🎯 **Design URLs that are predictable, readable, and meaningful—and let HTTP do the rest.**

---
