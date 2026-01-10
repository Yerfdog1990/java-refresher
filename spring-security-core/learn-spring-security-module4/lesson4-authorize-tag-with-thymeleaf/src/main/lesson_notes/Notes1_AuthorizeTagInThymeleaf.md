
# **The Authorize Tag in Thymeleaf**

**Spring Boot 3 · Spring Security 6 · Thymeleaf**

The lesson is **structured**, **modern**, and **practical**, and includes a **mini project** that demonstrates how `sec:authorize` works in real applications.

---

## 1. Lesson Overview

In this lesson, you will learn:

* What the **Authorize tag** in Thymeleaf is
* How `sec:authorize` works internally
* How authorization expressions are evaluated
* How to use **role-based**, **expression-based**, and **URL-based** authorization
* How authorization differs from authentication
* How to build a **mini project** that shows and hides UI elements based on roles

This lesson uses:

* Spring Boot 3
* Spring Security 6
* Thymeleaf + Spring Security Dialect
* `SecurityFilterChain` (modern configuration)

---

## 2. What Is the Authorize Tag?

The **Authorize tag** is used to **conditionally render HTML content** based on the current user’s permissions.

In Thymeleaf, it is implemented as an **HTML attribute**, not a custom tag.

### Basic Example

```html
<div sec:authorize="hasRole('ROLE_USER')">
    Only users can see this message
</div>

<div sec:authorize="hasRole('ROLE_ADMIN')">
    Only admins can see this message
</div>
```

### Key Idea

* If the expression evaluates to `true`, the content is rendered
* If it evaluates to `false`, the content is **not included in the HTML at all**

---

## 3. How sec:authorize Works Internally

Conceptually, this:

```html
<div sec:authorize="hasRole('ROLE_USER')">
```

Is equivalent to:

```html
<div th:if="${#authorization.expression('hasRole(''ROLE_USER'')')}">
```

### Important Concepts

| Concept         | Explanation                               |
| --------------- | ----------------------------------------- |
| Expression type | Spring Expression Language (SpEL)         |
| Root object     | Spring Security authorization context     |
| Evaluation time | Server-side rendering                     |
| Purpose         | UI visibility only (not backend security) |

---

## 4. Expression Syntax vs Shortcut Syntax

### Shortcut Syntax (Recommended)

```html
<div sec:authorize="hasRole('ROLE_USER')">
```

### Full Expression Syntax

```html
<div sec:authorize="${hasRole('ROLE_USER')}">
```

Both are valid and behave **identically**.

✅ The shortcut is preferred because it is:

* Cleaner
* More readable
* Idiomatic Spring Security

---

## 5. Using the Authorization Object Directly

Thymeleaf exposes an internal object called **`#authorization`**.

### Example

```html
<div th:if="${#authorization.expression('hasRole(''ROLE_ADMIN'')')}">
    Only admins can see this message
</div>
```

### What Is `#authorization`?

* Instance of
  `org.thymeleaf.extras.springsecurity6.auth.Authorization`
* Provides APIs such as:

    * `expression(...)`
    * `url(...)`
    * `hasRole(...)`

This approach is **more verbose**, but useful in advanced scenarios.

---

## 6. URL-Based Authorization (Very Powerful)

Instead of checking roles directly, you can **delegate authorization to URL security rules**.

### Example

```html
<div sec:authorize-url="/user/delete/1">
    Only users who can access /user/delete/1 can see this
</div>
```

### Why This Is Useful

* No hardcoded roles in the view
* UI stays aligned with backend security rules
* Changes in security config automatically affect UI

---

## 7. URL Authorization with HTTP Method

You can also check authorization **per HTTP method**.

### Example

```html
<div sec:authorize-url="POST /user/delete/1">
    Only users who can POST to /user/delete/1 see this
</div>
```

### When to Use This

* RESTful applications
* Fine-grained security control
* Admin-only actions like DELETE or POST

---

## 8. Upgrade Notes (Spring Boot 3)

| Spring Boot Version | Dependency                         |
| ------------------- | ---------------------------------- |
| Boot 2              | `thymeleaf-extras-springsecurity5` |
| Boot 3              | `thymeleaf-extras-springsecurity6` |

Package name changed, but **behavior remains the same**.

---

## 9. Important Security Warning 🚨

> **Hiding UI elements does NOT secure your application**

* Users can still manually access URLs
* Backend security must always be enforced
* `sec:authorize` is for **presentation only**

---

## 10. Mini Project: Role-Based Profile Page

### Project Goal

Create a **Profile Page** that:

* Shows different messages for USER and ADMIN
* Demonstrates role-based and URL-based authorization
* Uses modern Spring Security configuration

---

## 11. Security Configuration (Spring Security 6)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/user/delete/**").hasRole("ADMIN")
                .requestMatchers("/profile").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/profile", true)
            )
            .logout(logout -> logout.logoutSuccessUrl("/login"));

        return http.build();
    }

    @Bean
    public UserDetailsService users() {

        UserDetails user = User
            .withUsername("user")
            .password("{noop}pass")
            .roles("USER")
            .build();

        UserDetails admin = User
            .withUsername("admin")
            .password("{noop}pass")
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
```

---

## 12. Controller

```java
@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        return "redirect:/profile";
    }
}
```

---

## 13. profile.html (Authorize Tag Demo)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

<head>
    <title>Profile</title>
</head>

<body>

<h1>Profile Page</h1>

<div sec:authorize="hasRole('ROLE_USER')">
    <p>Only users can see this message</p>
</div>

<div sec:authorize="hasRole('ROLE_ADMIN')">
    <p>Only admins can see this message</p>
</div>

<div sec:authorize-url="/user/delete/1">
    <p>You are authorized to delete users</p>
</div>

<div sec:authorize-url="POST /user/delete/1">
    <p>You are authorized to POST delete requests</p>
</div>

</body>
</html>
```

---

## 14. Expected Behavior

| Logged-in User | Visible Content                      |
| -------------- | ------------------------------------ |
| user / pass    | User message only                    |
| admin / pass   | Admin message + delete authorization |
| Anonymous      | Redirected to login                  |

---

## 15. Request Flow Diagram (Authorization)

📌 **Diagram Placeholder**

```
Browser
   ↓
GET /profile
   ↓
SecurityFilterChain
   ↓
Authentication Object
   ↓
Authorization Evaluation
   ↓
Thymeleaf sec:authorize
   ↓
Final HTML Rendered
```

---

## 16. Key Takeaways

* `sec:authorize` controls **UI visibility**
* Expressions are **Spring EL**
* Shortcut syntax is preferred
* URL-based authorization prevents duplication
* Backend security must always exist
* Thymeleaf replaces JSP authorize tags completely

---

## 17. What’s Next

Next lessons can cover:

* Method-level security (`@PreAuthorize`)
* Custom authorization expressions
* JWT + Thymeleaf
* SecurityFilterChain filter internals


