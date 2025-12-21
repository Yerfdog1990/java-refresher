
---

# Spring Security – CORS

**Last Updated : 23 Jul, 2025**

---

## 1. Introduction to CORS

**Cross-Origin Resource Sharing (CORS)** is a security mechanism implemented by **web browsers** that controls whether a web page can make requests to a different **origin** (domain, protocol, or port) than the one that served the page.

By default, browsers **block cross-origin HTTP requests** initiated by JavaScript. CORS provides a controlled way for servers to explicitly allow such requests while still maintaining security.

CORS plays a crucial role in modern web applications, especially when:

* The frontend and backend are hosted on different domains
* A SPA (React, Angular, Vue) consumes a REST API
* Authentication cookies or authorization headers are involved

> ⚠️ **Important:**
> CORS is enforced by browsers, not by servers. Spring Security only **supplies headers** that browsers interpret.

---

## 2. CORS vs CSRF (Clarification)

* **CORS** controls *who can read responses* from the browser
* **CSRF** protects *state-changing requests* from being forged

Although often mentioned together, **CORS does not replace CSRF protection**.

---

## 3. Working of CORS

When a web page makes a request to a different origin:

1. The browser may first send a **preflight request**
2. This is an HTTP **OPTIONS** request
3. The server responds with **CORS headers**
4. The browser decides whether the actual request is allowed

This mechanism ensures that only approved origins can access server resources.

---

## 4. CORS Headers (Detailed Explanation)

### 4.1 Access-Control-Allow-Origin

Specifies which origins are allowed to access the resource.

**Usage**

* `*` → allows any origin
* Specific origin → restricts access

**Example**

```http
Access-Control-Allow-Origin: *
```

or

```http
Access-Control-Allow-Origin: http://example.com
```

> ⚠️ Using `*` makes resources publicly accessible.
> In production, **always specify exact origins**, especially when credentials are involved.

---

### 4.2 Access-Control-Allow-Methods

Specifies allowed HTTP methods.

**Example**

```http
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
```

This ensures only permitted operations can be performed on the resource.

---

### 4.3 Access-Control-Allow-Headers

Specifies which headers can be used in the actual request.

**Example**

```http
Access-Control-Allow-Headers: Content-Type, Authorization
```

Restricting headers improves security by blocking unauthorized custom headers.

---

### 4.4 Access-Control-Allow-Credentials

Determines whether cookies or authentication data can be sent.

**Example**

```http
Access-Control-Allow-Credentials: true
```

> ⚠️ Cannot be used with `Access-Control-Allow-Origin: *`

---

### 4.5 Access-Control-Expose-Headers

Specifies which response headers are accessible to the browser.

**Example**

```http
Access-Control-Expose-Headers: Content-Length, X-Kuma-Revision
```

Useful when clients need custom response headers.

---

### 4.6 Access-Control-Max-Age

Defines how long the preflight request can be cached.

**Example**

```http
Access-Control-Max-Age: 3600
```

Reduces the number of preflight requests and improves performance.

---

### 4.7 Access-Control-Request-Headers / Methods

Sent by the browser during preflight to inform the server of:

* Headers to be used
* HTTP method to be used

The server decides whether to allow the actual request.

---

## 5. Implementation of Spring Security – CORS

This project demonstrates how to implement **CORS with Spring Security** to control cross-origin access securely.

---

## Step 1: Create the Spring Boot Project

Add the following dependencies:

* Spring Web
* Spring Security
* Lombok
* Spring DevTools

---

## Step 2: Application Properties

```properties
spring.application.name=spring-security-cors

spring.security.user.name=user
spring.security.user.password=password
```

---

## Step 3: Security Configuration

### Recommended (Spring Security–aligned approach)

