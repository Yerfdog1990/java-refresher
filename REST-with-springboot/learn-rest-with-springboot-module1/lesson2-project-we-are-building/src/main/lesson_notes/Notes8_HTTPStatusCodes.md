
---

# List of HTTP Status Codes

### Detailed Lesson Notes

---

## 1. Introduction to HTTP Status Codes

**HTTP status codes** are **three-digit numbers** returned by a server in response to a client’s request.
They tell the client **what happened** when the request was processed.

### Why Status Codes Matter

Status codes:

* Help browsers know **how to react**
* Help developers **debug issues**
* Are critical for **REST APIs**
* Influence **SEO**, caching, and security
* Are frequently tested in **exams & interviews**

### Status Code Structure

| Digit               | Meaning            |
| ------------------- | ------------------ |
| **1st digit**       | Category (1xx–5xx) |
| **Last two digits** | Specific result    |

Example:

```
HTTP/1.1 404 Not Found
```

---

## 2. Status Code Categories Overview

| Category | Meaning       |
| -------- | ------------- |
| **1xx**  | Informational |
| **2xx**  | Success       |
| **3xx**  | Redirection   |
| **4xx**  | Client Error  |
| **5xx**  | Server Error  |

---

## 3. 1xx – Informational Responses

**Informational responses** mean:

> “The request was received and is being processed.”

⚠️ These responses:

* Do **not** contain a body
* Are **temporary**
* Are rarely used in everyday applications

---

### 3.1 Common 1xx Codes

#### 100 Continue

* Server received headers
* Client may send request body

**Used when**:

* Uploading large payloads
* Client sends `Expect: 100-continue`

---

#### 101 Switching Protocols

* Server agrees to switch protocols

**Example use**:

* HTTP → WebSocket

---

#### 102 Processing (WebDAV – Deprecated)

* Request is valid but still processing
* Prevents client timeout

⚠️ Deprecated — avoid using.

---

#### 103 Early Hints

* Sends headers early before final response
* Improves performance (e.g. preload assets)

---

📌 *Diagram Space: Client–Server handshake showing 100/103 flow*

---

## 4. 2xx – Success Responses

A **2xx status code** means:

> “The request was successful.”

These are the **most important codes** in APIs.

---

### 4.1 Common 2xx Codes

#### 200 OK

Standard success response.

| Method | Meaning           |
| ------ | ----------------- |
| GET    | Resource returned |
| POST   | Action completed  |
| PUT    | Resource updated  |

---

#### 201 Created

* A new resource was created
* Typically used after **POST**

📌 Best practice:

```
201 Created
Location: /users/42
```

---

#### 202 Accepted

* Request accepted
* Processing is **asynchronous**

**Example**:

* Video processing
* Image conversion

---

#### 204 No Content

* Request succeeded
* No response body

**Common use**:

* DELETE requests

---

#### 206 Partial Content

* Partial response using `Range` header
* Used for resumable downloads

---

#### WebDAV-Specific 2xx Codes

| Code | Meaning          |
| ---- | ---------------- |
| 207  | Multi-Status     |
| 208  | Already Reported |
| 226  | IM Used          |

---

📌 *Diagram Space: REST CRUD → 200 / 201 / 204 mapping*

---

## 5. 3xx – Redirection Responses

A **3xx status code** means:

> “The client must take additional action.”

Usually involves **redirecting to another URL**.

---

### 5.1 Common 3xx Codes

#### 300 Multiple Choices

* Multiple representations available
* Client must choose

---

#### 301 Moved Permanently

* Resource has **permanently moved**
* Search engines update links

✅ Best for:

* HTTP → HTTPS
* URL restructuring

---

#### 302 Found

* Temporary redirect
* Method **may change to GET**

⚠️ Historically confusing

---

#### 303 See Other

* Redirect using **GET**
* Common after POST (PRG pattern)

---

#### 304 Not Modified

* Cached version still valid
* No body sent

Used with:

* `If-Modified-Since`
* `ETag`

---

#### 307 Temporary Redirect

* Same method must be reused
* Safer alternative to 302

---

#### 308 Permanent Redirect

* Permanent
* Method preserved

---

📌 *Diagram Space: Redirect flow (301 vs 302 vs 307 vs 308)*

---

## 6. 4xx – Client Error Responses

A **4xx status code** means:

> “The client made a mistake.”

