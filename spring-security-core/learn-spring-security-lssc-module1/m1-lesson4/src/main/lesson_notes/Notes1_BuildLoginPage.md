
---

# **Lesson Notes: Building a Custom Spring Security Login Page**

## **1. Introduction**

Spring Security provides a powerful and flexible form-based authentication system. By default, it generates its own login page, but production applications typically require a **custom login form** for branding, UX, and security.

In this lesson, we learn:

* How Spring Security handles form login internally
* How to replace the default login page with our own
* How to configure custom login URLs and processing URLs
* How to render a login page with Thymeleaf
* How to handle error scenarios

---

# **2. How Form-Based Authentication Works**

Spring Security intercepts unauthenticated requests and automatically redirects the user to the login page.

### **Authentication Flow (Unauthorized Request → Login Page Redirect)**

**Figure 1. Redirecting to the Login Page**

![img.png](img.png)

### **Explanation**

1. The user attempts to access a protected resource (e.g., `/private`).
2. `AuthorizationFilter` rejects the request because the user is not authenticated.
3. `ExceptionTranslationFilter` triggers `AuthenticationEntryPoint`, redirecting the user to `/login`.
4. The browser loads the login page.
5. Spring MVC renders the login.html view.

---

## **3. How Username/Password Authentication Works**

When the login form is submitted, the `UsernamePasswordAuthenticationFilter` handles the request.

### **Authentication Flow (Submitting Credentials)**

**Figure 2. Authenticating Username and Password**

![img_1.png](img_1.png)


### **Explanation**

1. On form submission, Spring extracts `username` and `password` from the request.
2. An `Authentication` object is created and passed to `AuthenticationManager`.
3. If authentication fails:

    * SecurityContext is cleared
    * Remember-me logic fails
    * `AuthenticationFailureHandler` runs (redirects to page with `?error`)
4. If authentication succeeds:

    * SecurityContext is populated
    * Session strategy executes
    * Success handler redirects (usually to previously saved request)

---

# **4. Maven Dependency (Spring Boot)**

If you're using Spring Boot, add this:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

This automatically brings in:

* spring-security-core
* spring-security-web
* spring-security-config

---

# **5. Creating the Security Configuration**

## **5.1. Define Users (In-Memory)**

```java
@Bean
public InMemoryUserDetailsManager userDetailsService() {
    UserDetails user1 = User.withUsername("user1")
            .password(passwordEncoder().encode("user1Pass"))
            .roles("USER")
            .build();

    UserDetails user2 = User.withUsername("user2")
            .password(passwordEncoder().encode("user2Pass"))
            .roles("USER")
            .build();

    UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder().encode("adminPass"))
            .roles("ADMIN")
            .build();

    return new InMemoryUserDetailsManager(user1, user2, admin);
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## **5.2. Authorize Requests**

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
       .authorizeRequests()
       .antMatchers("/admin/**").hasRole("ADMIN")
       .antMatchers("/login*", "/css/**").permitAll()
       .anyRequest().authenticated()
       .and()
       .formLogin();
    
    return http.build();
}
```

This version still uses the *default* login page.

---

# **6. Adding a Custom Login Page**

We disable the default login form by specifying:

* a custom login page URL: `/login`
* a custom login-processing URL: `/doLogin`

### **Updated Security Configuration**

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/login*", "/css/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/homepage", true)
                .failureUrl("/login?error=true")
                .permitAll()
        )
        .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login?logout=true")
        );

    return http.build();
}
```

### Important Settings

| Setting                                 | Meaning                                 |
| --------------------------------------- | --------------------------------------- |
| `.loginPage("/login")`                  | Where the login HTML is rendered        |
| `.loginProcessingUrl("/doLogin")`       | The POST URL Spring Security listens on |
| `.defaultSuccessUrl("/homepage", true)` | Redirect on success                     |
| `.failureUrl("/login?error=true")`      | Redirect on bad credentials             |
| `.permitAll()`                          | Allows everyone to view login page      |

---

# **7. LoginController for Spring MVC**

Spring MVC must be able to render the login page:

```java
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";  // returns login.html
    }
}
```

---

# **8. Building the Custom Login Page (Thymeleaf)**

Create:

```
src/main/resources/templates/login.html
```

### **Thymeleaf Login Page**

```html
<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">
<head>
    <title>Please Log In</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" />
</head>
<body class="container mt-5">

<h2 class="mb-4">Login</h2>

<div th:if="${param.error}" class="alert alert-danger">
    Invalid username or password.
</div>

<div th:if="${param.logout}" class="alert alert-success">
    You have been logged out.
</div>

<form th:action="@{/doLogin}" method="post" class="w-25">
    <div class="mb-3">
        <label>Username</label>
        <input type="text" name="username" class="form-control"/>
    </div>

    <div class="mb-3">
        <label>Password</label>
        <input type="password" name="password" class="form-control"/>
    </div>

    <button type="submit" class="btn btn-primary">Log In</button>
</form>

</body>
</html>
```

### Important Details

* Action must match `.loginProcessingUrl("/doLogin")`
* Must use POST
* Must name fields `username` and `password`
* Thymeleaf inserts CSRF token automatically

---

# **9. Why Use Custom URLs? (Security Reasoning)**

Default Spring Security URLs (like `/login` or the old `/j_spring_security_check`) reveal that your application uses Spring Security.

This makes the application easier to fingerprint by attackers.

**Use custom URLs in production**:

* `/doLogin`
* `/perform_logout`
* `/auth/start`

Avoid predictable, default names.

---

# **10. Handling Invalid Login Attempts**

Spring Security redirects here:

```
/login?error=true
```

In Thymeleaf:

```html
<div th:if="${param.error}">
     Invalid username and password.
</div>
```

This makes the user experience clear and user-friendly.

---

# **11. Summary**

In this lesson, you learned:

* How Spring Security redirects unauthenticated requests
* How the UsernamePasswordAuthenticationFilter processes credentials
* How to replace the default login page
* How to configure custom URLs for security reasons
* How to build a simple Thymeleaf login form
* How to handle error and logout messages

This forms the foundation of any secure web application using Spring Security.

---