```java
package org.example.springsecuritycors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-CSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### Explanation

* `addMapping("/**")` → applies to all endpoints
* `allowedOrigins(...)` → restricts allowed domains
* `allowedMethods(...)` → limits HTTP operations
* `allowedHeaders(...)` → restricts request headers
* `allowCredentials(true)` → allows cookies/auth headers
* `maxAge(3600)` → caches preflight response

---

## Alternative: Using `CorsFilter`

```java
@Bean
public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost:8080");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
}
```

### Comparison of Approaches

| Aspect                           | CorsConfigurationSource | CorsFilter             |
| -------------------------------- | ----------------------- | ---------------------- |
| Integration with Spring Security | ✅ Yes                   | ⚠️ Indirect            |
| Granular control                 | ✅ High                  | ❌ Low                  |
| Security                         | ✅ More restrictive      | ⚠️ More permissive     |
| Recommended for production       | ✅ Yes                   | ❌ Only for quick setup |

**Best practice:**
✔ Use `CorsConfigurationSource` with `.cors(Customizer.withDefaults())`

---

## Step 4: Controller

```java
package org.example.springsecuritycors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, CORS!";
    }
}
```

---

## Step 5: Main Class

```java
package org.example.springsecuritycors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSecurityCorsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityCorsApplication.class, args);
    }
}
```

---

## Step 6: Run the Application

* Application runs on **port 8080**
* Accessing `/hello` requires authentication
* Login page is provided by Spring Security

---

## Step 7: Testing Cross-Origin Requests

Make a request from:

```
http://example.com
```

To:

```
http://localhost:8080/hello
```

If the origin is allowed, the response will be:

```text
Hello, CORS!
```

---

## 8. Key Takeaways

* CORS is a **browser-enforced security mechanism**
* Spring Security **supplies headers**, browsers enforce them
* Defaults (`.cors(Customizer.withDefaults())`) are safest
* Avoid `*` origins in production
* Prefer `CorsConfigurationSource` over `CorsFilter`
* CORS improves **security + user experience** when configured correctly

---

### ✅ Final Recommendation

> **Production-ready Spring Security CORS setup = restrictive, explicit, integrated with SecurityFilterChain**

---

# Comparing CORS Strategies: MVC vs REST Applications in Spring Security

---

## 1. Introduction

Cross-Origin Resource Sharing (CORS) is handled **very differently** depending on whether a Spring application is:

* **MVC (server-rendered)**, or
* **REST / API-based** (often consumed by SPAs or mobile clients)

Understanding this distinction is critical to **correct security configuration**.

---

## 2. Key Concept: When Does CORS Matter?

> **CORS only matters when the browser is making a cross-origin request.**

| Application Type          | Same Origin? | CORS Needed? |
| ------------------------- | ------------ | ------------ |
| MVC (Thymeleaf/JSP)       | Yes          | ❌ No         |
| REST + SPA                | No           | ✅ Yes        |
| Mobile / Server-to-Server | No browser   | ❌ No         |

---

## 3. MVC Applications (Server-Rendered UI)

### 3.1 Characteristics

* UI and backend served from **same origin**
* Uses **Thymeleaf, JSP, or Freemarker**
* Authentication via **formLogin**
* Cookies & sessions are used
* Browser never performs cross-origin calls

### 3.2 CORS Strategy for MVC

> **Do not configure CORS unless truly required**

### Recommended Setup

```java
http
    .cors(Customizer.withDefaults()) // effectively no-op
    .csrf(Customizer.withDefaults())
    .formLogin(Customizer.withDefaults());
```

No `CorsConfigurationSource` is required because:

* Requests are same-origin
* Browser does not block them
* CORS headers are unnecessary

### Why Not Configure CORS?

* Adds unnecessary complexity
* Increases attack surface
* Can accidentally expose endpoints

---

### MVC Summary

| Aspect        | MVC               |
| ------------- | ----------------- |
| CORS required | ❌ No              |
| CSRF required | ✅ Yes             |
| Cookies used  | ✅ Yes             |
| Best practice | Avoid CORS config |

---

## 4. REST Applications (API + SPA)

### 4.1 Characteristics

* Frontend hosted on **different origin**
* Uses **React, Angular, Vue**
* Communicates via **JSON APIs**
* Browser enforces CORS
* Authentication via:

    * Cookies
    * JWT
    * OAuth2

---

## 5. REST + SPA CORS Strategy

### 5.1 Must Explicitly Configure CORS

```java
http
    .cors(Customizer.withDefaults())
    .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    );
