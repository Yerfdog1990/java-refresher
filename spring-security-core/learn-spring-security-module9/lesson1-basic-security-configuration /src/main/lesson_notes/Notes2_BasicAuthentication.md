
---

# Basic Authentication for the API

---

# Page 1 of 3

## The Basics of Basic Authentication

---

## 1. Introduction

Basic Authentication is the **lowest common denominator** for securing an API.

It is:

* Extremely well-known
* Universally supported
* Simple to implement
* Supported out of the box by Spring Security and Spring Boot

Because of these characteristics, it is often the **first authentication mechanism developers encounter** when learning API security.

---

## 2. What Is Basic Authentication?

Basic Authentication is an HTTP authentication scheme defined by the HTTP standard.

It works by sending a username and password with **every HTTP request**, encoded using Base64, in the `Authorization` header.

### Key Characteristics

* Stateless
* Simple
* Credentials-based
* No session management by default

---

## 3. How Basic Authentication Works

### Step-by-Step Flow

1. Client sends a request to a protected resource
2. Server responds with `401 Unauthorized`
3. Client retries the request with credentials
4. Credentials are sent in the HTTP header
5. Server validates credentials
6. Access is granted or denied

---

## 4. Authorization Header Format

The credentials are sent as:

```
Authorization: Basic <Base64(username:password)>
```

Example:

```
username:password
↓ Base64
dXNlcm5hbWU6cGFzc3dvcmQ=
```

---

## 5. Practical Example Using cURL

```bash
curl -u username:password https://api.example.com/resource
```

Equivalent explicit form:

```bash
curl -H "Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=" \
https://api.example.com/resource
```

---

## 6. Basic Authentication in Spring Boot

Spring Boot **assumes Basic Authentication by default** when Spring Security is on the classpath.

Minimal configuration:

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());

    return http.build();
}
```

📌 With this configuration:

* Every endpoint is protected
* Browser or client will prompt for credentials

---

## 7. Advantages of Basic Authentication

### 7.1 Ubiquitous Support

* Browsers
* Mobile clients
* Networking devices
* Reverse proxies
* API gateways

### 7.2 Excellent Framework Support

* Spring Security
* Spring Boot auto-configuration
* Minimal setup

### 7.3 Simplicity

* No token generation
* No storage
* No expiration handling

⚠️ This simplicity, however, is also the source of many limitations.

---

# Page 2 of 3

## Problems and Limitations of Basic Authentication

---

## 1. Base64 Is Not Encryption

Basic Authentication uses **Base64 encoding**, not encryption.

### Why This Is a Problem

Base64 is:

* Reversible
* Trivial to decode
* Not secure by itself

Example:

```bash
echo dXNlcm5hbWU6cGFzc3dvcmQ= | base64 --decode
```

Output:

```
username:password
```

---

## 2. Mandatory HTTPS Requirement

To avoid credential exposure:

* **HTTPS is mandatory**

### What HTTPS Protects

* Data in transit
* Man-in-the-middle attacks

### What HTTPS Does NOT Protect

* Server-side logs
* Internal routing
* Monitoring systems
* Debug tooling

📌 Credentials may still appear in:

* Access logs
* Error logs
* Proxy logs

---

## 3. Credentials Are Sent on Every Request

With Basic Authentication:

* Username and password are transmitted **on every request**
* This increases the attack surface

### Comparison with Token-Based Security

| Mechanism   | What Is Sent        |
| ----------- | ------------------- |
| Basic Auth  | Full credentials    |
| JWT / Token | Limited-scope token |

📌 Tokens can be:

* Expired
* Revoked
* Scoped

Passwords cannot.

---

## 4. Client Receives the “Master Key”

The client must store:

* The actual username
* The actual password

### Consequences

* No delegation
* No partial access
* No trust boundaries

If compromised:

* Entire account is compromised
* Password must be rotated everywhere

This is especially dangerous for:

* Third-party clients
* Mobile apps
* Browser-based apps

---

## 5. Browser Credential Caching

Browsers treat Basic Authentication specially.

### Browser Behavior

* Credentials are cached
* Automatically resent on every request
* Can be permanently stored

### Security Impact

* Opens the door to CSRF attacks
* User has little control
* Logout is unreliable

📌 This is why Basic Authentication and browsers are a dangerous combination.

---

# Page 3 of 3

## Advanced Limitations and When to Use Basic Authentication

---

## 1. No Support for Two-Factor Authentication (2FA)

Modern security relies on:

* Something you know (password)
* Something you have (device, token)

Basic Authentication:

* Has no challenge-response mechanism
* Has no second authentication step
* Cannot integrate OTP or biometrics

📌 Two-factor authentication is impossible with Basic Authentication.

---

## 2. No Authorization Semantics

Basic Authentication:

* Identifies the user
* Does NOT describe permissions

There is:

* No place for roles
* No place for privileges
* No scope concept

Authorization must be:

* Hardcoded
* Derived server-side only

This severely limits flexibility.

---

## 3. No Distinction Between Users and Machines

Basic Authentication:

* Treats all clients the same
* Cannot identify:

    * Human users
    * Backend services
    * Automated jobs

### Unsupported Use Cases

* Service-to-service authentication
* Acting-on-behalf-of users
* Delegation (OAuth-style)

---

## 4. Why OAuth and Token-Based Auth Exist

OAuth and JWT were created to solve exactly these problems:

| Problem       | Basic Auth | Token Auth |
| ------------- | ---------- | ---------- |
| Delegation    | ❌          | ✅          |
| Scopes        | ❌          | ✅          |
| 2FA           | ❌          | ✅          |
| Revocation    | ❌          | ⚠️         |
| Machine users | ❌          | ✅          |

---

## 5. When Basic Authentication Is Acceptable

Basic Authentication can still be reasonable when:

* Internal APIs
* Trusted clients only
* Low-risk environments
* Short-lived systems
* Prototyping and learning

---

## 6. When Basic Authentication Is a Show Stopper

Do NOT use Basic Authentication when:

* Third-party clients are involved
* Browser-based access is required
* Fine-grained authorization is needed
* 2FA is required
* High-security environments exist

---

## 7. Final Takeaways

* Basic Authentication is simple but dangerous
* HTTPS is mandatory but insufficient
* Credentials are repeatedly exposed
* No support for modern security patterns
* Best used only in narrow, controlled scenarios

---

### End of Lesson

> *Basic Authentication is easy to start with—but hard to justify in production systems.*

---

