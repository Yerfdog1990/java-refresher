
---

# HTTP Headers

## HTTP Security Response Headers – Lesson Notes

---

## 1. Introduction to HTTP Headers

**HTTP headers** allow the **client (browser)** and **server** to exchange **additional metadata** about a request or response.

They are sent as **key–value pairs**:

```
Header-Name: header-value
```

### Why Headers Matter for Security

Properly configured HTTP headers can:

* Prevent **Cross-Site Scripting (XSS)**
* Block **Clickjacking**
* Reduce **information disclosure**
* Enforce **HTTPS**
* Limit **cross-origin abuse**

> 💡 Headers are one of the **cheapest and most effective security controls** you can add to a web application.

---

## 2. Categories of HTTP Headers

### By Context

| Category                   | Purpose                              |
| -------------------------- | ------------------------------------ |
| **Request Headers**        | Information about the client/request |
| **Response Headers**       | Metadata about the response/server   |
| **Representation Headers** | Describe the body (type, encoding)   |
| **Payload Headers**        | Transport-related information        |

### By Proxy Behavior

| Type           | Description                           |
| -------------- | ------------------------------------- |
| **End-to-End** | Passed unchanged to final destination |
| **Hop-by-Hop** | Used only for a single connection     |

---

## 3. Key HTTP Security Response Headers

---

## 3.1 X-Frame-Options

Prevents a page from being embedded in a frame or iframe — protects against **clickjacking**.

### Example

```
X-Frame-Options: DENY
```

### Notes

* Only effective for **interactive pages**
* **Not useful** for APIs or redirects
* **Obsoleted** by CSP `frame-ancestors`

### Recommendation ✅

```
X-Frame-Options: DENY
```

or preferably use CSP instead.

---

## 3.2 Content-Security-Policy (CSP)

Defines **which resources** (scripts, styles, images) are allowed to load.

### Why CSP Is Powerful

* Prevents **XSS**
* Prevents **data injection**
* Controls third-party content

### Example (basic)

```
Content-Security-Policy: default-src 'self'
```

### Notes

* Complex but extremely powerful
* Most important modern security header
* Less relevant for pure JSON APIs

📌 *Leave space here for CSP diagram / flow*

---

## 3.3 X-XSS-Protection ❌

Legacy browser XSS filter.

### Example

```
X-XSS-Protection: 0
```

### Warning ⚠️

* Can **introduce vulnerabilities**
* Deprecated in modern browsers

### Recommendation ❌

Do **not** use it. Disable explicitly.

---

## 3.4 X-Content-Type-Options

Prevents **MIME-type sniffing**.

### Example

```
X-Content-Type-Options: nosniff
```

### Why It Matters

* Stops browsers from executing files as scripts
* Prevents MIME confusion attacks

### Recommendation ✅

Always enable.

---

## 3.5 Referrer-Policy

Controls how much referrer information is shared.

### Example

```
Referrer-Policy: strict-origin-when-cross-origin
```

### Why It Matters

* Prevents leaking URLs, tokens, and query strings

### Recommendation ✅

Force modern safe behavior for all browsers.

---

## 3.6 Content-Type

Specifies the **media type** of the response.

### Example

```
Content-Type: text/html; charset=UTF-8
```

### Security Importance

* Prevents XSS caused by incorrect content interpretation
* Charset is **mandatory** for HTML pages

---

## 3.7 Set-Cookie (Security Attributes)

Not a security header itself, but critical.

### Secure Attributes

* `Secure`
* `HttpOnly`
* `SameSite`

📌 *Refer students to Session Management Cheat Sheet*

---

## 3.8 Strict-Transport-Security (HSTS)

Forces browsers to use **HTTPS only**.

### Example

```
Strict-Transport-Security: max-age=63072000; includeSubDomains; preload
```

### Warning ⚠️

* Misconfiguration can **lock users out**
* Only enable after HTTPS is fully stable

---

## 3.9 Expect-CT ❌

Certificate Transparency reporting header.

### Recommendation ❌

Do **not** use. Deprecated and unnecessary.

---

## 3.10 CORS – Access-Control-Allow-Origin

Controls cross-origin access.

### Example

```
Access-Control-Allow-Origin: https://yoursite.com
```

### Best Practice

* Avoid `*` unless it’s a **public API**
* SOP is safer by default

---

## 3.11 COOP, COEP, CORP (Browser Isolation)

These headers protect against **Spectre-style attacks**.

| Header | Purpose                               |
| ------ | ------------------------------------- |
| COOP   | Isolates browsing context             |
| COEP   | Blocks unapproved cross-origin embeds |
| CORP   | Restricts resource sharing            |

### Recommended Values

```
Cross-Origin-Opener-Policy: same-origin
Cross-Origin-Embedder-Policy: require-corp
Cross-Origin-Resource-Policy: same-site
```

---

## 3.12 Permissions-Policy

Controls browser features (camera, mic, GPS).

### Example

```
Permissions-Policy: geolocation=(), camera=(), microphone=()
```

### Why Important

* Limits damage of XSS
* Prevents abuse of browser APIs

---

## 3.13 Privacy & Tracking Headers

### Disable Google FLoC

```
Permissions-Policy: interest-cohort=()
```

### Remove Fingerprinting Headers

```
Server: webserver
```

Remove:

* `X-Powered-By`
* `X-AspNet-Version`
* `X-AspNetMvc-Version`

---

## 3.14 X-Robots-Tag

Controls search engine indexing.

### Examples

```
X-Robots-Tag: noindex, nofollow
X-Robots-Tag: index, follow
```

Used especially for:

* PDFs
* Images
* Private content

---

## 3.15 X-DNS-Prefetch-Control

Controls DNS prefetching.

### Example

```
X-DNS-Prefetch-Control: off
```

---

## 3.16 Deprecated Headers (Do Not Use)

| Header    | Status     |
| --------- | ---------- |
| HPKP      | Deprecated |
| Expect-CT | Deprecated |
| Pragma    | Deprecated |
| Warning   | Deprecated |

---

## 4. Implementing Headers in Servers

### PHP

```php
header("X-Frame-Options: DENY");
```

### Apache

```apache
Header always set X-Frame-Options "DENY"
```

### Nginx

```nginx
add_header X-Frame-Options "DENY" always;
```

### Express (Node.js)

```js
app.use(helmet.frameguard({ action: "sameorigin" }));
```

---

## 5. Testing Security Headers

### Tools

* **Mozilla Observatory**
* **SmartScanner**

These tools:

* Scan headers
* Identify missing protections
* Score overall security posture

---

## 6. Key Exam Takeaways 🧠

* Headers are **metadata**, not content
* CSP is the **most powerful security header**
* HSTS enforces HTTPS
* CORS relaxes SOP — use carefully
* Remove headers that reveal server details
* Deprecated headers should not be used

---

## 7. One-Line Summary

> **HTTP security headers harden browsers against attacks without changing application code.**

---