```

### 5.2 CORS Configuration Bean

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://app.example.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-CSRF-TOKEN"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

---

## 6. CSRF Handling Differences

| Scenario             | MVC       | REST                   |
| -------------------- | --------- | ---------------------- |
| Uses cookies         | ✅ Yes     | Often                  |
| CSRF enabled         | ✅ Yes     | ✅ If cookies used      |
| CSRF token transport | HTML form | Header                 |
| CSRF disabled        | ❌ Never   | Only for stateless JWT |

---

## 7. Authentication & CORS Relationship

| Auth Type                  | CORS Required | Notes                  |
| -------------------------- | ------------- | ---------------------- |
| formLogin                  | ❌             | Same origin            |
| Session + cookies          | ✅             | Cross-origin SPA       |
| JWT (Authorization header) | ✅             | Header must be allowed |
| OAuth2                     | ✅             | Redirect + CORS        |

---

## 8. Common Configuration Mistakes

### ❌ MVC App Mistakes

* Enabling CORS unnecessarily
* Allowing `*` origins
* Disabling CSRF

### ❌ REST App Mistakes

* Forgetting OPTIONS requests
* Using `*` with credentials
* Disabling CSRF while using cookies

---

## 9. Security Comparison Table

| Feature                | MVC App | REST App               |
| ---------------------- | ------- | ---------------------- |
| CORS needed            | ❌       | ✅                      |
| CSRF needed            | ✅       | Depends                |
| Complexity             | Low     | High                   |
| Attack surface         | Smaller | Larger                 |
| Recommended strictness | Default | Explicit & restrictive |

---

## 10. Best-Practice Decision Matrix

| App Type          | CORS Strategy                 |
| ----------------- | ----------------------------- |
| Thymeleaf/JSP     | None                          |
| SPA + Cookie Auth | Strict origins + CSRF         |
| SPA + JWT         | Strict origins, CSRF disabled |
| Public API        | No cookies, CORS limited      |

---

## 11. Final Takeaway

> **MVC apps rarely need CORS. REST apps always need explicit CORS configuration.**

### Golden Rules

* Do not add CORS “just in case”
* Be explicit and restrictive
* Align CORS with authentication strategy
* Defaults first, customize only when required

---

# MVC vs REST: Request Flow Diagrams & SecurityFilterChain Templates

---

## 1️⃣ MVC (Server-Rendered) Request Flow

### Typical Stack

* Thymeleaf / JSP
* Same origin (UI + backend)
* Cookies + Session
* `formLogin`
* CSRF **enabled**
* CORS **not required**

---

### 📌 MVC Request Flow Diagram

```
+------------------+
|     Browser      |
| (Same Origin UI) |
+--------+---------+
         |
         | 1. GET /login
         |
         v
+----------------------------+
| Spring MVC Controller      |
| (Thymeleaf/JSP View)       |
+----------------------------+
         |
         | 2. POST /login (form)
         |    + CSRF token
         |
         v
+----------------------------+
| Spring Security Filter     |
| Chain                      |
| - CsrfFilter               |
| - UsernamePasswordAuth     |
| - SessionManagement        |
+----------------------------+
         |
         | 3. Authenticated
         |
         v
+----------------------------+
| Secured Controller/View    |
+----------------------------+
```

### 🔐 Key Observations (MVC)

* No cross-origin requests
* Browser never blocks requests
* CORS headers are unnecessary
* CSRF tokens are embedded in HTML forms

---

## 2️⃣ REST + SPA Request Flow

### Typical Stack

* React / Angular / Vue
* Different origin
* JSON APIs
* Cookies **or** JWT
* CORS **mandatory**
* CSRF depends on auth method

---

### 📌 REST + SPA Request Flow Diagram

```
+--------------------+
|  SPA (localhost:3000)
|  React / Angular   |
+----------+---------+
           |
           | 1. OPTIONS /api/data
           |    (Preflight request)
           |
           v
+------------------------------+
| Spring Security Filter Chain |
| - CorsFilter                 |
+------------------------------+
           |
           | 2. CORS headers returned
           |
           v
+--------------------+
| Browser Decision   |
| Allow / Block      |
+----------+---------+
           |
           | 3. GET /api/data
           |    Authorization / Cookie
           |
           v
+------------------------------+
| Spring Security Filter Chain |
| - CsrfFilter (optional)      |
| - Auth Filter (JWT/Session)  |
+------------------------------+
           |
           v
+------------------------------+
| REST Controller (JSON)       |
+------------------------------+
```

### 🔐 Key Observations (REST)

* Browser enforces CORS
* Preflight (`OPTIONS`) must succeed
* Headers must be explicitly allowed
* CSRF needed **only if cookies are used**

---

## 3️⃣ SecurityFilterChain Template — MVC Architecture

### ✅ Production-Recommended MVC Configuration

```java
@Bean
public SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http)
        throws Exception {

    http
        // CORS is effectively a no-op (same-origin)
        .cors(Customizer.withDefaults())

        // CSRF must be enabled for form-based login
        .csrf(Customizer.withDefaults())

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
        )

        .formLogin(Customizer.withDefaults())

        .logout(Customizer.withDefaults());

    return http.build();
}
```

### ✔ Why this is correct

* Minimal configuration
* Small attack surface
* Uses Spring Security defaults
* Ideal for Thymeleaf/JSP apps

---

## 4️⃣ SecurityFilterChain Template — REST + SPA (Cookie Auth)

### ✅ SPA using Cookies (Session-based)

```java
@Bean
public SecurityFilterChain apiCookieSecurityFilterChain(HttpSecurity http)
        throws Exception {

    http
        .cors(Customizer.withDefaults())

        .csrf(csrf -> csrf
            .csrfTokenRepository(
                CookieCsrfTokenRepository.withHttpOnlyFalse()
            )
        )

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        );

    return http.build();
}
```

### Required CORS Configuration

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(
        List.of("Authorization", "Content-Type", "X-CSRF-TOKEN")
    );
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

---

## 5️⃣ SecurityFilterChain Template — REST + SPA (JWT Auth)

### ✅ Stateless REST API (JWT)

```java
@Bean
public SecurityFilterChain apiJwtSecurityFilterChain(HttpSecurity http)
        throws Exception {

    http
        .cors(Customizer.withDefaults())

        // No cookies → CSRF not needed
        .csrf(csrf -> csrf.disable())

        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        );

    return http.build();
}
```

### ✔ Why CSRF is disabled here

* No cookies
* Token sent in `Authorization` header
* Browser cannot forge headers cross-site

---

## 6️⃣ MVC vs REST — Security Strategy Comparison

| Feature        | MVC          | REST + SPA          |
| -------------- | ------------ | ------------------- |
| CORS           | ❌ Not needed | ✅ Mandatory         |
| CSRF           | ✅ Always     | ✅ Only with cookies |
| Preflight      | ❌ No         | ✅ Yes               |
| Auth           | formLogin    | Cookie / JWT        |
| Complexity     | Low          | High                |
| Attack surface | Small        | Larger              |

---

## 7️⃣ Architecture Decision Guide

```
Is UI served by Spring?
 ├─ Yes → MVC
 │    ├─ Enable CSRF
 │    └─ Do NOT configure CORS
 └─ No → REST
      ├─ Browser-based client?
      │    ├─ Yes → Configure CORS
      │    │    ├─ Cookies → CSRF ON
      │    │    └─ JWT → CSRF OFF
      │    └─ No → CORS irrelevant
