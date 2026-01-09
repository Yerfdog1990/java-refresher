
---

# Lesson Notes

## Rendering Thymeleaf vs JSP with Spring Security (Beginner Guide)

---

## 1. Big Picture: What Spring Security Actually Does

Spring Security sits **in front of your application** as a chain of servlet filters.

Every HTTP request goes through this flow:

```
Browser → Security Filters → DispatcherServlet → Controller → View → Response
```

Spring Security decides:

* Is this request allowed?
* Does the user need to log in?
* Should it redirect to `/login`?

This happens **before** your controller returns a view.

---

## 2. Why Thymeleaf and JSP Behave Differently

This is the **most important concept** to understand.

### Thymeleaf (Template Rendering)

* Thymeleaf is a **Java template engine**
* Spring reads the `.html` file
* Generates HTML
* Writes it directly to the HTTP response

➡️ **No internal forwarding**

```
Controller → Thymeleaf → HTML → Browser
```

---

### JSP (Servlet Forwarding)

* JSP is a **Servlet technology**
* Spring does **NOT** render JSP
* Instead, it **forwards** the request internally

```
Controller → FORWARD → /WEB-INF/jsp/home.jsp → Browser
```

⚠️ That **FORWARD** is critical
Spring Security treats it as a *new request*

---

## 3. Why using JSP causes `ERR_TOO_MANY_REDIRECTS`?

### What happens when accessing `/home` end point in this scenario? 

1. `/home` → allowed
2. Controller returns `"home"`
3. Spring forwards to `/WEB-INF/jsp/home.jsp`
4. Spring Security intercepts `/WEB-INF/jsp/home.jsp`
5. User is anonymous → redirect to `/login`
6. `/login` forwards to `/WEB-INF/jsp/loginPage.jsp`
7. Spring Security intercepts again
8. Redirects to `/login`
9. 🔄 Infinite loop

### Why Thymeleaf DOES NOT Have This Problem

Because Thymeleaf **does not forward**
There is no `/WEB-INF/...` request to intercept

---

## 4. Solution Concept (Not Just Code)

### Key Rule

> **JSP rendering uses internal FORWARD requests**
> Spring Security must explicitly allow them

That’s why this line is mandatory for JSP:

```java
.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
```

---

## 5. Directory Structure (Very Important)

### Thymeleaf Project Structure

```
src/main/resources/
├── templates/
│   ├── login.html
│   └── home.html
└── static/
    └── css/
```

Spring Boot auto-configures this. No extra setup.

---

### JSP Project Structure

```
src/main/webapp/
├── WEB-INF/
│   └── jsp/
│       ├── loginPage.jsp
│       └── home.jsp
└── static/
    └── css/
```

Why `WEB-INF`?

* Prevents direct browser access
* JSP must be accessed via controller

---

## 6. Spring Security Configuration

### Common Concepts (Both)

* `/login` must be public
* Everything else requires authentication
* Form login enabled

---

### ✅ Security Configuration for Thymeleaf

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/doLogin")
            .defaultSuccessUrl("/home", true)
            .permitAll()
        );

    return http.build();
}
```

✔️ Simple
✔️ No dispatcher configuration needed

---

### ✅ Security Configuration for JSP (CRITICAL DIFFERENCE)

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            // REQUIRED for JSP
            .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD)
            .permitAll()

            .requestMatchers("/login", "/css/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/doLogin")
            .defaultSuccessUrl("/home", true)
            .permitAll()
        );

    return http.build();
}
```

🧠 This tells Spring Security:

> “If this request is an internal forward, don’t block it.”

---

## 7. Controller (Same for Both)

Spring MVC does **not care** whether you use JSP or Thymeleaf.

```java
@Controller
public class HomeController {

    @GetMapping("/login")
    public String login() {
        return "loginPage";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
```

### View Name Resolution

| Technology | Resolves To                          |
| ---------- |--------------------------------------|
| Thymeleaf  | `main/resources/templates/home.html` |
| JSP        | `main/webapps/WEB-INF/jsp/home.jsp`  |

---

## 8. Login Page – Thymeleaf

📁 `src/main/resources/templates/login.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>

<form th:action="@{/doLogin}" method="post">
    <input type="text" name="username" />
    <input type="password" name="password" />
    <button type="submit">Login</button>
</form>

</body>
</html>
```

### Why Thymeleaf Is Easier

* CSRF token injected automatically
* No taglibs
* Clean HTML

---

## 9. Login Page – JSP

📁 `src/main/webapp/WEB-INF/jsp/loginPage.jsp`

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url var="loginUrl" value="/doLogin"/>

<form action="${loginUrl}" method="post">
    <sec:csrfInput />

    <input type="text" name="username" />
    <input type="password" name="password" />
    <button type="submit">Login</button>
</form>
```

⚠️ **CSRF must be added manually**
Without `<sec:csrfInput />`, login fails silently.

---

## 10. Home Page Example (Both)

### Thymeleaf Home Page

```html
<p>Welcome!</p>
<p th:text="${#authentication.name}"></p>
```

---

### JSP Home Page

```jsp
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<p>Welcome!</p>
<p><sec:authentication property="name"/></p>
```

---

## 11. Summary Table (Beginner Friendly)

| Feature        | Thymeleaf           | JSP                |
| -------------- | ------------------- | ------------------ |
| Rendering      | Template engine     | Servlet forward    |
| Directory      | resources/templates | webapp/WEB-INF/jsp |
| FORWARD config | ❌ Not needed        | ✅ Required         |
| CSRF           | Automatic           | Manual             |
| Modern usage   | Recommended         | Legacy but valid   |
| Common issues  | Few                 | Redirect loops     |

---

## 12. Mental Model to Remember

> **Thymeleaf renders → JSP forwards**

Spring Security **protects forwards by default**, and that’s why JSP needs extra configuration

---

