
---

# 🌐 Spring Security URL Authorization

### **(With Maven, Spring Security 6+, and Code Examples)**

---

# 1. **Introduction: Why URL Authorization Matters**

Securing your application is not only about authentication (who are you?)—you must also handle **authorization** (what are you allowed to do?).

In Spring Security, URL authorization allows you to:

✔ Restrict certain endpoints to certain roles
✔ Protect sensitive operations (POST, PUT, DELETE)
✔ Allow public access to registration or login pages
✔ Prevent malicious users from accessing admin-only paths
✔ Enforce role-based or permission-based security

---

# 2. **Core Concepts in Spring Security URL Authorization**

## **2.1. URL Authorization**

Controls **which roles/authorities** can access specific URL patterns.

Examples:

```java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/posts/**").hasAnyAuthority("POST_READ")
```

Order matters — the FIRST match wins.

---

## **2.2. HTTP Method Authorization**

You can restrict access not just by URL, but also by method:

```java
.requestMatchers(HttpMethod.POST, "/posts/**").hasRole("USER")
.requestMatchers(HttpMethod.DELETE, "/posts/**").hasAnyRole("USER", "ADMIN")
```

This is essential when protecting CRUD APIs.

---

## **2.3. Authorization Methods**

| Method                           | Description                                    |
| -------------------------------- | ---------------------------------------------- |
| `hasRole("ADMIN")`               | Role check (adds `ROLE_` prefix automatically) |
| `hasAuthority("ADMIN")`          | Exact authority string (no prefix added)       |
| `hasAnyRole("USER", "ADMIN")`    | Match any listed role                          |
| `hasIpAddress("192.168.1.0/24")` | Restrict by IP                                 |
| `permitAll()`                    | Allow everyone                                 |
| `denyAll()`                      | Deny everyone                                  |
| `authenticated()`                | Require authentication                         |
| `anonymous()`                    | Only non-authenticated users                   |
| `access("SPEL expression")`      | Custom logic using Spring Expression Language  |

---

# 3. **antMatchers vs. requestMatchers**

| Feature      | antMatchers()                      | requestMatchers()                           |
| ------------ | ---------------------------------- | ------------------------------------------- |
| Status       | ❌ Deprecated in Spring Security 6+ | ✔ Recommended                               |
| Pattern type | Ant path only                      | Path, HTTP method, headers, custom matchers |
| Example      | `.antMatchers("/admin/**")`        | `.requestMatchers("/admin/**")`             |

So in **Spring Security 6+**, ALWAYS use:

```java
.requestMatchers(...)
```

---

# 4. Maven Dependency Setup

Add these to your **pom.xml**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

# 5. **Modern Spring Security URL Authorization Configuration (Spring Boot 3/Security 6)**

Below is a complete configuration using:

✔ URL authorization
✔ Method-based restrictions
✔ Role-based access
✔ Public registration endpoint
✔ H2 console access
✔ Form login

---

# **SecurityConfig.java**

```java
package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // For H2 console & testing
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            .authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers("/users/register", "/", "/home").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // User-specific post operations
                .requestMatchers(HttpMethod.GET, "/posts/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/posts/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/posts/**").hasRole("USER")

                // Admin-only delete
                .requestMatchers(HttpMethod.DELETE, "/posts/**")
                    .hasAnyRole("ADMIN", "USER")

                // Any admin endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Everything else → authentication required
                .anyRequest().authenticated()
            )

            // Form login
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )

            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

# 6. How Authorization Rules Are Processed

Spring Security processes URL rules **top → bottom**.

```
1. Public endpoints        → permitAll()
2. H2 console              → permitAll()
3. User CRUD endpoints     → role restrictions
4. Admin area              → admin only
5. Catch-all rule          → authenticated()
```

The FIRST matching rule wins.

---

# 7. Example Authorization Flow Diagram (Text-Based)

```
 ┌──────────────────────────────┐
 | Incoming HTTP Request        |
 └──────────────┬──────────────┘
                ▼
        Match URL pattern?
                ▼
   ┌────────────┴────────────┐
   | First rule that matches  |
   └────────────┬────────────┘
                ▼
   Is authentication required?
                ▼
       ┌────────┴────────┐
       | Yes              | No
       ▼                  ▼
   Check roles        Allow access
       ▼
  Authorized?
   |Yes | No
   ▼     ▼
Allow   Deny (403)
```

---

# 8. Adding Method-Level Authorization

Because we added:

```java
@EnableMethodSecurity
```

You can now also secure service-layer methods:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deletePost(Long id) {}
```

---

# 9. URL Authorization Best Practices

### ✔ Always secure admin endpoints

### ✔ Use HTTP method restrictions

### ✔ Put `anyRequest().authenticated()` at the bottom

### ✔ Disable CSRF only for APIs or H2 console

### ✔ Do NOT rely solely on frontend checks

### ✔ Use roles for broad access; authorities for fine-grained permissions

### ✔ Method-level authorization is a second safety layer

---

# 10. Summary

Spring Security URL Authorization enables you to:

* Control access based on URL patterns
* Lock down operations via HTTP method rules
* Use roles or authorities for fine-grained control
* Combine path-level and method-level security
* Build secure production-grade applications quickly

Your modern configuration uses:

✔ `requestMatchers()` (not deprecated)
✔ Method security: `@EnableMethodSecurity`
✔ Role-based restrictions
✔ Public endpoints
✔ H2 console access
✔ Form login

---