```

---

## 8️⃣ Final Takeaways

* **MVC apps rarely need CORS**
* **REST apps always need explicit CORS**
* **CSRF depends on cookies, not REST**
* Defaults are safest; customize only when required
* CORS protects browsers, CSRF protects users

---

# Misconfigured CORS Attack Scenarios 🚨

These are **real vulnerabilities** caused by incorrect CORS configuration.

---

## ❌ Attack Scenario 1: `allowedOrigins("*") + allowCredentials(true)`

### Misconfiguration

```java
config.setAllowedOrigins(List.of("*"));
config.setAllowCredentials(true);
```

### Why This Is Dangerous

```
Attacker Site (evil.com)
        |
        |  GET /api/user
        |  (browser sends victim cookies)
        |
Victim Browser
        |
        |  CORS allows response
        |
Spring API
```

### Impact

* Attacker reads **authenticated responses**
* Full account takeover possible

> ⚠️ This configuration is **explicitly blocked** by modern browsers,
> but similar patterns still appear in real systems.

---

## ❌ Attack Scenario 2: Overly Permissive Origins

### Misconfiguration

```java
config.setAllowedOrigins(List.of("http://localhost:*"));
```

### Attack Flow

```
Attacker runs site on localhost:5000
        |
        |  Fetch authenticated API
        |
Victim Browser
        |
        |  CORS passes (origin matches wildcard)
        |
Spring API
```

### Impact

* Trusted origin bypass
* Credential leakage
* Common during development → forgotten in prod

---

## ❌ Attack Scenario 3: Allowing All Headers (`*`)

### Misconfiguration

```java
config.addAllowedHeader("*");
```

### Attack Vector

```
Attacker JS sends:
Authorization: Bearer stolen-token
X-Admin: true
```

### Impact

* Header-based privilege escalation
* Token replay risks
* Weak audit control

---

## ❌ Attack Scenario 4: Missing OPTIONS Handling

### Misconfiguration

* OPTIONS requests not permitted
* CORS headers missing on preflight

### Result

```
Browser → OPTIONS /api/data
        → Blocked
```

### Impact

* Application breaks
* Developers disable CORS entirely ❌
* Leads to `*` origin misuse

---

## ❌ Attack Scenario 5: Disabling CSRF Because “We Use CORS”

### Misbelief

> “CORS prevents CSRF”

### Reality

```
Attacker submits POST form
Browser sends cookies automatically
No CSRF token validation
```

### Impact

* Silent CSRF attacks
* Data modification
* Account takeover

> 🔥 **CORS does NOT replace CSRF**

---

## 6️⃣ Secure Configuration Checklist ✅

### MVC Apps

* ❌ No CORS
* ✅ CSRF enabled
* ✅ Same-origin only

### REST + SPA (Cookies)

* ✅ Explicit origins
* ✅ CSRF tokens
* ❌ No wildcards
* ❌ No `*` headers

### REST + JWT

* ✅ CORS enabled
* ❌ CSRF disabled
* ❌ Cookies disabled

---

## 7️⃣ Golden Rules (Exam + Production)

1. **CSRF protects cookies**
2. **CORS protects browsers**
3. **JWT ≠ CSRF**
4. **Defaults are safer than custom**
5. **Never trust wildcards in production**

---


