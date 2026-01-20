
---

# From Basic Authentication to Token Authentication

---

## 1. Introduction

Modern applications expose APIs that are consumed by:

* Web clients
* Mobile applications
* Microservices
* Third-party systems

Traditional security mechanisms—particularly **session-based authentication using cookies**—do not scale well in these environments. As systems evolved toward **distributed, stateless architectures**, the need for a **portable, self-contained security credential** became clear.

This lesson examines:

1. The available API security options
2. The evolution of token implementations
3. The internal structure of JWTs
4. Why JWT became the dominant standard
5. How JWT integrates with Spring Security

---

## 2. API Security Options (High-Level Overview)

The API security ecosystem provides multiple authentication strategies, each with different trade-offs.

### 2.1 Common API Security Mechanisms

| Mechanism                 | Stateful             | Description                                        |
| ------------------------- | -------------------- |----------------------------------------------------|
| Basic Authentication      | ❌ Stateless         | Base64-encoded credentials sent with every request |
| Digest Authentication     | ❌ Stateless         | Hash-based challenge/response                      |
| Form-based Authentication | ✅ Stateful          | Cookies + server sessions                          |
| OAuth 2                   | ✅ Typically stateful| Authorization framework                            |
| OAuth 2 + JWT             | ❌ Stateless         | Token-based authorization                          |
| Custom Token              | Depends              | Application-specific                               |
| X.509 Authentication      | ❌ Stateless         | Certificate-based, niche use                       |

### Observations

* **Form-based authentication** relies on cookies and server-side session state.
* **Basic and Digest authentication** resend credentials on every request.
* **OAuth 2 + JWT** allows secure, stateless, scalable authorization.

📌 **JWT is not a replacement for OAuth 2; it is often used as the token format within OAuth 2.**

---

## 3. Token-Based Authentication: Why Tokens?

### Problems with Credential-Based Authentication

* Credentials must be sent repeatedly
* Credentials cannot be easily scoped
* No fine-grained expiration
* Increased exposure surface

### Token-Based Approach

* Credentials exchanged **once**
* Server issues a **token**
* Token represents identity and permissions
* Client sends token with each request

---

## 4. Evolution of Token Implementations

### 4.1 SAML (Security Assertion Markup Language)

**Characteristics**

* XML-based
* Extremely expressive
* Rich cryptographic options
* Heavy infrastructure requirements

**Problems**

* Complex XML tooling
* Poor mobile support
* Large message sizes

SAML was powerful but impractical for lightweight clients.

---

### 4.2 Simple Web Token (SWT)

Created by **Microsoft, Google, and Yahoo** as a reaction to SAML’s complexity.

**Characteristics**

* JSON-based
* Lightweight
* Easy to parse

**Limitations**

* Only symmetric signing
* Limited cryptographic flexibility
* Too restrictive for many use cases

---

### 4.3 JSON Web Token (JWT)

JWT combines:

* The simplicity of SWT
* The security strength of SAML
* Broad JSON ecosystem support

JWT quickly became the **industry standard**.

📌 JWT “hit the sweet spot” between complexity and flexibility.

---

## 5. What Is a JSON Web Token?

A **JSON Web Token** is:

* A **compact**
* **URL-safe**
* **self-contained**
* **cryptographically signed** token

JWTs transmit **claims** between parties.

