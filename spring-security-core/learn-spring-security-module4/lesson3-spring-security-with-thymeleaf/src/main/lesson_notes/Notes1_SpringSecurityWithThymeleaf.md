
---

# Thymeleaf + Spring Security Integration Basics

**Spring Boot 3 · Spring Framework 6 · Spring Security 6**

---

## 1. Overview

Modern Spring Boot applications use **Thymeleaf** as the preferred server-side template engine and **Spring Security** for authentication and authorization.

Older tutorials often rely on:

* JSP views
* `WebSecurityConfigurerAdapter`
* JSP security tag libraries

These approaches are **deprecated or removed** in Spring 6.

This lesson explains how to integrate **Thymeleaf with Spring Security using modern configuration**, replacing JSP entirely and using:

* `SecurityFilterChain`
* Thymeleaf Security Dialect
* Attribute-based authorization (`sec:authorize`)

---

## 2. Prerequisites

You should already understand:

* Spring Boot basics
* Spring MVC controllers
* Thymeleaf templates
* Core Spring Security concepts (roles, authentication)

---

## 3. Required Dependencies (Spring Boot 3)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Thymeleaf Spring Security Dialect -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

---

## 4. Modern Spring Security Configuration (Spring 6)

### Security Configuration Using `SecurityFilterChain`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**").permitAll()
                .requestMatchers("/profile").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/profile", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
            );

        return http.build();
    }
}
```

---

## 5. Login Controller (Modern Mapping)

```java
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }
}
```

---

## 6. Thymeleaf Login Template

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Login</title>
</head>
<body>

<h1>Login</h1>

<p th:if="${loginError}" style="color:red">
    Invalid username or password
</p>

<form th:action="@{/login}" method="post">
    <label>Username:</label>
    <input type="text" name="username" autofocus />

    <br/>

    <label>Password:</label>
    <input type="password" name="password" />

    <br/>

    <button type="submit">Login</button>
</form>

</body>
</html>
```

---

## 7. Request Flow Diagram (Authentication)

**[Diagram Placeholder – Paste Diagram Here]**

```
Browser
   ↓
/login (GET)
   ↓
Login Page (Thymeleaf)
   ↓
POST /login
   ↓
SecurityFilterChain
   ↓
UsernamePasswordAuthenticationFilter
   ↓
AuthenticationManager
   ↓
Success → /profile
Failure → /login?error=true
```

---

## 8. Global Error Handling with Thymeleaf

### Exception Handler

```java
@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Throwable ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}
```

---

### Error Template (`error.html`)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Error</title>
</head>
<body
  th:with="status=${T(org.springframework.http.HttpStatus).valueOf(#response.status)}">

<h1 th:text="|${status.value()} - ${status.reasonPhrase}|">500</h1>

<p th:text="${errorMessage}">
    Unexpected error
</p>

<a th:href="@{/}">Home</a>

</body>
</html>
```

---

## 9. Spring Security Dialect for Thymeleaf

### Required Namespace

```html
xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
```

This dialect **replaces JSP security tag libraries** in Spring MVC.

---

## 10. Role-Based Authorization in Thymeleaf

### Conditional Rendering

```html
<div sec:authorize="isAuthenticated()">
    Visible only to authenticated users
</div>

<div sec:authorize="hasRole('ROLE_ADMIN')">
    Admin-only content
</div>

<div sec:authorize="hasRole('ROLE_USER')">
    User-only content
</div>
```

---

## 11. Display Logged-in User Details

```html
<p>
    Username:
    <span sec:authentication="name">Guest</span>
</p>

<p>
    Roles:
    <span sec:authentication="authorities">
        [ROLE_USER]
    </span>
</p>
```

### Why This Is Powerful

* Uses **standard HTML**
* Attributes add security behavior
* Templates work **without backend**
* Front-end developers can edit offline

---

## 12. JSP vs Thymeleaf (Authorization Comparison)

| Feature               | JSP         | Thymeleaf       |
| --------------------- | ----------- | --------------- |
| Security syntax       | Custom tags | HTML attributes |
| Front-end friendly    | ❌           | ✅               |
| Offline rendering     | ❌           | ✅               |
| Spring Boot 3 support | ❌           | ✅               |
| Recommended           | ❌           | ✅               |

**JSP**

```jsp
<sec:authorize access="hasRole('ROLE_ADMIN')">
   Admin Content
</sec:authorize>
```

**Thymeleaf**

```html
<div sec:authorize="hasRole('ROLE_ADMIN')">
   Admin Content
</div>
```

---

## 13. Mini Project: Secure Profile Page

### Objective

Demonstrate:

* Authentication
* Role-based UI rendering
* Thymeleaf + Spring Security integration

---

### Controller

```java
@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}
```

---

### Profile Template (`profile.html`)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

<head>
    <title>Profile</title>
</head>

<body>

<h1>User Profile</h1>

<p>
    Logged in as:
    <span sec:authentication="name">Anonymous</span>
</p>

<p>
    Roles:
    <span sec:authentication="authorities">[ROLE_USER]</span>
</p>

<div sec:authorize="hasRole('ROLE_USER')">
    <p>Welcome, User!</p>
</div>

<div sec:authorize="hasRole('ROLE_ADMIN')">
    <p>Welcome, Admin!</p>
</div>

</body>
</html>
```

---

## 14. Key Takeaways

* `WebSecurityConfigurerAdapter` is **removed**
* `SecurityFilterChain` is the **modern standard**
* Thymeleaf replaces JSP completely
* Security logic lives in **attributes, not tags**
* Spring Boot + Thymeleaf + Spring Security is the **recommended stack**

---

## 15. What’s Next

Next lessons can cover:

* SecurityFilterChain filters explained one by one
* Custom authorization expressions
* Method-level security (`@PreAuthorize`)
* JWT-based security with Thymeleaf UI

---