The request **should not be retried unchanged**.

---

### 6.1 Most Important 4xx Codes

#### 400 Bad Request

* Malformed request
* Invalid syntax or payload

---

#### 401 Unauthorized

* Authentication required or failed
* Must include `WWW-Authenticate`

🧠 Means: *Unauthenticated*

---

#### 403 Forbidden

* Authentication succeeded
* Permission denied

🧠 Means: *Authenticated but not allowed*

---

#### 404 Not Found

* Resource does not exist
* May exist later

---

#### 405 Method Not Allowed

* HTTP method not supported
* Server must send `Allow` header

---

#### 409 Conflict

* Resource state conflict
* Common in concurrent updates

---

#### 410 Gone

* Resource permanently removed
* Should be deleted from indexes

---

#### 413 Content Too Large

* Payload exceeds server limits

---

#### 415 Unsupported Media Type

* Unsupported `Content-Type`

---

#### 418 I’m a Teapot ☕

* April Fools’ joke (RFC 2324)
* Sometimes used humorously

---

#### 429 Too Many Requests

* Rate limiting triggered
* Retry later

---

#### 451 Unavailable for Legal Reasons

* Access blocked by legal demand

---

📌 *Diagram Space: Authentication vs Authorization (401 vs 403)*

---

## 7. 5xx – Server Error Responses

A **5xx status code** means:

> “The server failed to process a valid request.”

The problem is **not the client’s fault**.

---

### 7.1 Common 5xx Codes

#### 500 Internal Server Error

* Generic server failure
* No specific cause exposed

---

#### 501 Not Implemented

* Method not supported
* Feature may be added later

---

#### 502 Bad Gateway

* Invalid response from upstream server

---

#### 503 Service Unavailable

* Server overloaded or down
* Usually temporary

---

#### 504 Gateway Timeout

* Upstream server did not respond in time

---

#### 505 HTTP Version Not Supported

* Server does not support HTTP version used

---

#### Advanced / WebDAV Codes

| Code | Meaning                         |
| ---- | ------------------------------- |
| 507  | Insufficient Storage            |
| 508  | Loop Detected                   |
| 510  | Not Extended                    |
| 511  | Network Authentication Required |

---

📌 *Diagram Space: Proxy / Gateway failure flow*

---

## 8. Non-Standard HTTP Status Codes

Non-standard codes are **vendor-specific** and **not defined by IETF**.

---

### 8.1 IIS (Microsoft)

| Code | Meaning                      |
| ---- | ---------------------------- |
| 440  | Login Timeout                |
| 449  | Retry With                   |
| 450  | Blocked by Parental Controls |

---

### 8.2 nginx

| Code | Meaning                  |
| ---- | ------------------------ |
| 444  | No Response              |
| 494  | Request Header Too Large |
| 499  | Client Closed Request    |

---

### 8.3 Cloudflare

| Code    | Meaning              |
| ------- | -------------------- |
| 520–526 | Origin server errors |
| 522     | Connection timed out |
| 525     | SSL Handshake Failed |

---

### 8.4 Framework-Specific

| Platform | Code | Meaning             |
| -------- | ---- | ------------------- |
| Laravel  | 419  | Page Expired (CSRF) |
| Spring   | 420  | Method Failure      |
| Twitter  | 420  | Enhance Your Calm   |
| Shopify  | 430  | Security Rejection  |

---

### 8.5 Miscellaneous

| Code | Meaning                 |
| ---- | ----------------------- |
| 598  | Network read timeout    |
| 599  | Network connect timeout |

---

## 9. REST API Best Practices 🧠

| Scenario          | Recommended Code |
| ----------------- | ---------------- |
| Successful GET    | 200              |
| Resource created  | 201              |
| Delete success    | 204              |
| Invalid input     | 400              |
| Not authenticated | 401              |
| Not authorized    | 403              |
| Not found         | 404              |
| Conflict          | 409              |
| Rate limited      | 429              |
| Server failure    | 500              |

---

## 10. Exam & Interview Tips

* **401 ≠ 403** (authentication vs authorization)
* Use **201 + Location** for POST
* Prefer **307/308** over 302
* Use **429** for rate limiting
* 5xx errors mean **retry may succeed**

---

## 11. One-Sentence Summary

> **HTTP status codes provide a standardized way for servers to communicate request outcomes, enabling reliable web communication, debugging, and RESTful design.**

---