Example (formatted for readability):

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9
.
TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ
```

---

## 6. JWT Structure (Detailed)

A JWT consists of **three Base64URL-encoded parts**:

```
HEADER . PAYLOAD . SIGNATURE
```

---

### 6.1 Header

The header describes:

* Token type
* Cryptographic algorithm

Example:

```json
{
  "typ": "JWT",
  "alg": "HS256"
}
```

* `typ`: Media type of the token
* `alg`: Algorithm used to sign the token

📌 The header is not encrypted.

---

### 6.2 Payload (Claims)

The payload contains **claims**, which are statements about a subject.

Example:

```json
{
  "iss": "scotch.io",
  "sub": "1234567890",
  "exp": 1300718380,
  "name": "Eugen",
  "admin": true
}
```

---

#### Types of Claims

##### 1. Registered Claims (Standardized)

| Claim | Meaning                   |
| ----- | ------------------------- |
| `iss` | Issuer                    |
| `sub` | Subject (user identifier) |
| `aud` | Audience                  |
| `exp` | Expiration time           |
| `iat` | Issued at                 |

##### 2. Public Claims

* Defined by agreement
* Avoid collisions via namespaces

##### 3. Private Claims

* Application-specific
* Used heavily in real systems

📌 JWT does **not** mandate which claims must be present.

---

### 6.3 Signature

The signature ensures:

* Token integrity
* Token authenticity
* Protection against tampering

Signature is computed using:

```
Base64UrlEncode(header) + "." +
Base64UrlEncode(payload) + secret/key
```

Example (HMAC):

```
HMACSHA256(data, secret)
```

---

## 7. Signing and Encryption (JWS & JWE)

JWT works together with:

* **JWS (JSON Web Signature)** → integrity & authenticity
* **JWE (JSON Web Encryption)** → confidentiality

Most systems use:

* **Signed JWTs**
* Not encrypted JWTs (payload visible but safe)

---

## 8. JWT Algorithms (RFC 7518)

### Common “alg” Header Values

| Algorithm | Description                |
| --------- | -------------------------- |
| HS256     | HMAC + SHA-256             |
| HS384     | HMAC + SHA-384             |
| HS512     | HMAC + SHA-512             |
| RS256     | RSA + SHA-256              |
| ES256     | ECDSA + SHA-256            |
| PS256     | RSA-PSS                    |
| none      | No signature (discouraged) |

📌 **Production recommendation:** `RS256`

---

## 9. What Problem Does JWT Solve?

JWT standardizes:

* Claim representation
* Signing
* Validation

JWT eliminates:

* Server-side session storage
* Per-request credential exchange

---

## 10. Practical Uses of JWT

JWTs are commonly used for:

* Authentication
* Authorization
* Federated identity
* Stateless sessions
* Client-side secrets

---

## 11. JWT Authentication Flow (Detailed Diagram)

```
Client
  |
  | 1. POST /login (username + password)
  |
  v
Authentication Server
  |
  | 2. Credentials validated
  | 3. JWT generated & signed
  |
  v
Client
  |
  | 4. Authorization: Bearer <JWT>
  |
  v
Resource Server
  |
  | 5. JWT signature verified
  | 6. Claims extracted
  | 7. SecurityContext populated
  |
  v
Access Granted / Denied
```

---

## 12. JWT in Spring Security

In Spring Security, JWT is treated as a **stateless authentication credential**.

### Core Components

| Component             | Purpose                       |
| --------------------- | ----------------------------- |
| JwtEncoder            | Creates JWT                   |
| JwtDecoder            | Validates JWT                 |
| SecurityFilterChain   | Defines security rules        |
| Authentication        | Represents authenticated user |
| SecurityContextHolder | Stores authentication         |

---

## 13. Spring Security JWT Integration (Modern)

### Token Validation

```java
http
  .authorizeHttpRequests(auth -> auth
      .anyRequest().authenticated()
  )
  .oauth2ResourceServer(oauth2 ->
      oauth2.jwt()
  );
```

Spring Security:

* Extracts token
* Validates signature
* Builds Authentication
* Applies authorization rules

---

## 14. JWT + Roles + Privileges

JWT often contains:

```json
"authorities": [
  "ROLE_ADMIN",
  "READ_PRIVILEGE",
  "WRITE_PRIVILEGE"
]
```

Spring Security uses these for:

* `hasRole('ADMIN')`
* `hasAuthority('WRITE_PRIVILEGE')`

Works seamlessly with **role hierarchies**.

---

## 15. Security Considerations

JWT is secure when:

* HTTPS is enforced
* Tokens expire quickly
* Refresh tokens are used
* Strong keys are used
* Sensitive data is not stored in payload

---

## 16. Summary & Takeaways

* JWT is a **compact, secure, standardized token**
* Designed for **stateless authentication**
* Replaced older XML-heavy solutions
* Integrates naturally with Spring Security
* Ideal for microservices and APIs

---

## Final Key Insight

> **JWT is not just a token format — it is the foundation of modern, stateless API security.**

---
