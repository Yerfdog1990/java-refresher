
---

### **Spring Security Authorization**

---

### **2.1 Implementing Authorization**

In the previous lessons, we saw how authentication ensures that users identify themselves before accessing our application. However, authentication alone does not restrict *what* a user can do after logging in. This is where **authorization** comes in — determining *who can access what resources* based on their assigned roles or permissions.

By default, any authenticated user in our application can access resources. In this lesson, we’ll define access control rules that restrict specific actions to users with certain roles.

---

#### **Example Rule**

Only a user with the role **`MANAGER`** should be allowed to **create Projects**.

---

#### **Step 1: Define Users and Roles**

We begin by configuring users in memory using Spring’s `InMemoryUserDetailsManager` within our `WebSecurityConfig` class.
We’ll define two users — a regular user and a manager:

```java
@Bean
public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user = User.withUsername("user")
        .password(passwordEncoder.encode("password"))
        .roles("USER")
        .build();

    UserDetails manager = User.withUsername("manager")
        .password(passwordEncoder.encode("password"))
        .roles("MANAGER")
        .build();

    return new InMemoryUserDetailsManager(user, manager);
}
```

Here:

* The **user** has the role `USER`.
* The **manager** has the role `MANAGER`.
* Passwords are encoded for security purposes.

---

#### **Step 2: Define Access Rules**

Next, we specify access rules inside the `SecurityFilterChain` bean:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests((authorize) -> authorize
        // Only MANAGER can create projects
        .requestMatchers(HttpMethod.POST, "/projects").hasRole("MANAGER")
        // Other rules...
    );
    return http.build();
}
```

This configuration ensures:

* Only users with the role `MANAGER` can make `POST` requests to `/projects`.
* All other authenticated users are denied access (HTTP 403 Forbidden).

---

#### **Step 3: Testing Access**

1. **Login as a normal user:**

    * Visit `http://localhost:8080/projects`
    * Log in with credentials: `user / password`
    * On submitting the project form, a **403 Forbidden** error will appear:

      ```
      There was an unexpected error (type=Forbidden, status=403)
      ```

2. **Login as a manager:**

    * Log out (`http://localhost:8080/logout`)
    * Log in again using `manager / password`
    * You can now successfully create a project.

---

### **2.2 Spring Method-Level Security**

Spring Security also allows applying access control at the **method level** instead of just through URLs.
This provides finer-grained control, as we can secure individual service or repository methods.

---

#### **Step 1: Enable Method Security**

Add the `@EnableMethodSecurity` annotation to the configuration class:

```java
@EnableMethodSecurity
public class WebSecurityConfig {
    // ...
}
```

---

#### **Step 2: Secure Methods Using @PreAuthorize**

To enforce our access rule at the service layer, we use the `@PreAuthorize` annotation.

```java
@PreAuthorize("hasRole('MANAGER')")
public Iterable<Project> findAll() {
    return projectRepository.findAll();
}
```

Here, we use **Spring Expression Language (SpEL)** to express the rule:

* Only users with the role `MANAGER` can call the `findAll()` method.

Spring uses **AOP proxies** to intercept method calls and evaluate these expressions before execution.

---

#### **Testing Method Security**

* **As a normal user:** accessing `/projects` will result in a **403 Forbidden** error.
* **As a manager:** access is granted, and the project list is displayed.

---

### **2.3 Ant-Style Matchers vs MVC Matchers (Extra)**

Spring Security uses different matchers to interpret URL patterns.

| Matcher Type  | Implementation          | Description                                    |
| ------------- | ----------------------- | ---------------------------------------------- |
| **Ant-style** | `AntPathRequestMatcher` | Uses simple wildcard matching (`*`, `**`, `?`) |
| **MVC-style** | `MvcRequestMatcher`     | Follows Spring MVC’s path matching rules       |

**Examples of Ant-style Patterns:**

* `/proj?cts` → matches `/projects`, `/projucts`
* `/login*` → matches `/login`, `/login.html`
* `/projects/**` → matches `/projects/{id}` and subpaths

**Recommendation:** Use `mvcMatchers()` for more secure and consistent behavior in MVC applications.

**Example difference:**

```java
// Ant-style
.antMatchers("/login")

// MVC-style
.mvcMatchers("/login")
```

`mvcMatchers("/login")` also secures `/login/`, `/login.html`, or `/login.css`.

> Note: Since **Spring Boot 2.6**, the default matching strategy switched to **PathPatternParser** for better performance and predictability.

---

### **2.4 Spring Method Security Annotations (Extra)**

Spring provides multiple annotations for method-level authorization:

| Annotation       | Description                              | SpEL Support |
| ---------------- | ---------------------------------------- | ------------ |
| `@PreAuthorize`  | Checks access before method execution    | ✅            |
| `@PostAuthorize` | Checks access after method execution     | ✅            |
| `@Secured`       | Defines allowed roles                    | ❌            |
| `@RolesAllowed`  | JSR-250 standard for role-based security | ❌            |

---

**Examples:**

```java
@Secured("ROLE_MANAGER")
public Iterable<Project> findAll() { ... }

@RolesAllowed("ROLE_MANAGER")
public Iterable<Project> findAll() { ... }

@PostAuthorize("hasRole('MANAGER')")
public Iterable<Project> findAll() { ... }
```

Enable them in configuration:

```java
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig { }
```

---

### **2.5 Spring Method Security Internals (Extra)**

Spring uses **AOP (Aspect-Oriented Programming)** to implement method-level security.

* During runtime, Spring creates a proxy around the secured bean.
* When a secured method is called, the proxy intercepts the call.
* The security expression (e.g., `hasRole('MANAGER')`) is evaluated before proceeding.

If the current user doesn’t satisfy the expression, an `AccessDeniedException` is thrown.

---

### **2.6 Spring Boot 2 Notes (Extra)**

In earlier versions of Spring Security (prior to Boot 3 / Security 6):

* Method security was enabled using `@EnableGlobalMethodSecurity`:

  ```java
  @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
  ```
* The `WebSecurityConfig` class extended `WebSecurityConfigurerAdapter` and required overriding:

  ```java
  @Override
  protected void configure(HttpSecurity http) throws Exception { ... }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception { ... }
  ```
* Newer versions (Spring Security 6+) replace `antMatchers()` / `mvcMatchers()` with a unified `requestMatchers()` API.

---

### **Summary**

| Concept               | Description                                                          |
| --------------------- | -------------------------------------------------------------------- |
| **Authorization**     | Determines what authenticated users are allowed to do                |
| **Role-based Access** | Restrict actions (like creating or viewing resources) based on roles |
| **@PreAuthorize**     | Checks roles/conditions before method execution                      |
| **@PostAuthorize**    | Checks roles/conditions after execution                              |
| **AOP Proxies**       | Used internally by Spring to enforce method-level security           |
| **Matchers**          | Define URL patterns for endpoint-level access control                |

---

### **Key Takeaways**

* Use `@PreAuthorize` for fine-grained control at the service layer.
* Use `requestMatchers()` for endpoint-level authorization.
* Prefer `mvcMatchers()` for MVC applications.
* Always test access with different user roles to ensure proper configuration.

---
