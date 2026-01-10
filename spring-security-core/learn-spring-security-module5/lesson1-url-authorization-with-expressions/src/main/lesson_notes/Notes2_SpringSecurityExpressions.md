
---

# 📘 Lesson Notes: Spring Security Expressions (Spring 6)

## 1. Introduction

**Spring Security Expressions** allow you to define **fine-grained authorization rules** using **Spring Expression Language (SpEL)**.

They are used in:

* URL authorization
* Method-level security
* UI-level security (Thymeleaf)
* Custom access rules

Expressions go beyond simple role checks and allow:

* IP-based access
* Request method validation
* Logical combinations (AND, OR, NOT)
* Access to authentication and request objects

---

## 2. Modern Spring 6 Configuration Style

❌ Deprecated:

```java
.antMatchers("/secured").access(...)
```

✅ Spring 6 approach:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/secured").access(...)
            .anyRequest().authenticated()
        )
        .formLogin()
        .logout();
    return http.build();
}
```

---

## 3. Mini Project Overview 🚀

### 🎯 Goal

Secure a `/secured` endpoint using **multiple Spring Security expressions**, testing different authorization rules.

### 🧩 Components

* Spring Boot 3
* Spring Security 6
* In-memory users
* One secured controller endpoint
* Postman/browser testing

---

## 4. Project Setup (Dependencies)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## 5. Test Users Configuration

```java
@Bean
UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user")
            .password("{noop}password")
            .roles("USER")
            .build();

    UserDetails admin = User.withUsername("admin")
            .password("{noop}password")
            .roles("ADMIN")
            .build();

    return new InMemoryUserDetailsManager(user, admin);
}
```

---

## 6. Secured Controller

```java
@RestController
public class SecuredController {

    @GetMapping("/secured")
    public String securedPage() {
        return "You have accessed a secured endpoint";
    }
}
```

---

## 7. Core Security Expressions (Spring 6)

---

### 7.1 `hasRole()` vs `hasAuthority()`

```java
.requestMatchers("/secured")
.access(new WebExpressionAuthorizationManager("hasRole('USER')"))
```

Equivalent to:

```java
.access(new WebExpressionAuthorizationManager("hasAuthority('ROLE_USER')"))
```

📌 **Rule**:

* `hasRole("USER")` → internally checks `ROLE_USER`

---

### 7.2 IP-Based Authorization (`hasIpAddress`)

❌ Deny localhost:

```java
.access(new WebExpressionAuthorizationManager(
    "hasIpAddress('192.168.1.0/24')"
))
```

✅ Allow localhost (IPv6):

```java
.access(new WebExpressionAuthorizationManager(
    "hasIpAddress('::1')"
))
```

🧪 Result:

* Wrong IP → `403 Forbidden`
* Correct IP → Access granted

---

### 7.3 Anonymous Access

Allow access **only if NOT logged in**:

```java
.access(new WebExpressionAuthorizationManager("isAnonymous()"))
```

🧪 Test:

* Logged in → ❌ 403
* Remove `JSESSIONID` → ✅ Access allowed

---

### 7.4 Working with the Request Object

Restrict to **GET requests only**:

```java
.access(new WebExpressionAuthorizationManager(
    "request.method == 'GET'"
))
```

Allow everything **except POST**:

```java
.access(new WebExpressionAuthorizationManager(
    "request.method != 'POST'"
))
```

🧪 Test:

* Browser GET → ✅ Allowed
* Postman POST → ❌ Forbidden (unless allowed)

---

### 7.5 Negating Expressions (`not`)

```java
.access(new WebExpressionAuthorizationManager(
    "!hasIpAddress('::1')"
))
```

📌 Meaning:

* Allow everyone **except localhost**

---

### 7.6 Logical AND / OR

#### AND (both must be true)

```java
.access(new WebExpressionAuthorizationManager(
    "hasRole('ADMIN') and principal.username == 'user'"
))
```

❌ Usually fails (user ≠ admin)

---

#### OR (either condition true)

```java
.access(new WebExpressionAuthorizationManager(
    "hasRole('ADMIN') or principal.username == 'user'"
))
```

✅ User OR admin can access

---

## 8. Importance of Rule Order ⚠️

Spring Security evaluates rules **top to bottom**.

❌ Wrong:

```java
.anyRequest().authenticated()
.requestMatchers("/secured").access(...)
```

✅ Correct:

```java
.requestMatchers("/secured").access(...)
.anyRequest().authenticated()
```

📌 **First match wins**

---

## 9. Testing with Postman 🧪

### Example POST request

* URL: `http://localhost:8080/secured`
* Method: POST
* Authorization: Basic Auth
* Username: `user`
* Password: `password`

✔️ Useful for testing request.method expressions

---

## 10. Where Else Expressions Are Used

| Area            | Example                                 |
| --------------- | --------------------------------------- |
| URL security    | `.requestMatchers().access()`           |
| Method security | `@PreAuthorize("hasRole('ADMIN')")`     |
| Thymeleaf UI    | `sec:authorize="hasRole('ROLE_ADMIN')"` |
| Custom logic    | SpEL with request/auth objects          |

---

## 11. Summary Table 📌

| Expression       | Purpose                |
| ---------------- | ---------------------- |
| `hasRole()`      | Role-based access      |
| `hasAuthority()` | Authority-based access |
| `hasIpAddress()` | IP filtering           |
| `isAnonymous()`  | Unauthenticated access |
| `request.method` | HTTP method control    |
| `!expression`    | Negation               |
| `and / or`       | Complex logic          |

---

## 12. Final Takeaways 🏁

* Spring Security Expressions provide **maximum flexibility**
* Use them when **API methods are not expressive enough**
* Always use **Spring 6 DSL**
* Order of rules matters
* Combine expressions for real-world scenarios

---

