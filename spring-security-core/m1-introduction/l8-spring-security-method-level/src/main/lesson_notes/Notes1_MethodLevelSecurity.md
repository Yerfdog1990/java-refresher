
---

# **Spring Security at Method Level**

**Last Updated:** 13 Sep, 2025

Spring Security allows securing Java applications at multiple levels. Beyond securing URLs, **method-level security** provides fine-grained control by restricting access to specific methods rather than entire classes or endpoints.

---

## **Why Method-Level Security?**

1. **Granular Control:** Restrict access to individual methods.
2. **Business Logic Protection:** Service methods remain secured even if web endpoints are bypassed.
3. **Role-Based Access:** Define access rules based on roles at the method level.
4. **Separation of Concerns:** Security is applied declaratively without mixing with business logic.

---

## **Enabling Method-Level Security**

Use the `@EnableMethodSecurity` annotation (Spring 6+) instead of the deprecated `@EnableGlobalMethodSecurity`:

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    // Other security configurations
}
```

---

## **Common Method Security Annotations**

### 1. `@Secured`

Restricts access based on roles:

```java
@Service
public class ReportService {
    @Secured("ROLE_MANAGER")
    public String generateReport() {
        return "Report generated!";
    }
}
```

> Only users with `ROLE_MANAGER` can call `generateReport()`.

---

### 2. `@PreAuthorize`

Uses **Spring Expression Language (SpEL)** for more flexible conditions:

```java
@Service
public class AccountService {
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAccount(Long id) {
        return "Account " + id + " deleted!";
    }
}
```

> Only users with `ROLE_ADMIN` can execute `deleteAccount()`.

---

### 3. `@PostAuthorize`

Applies security **after method execution** (useful for filtering return values):

```java
@Service
public class AccountService {
    @PostAuthorize("returnObject.owner == authentication.name")
    public Account getAccountDetails(Long id) {
        return new Account(id, "john_doe");
    }
}
```

> Only the owner of the returned account can access it.

---

### 4. `@RolesAllowed`

Standard **JSR-250 annotation** (requires `@EnableMethodSecurity(jsr250Enabled = true)`):

```java
@Service
public class UserService {
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public String viewProfile() {
        return "Profile details shown!";
    }
}
```

> Both `ROLE_ADMIN` and `ROLE_USER` can access `viewProfile()`.

---

## **Step-by-Step Implementation with Spring Boot**

### **Step 1: Create a Spring Boot Project**

* **Project Type:** Maven
* **Spring Boot Version:** 3.3.x
* **Dependencies:** Spring Web, Spring Security

**pom.xml:**

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>
```

---

### **Step 2: Enable Method-Level Security**

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .httpBasic(); // Using HTTP Basic authentication
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var manager = new InMemoryUserDetailsManager();

        var user = User.withUsername("user")
                       .password(passwordEncoder().encode("user123"))
                       .roles("USER")
                       .build();

        var admin = User.withUsername("admin")
                        .password(passwordEncoder().encode("admin123"))
                        .roles("ADMIN")
                        .build();

        manager.createUser(user);
        manager.createUser(admin);

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### **Step 3: Create a Service with Method-Level Security**

```java
@Service
public class UserService {

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String getUserData() {
        return "This is user data accessible by USER or ADMIN.";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String getAdminData() {
        return "This is admin data accessible only by ADMIN.";
    }
}
```

---

### **Step 4: Create a Controller**

```java
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userAccess() {
        return userService.getUserData();
    }

    @GetMapping("/admin")
    public String adminAccess() {
        return userService.getAdminData();
    }
}
```

---

### **Step 5: Run and Test**

* Start the Spring Boot application.
* Access endpoints with Basic Auth.

**Test 1: `/user`**

* Username: `user`
* Password: `user123`

> Output: `"This is user data accessible by USER or ADMIN."`

**Test 2: `/admin`**

* Username: `admin`
* Password: `admin123`

> Output: `"This is admin data accessible only by ADMIN."`

---

### ✅ **Key Takeaways**

1. Method-level security allows **fine-grained access control**.
2. Use `@EnableMethodSecurity` to activate method security in Spring 6.
3. Choose annotations based on your use case: `@Secured`, `@PreAuthorize`, `@PostAuthorize`, `@RolesAllowed`.
4. Even if someone bypasses web endpoints, **service-layer methods remain secured**.
5. Works seamlessly with **in-memory users**, JDBC, or custom `UserDetailsService`.

---
