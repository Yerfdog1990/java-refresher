
---

# **Lesson Notes: URL Authorization**

## **1. Introduction**

Authentication answers **“Who are you?”**
Authorization answers **“What are you allowed to do?”**

In this lesson, we focus on **URL-based authorization**—controlling access to HTTP endpoints based on roles, authorities, IPs, or authentication state.

Spring Security provides a fluent DSL to create these rules, and starting from Spring Security 6+, authorization is fully powered by the new **AuthorizationManager API**.

---

# **2. Base Configuration Review (Pre–Spring Security 6)**

In older versions (Boot 2.x, Security 5.x), the standard configuration looked like this:

```java
@Override
protected void configure(HttpSecurity http) throws Exception { 
    http
      .authorizeRequests().anyRequest().authenticated()
      .and().formLogin()
      .and().httpBasic();
}
```

This tells Spring Security:

* **All requests require authentication**
* Provide **form login**
* Provide **HTTP Basic auth support**

This is the *default* behavior from the base class.

---

# **3. Adding URL Authorization Rules (Legacy)**

Suppose we want DELETE operations to be allowed **only for ADMIN users**.

```java
@Override
protected void configure(HttpSecurity http) throws Exception { 
    http
      .authorizeRequests()
        .antMatchers("/delete/**").hasRole("ADMIN")
        .anyRequest().authenticated()
      .and()
        .formLogin()
      .and()
        .httpBasic();
}
```

### ⚠️ Important Errata

**Order matters!**
Specific rules must come **before** general ones:

- ✔️ Specific → General
- ❌ General → Specific

Otherwise the general rule will “capture” the request and the specific rule never applies.

---

# **4. Authorization Methods (Legacy API)**

Spring Security offers several matchers:

### ✔ **hasRole("ADMIN")**

* Checks for the authority: `ROLE_ADMIN`
* Automatically adds the prefix **ROLE_**

### ✔ **hasAuthority("ADMIN")**

* No prefix added
* Exact match of the authority

### ✔ **hasAnyRole("ADMIN", "MANAGER")**

User must have **at least one** of:

* `ROLE_ADMIN`
* `ROLE_MANAGER`

### ✔ **hasAnyAuthority("ADMIN", "SUPER_ADMIN")**

User must have **at least one** of the listed authorities.

### ✔ **hasIpAddress("127.0.0.1")**

Restrict by IP.

### ✔ Other useful matchers

| Matcher                | Meaning                                                 |
| ---------------------- | ------------------------------------------------------- |
| `anonymous()`          | Only anonymous requests allowed (no authentication yet) |
| `authenticated()`      | Must be authenticated (any auth type)                   |
| `denyAll()`            | No one can access                                       |
| `permitAll()`          | Everyone can access                                     |
| `fullyAuthenticated()` | Not a remember-me user                                  |
| `rememberMe()`         | Only remember-me users allowed                          |
| `access("expression")` | Use SpEL expressions                                    |

---

# **5. Transition to Spring Security 6/7 (New API)**

Spring Security 6 completely removed the old `authorizeRequests()` API.

You MUST now use **authorizeHttpRequests()**, which uses the new **AuthorizationManager** under the hood.

### ✔ Updated configuration:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests((requests) -> requests
            .requestMatchers("/delete/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .formLogin(Customizer.withDefaults());

    return http.build();
}
```

### Also note:

* `antMatchers()` → ❌ deprecated
* `mvcMatchers()` → ❌ deprecated
* `requestMatchers()` → ✔ replacement

---

# **6. The AuthorizationManager Architecture (Security 6/7)**

The diagram you provided shows the hierarchy. At the center:

### **AuthorizationManager**

It replaces:

* `AccessDecisionManager`
* `AccessDecisionVoter`

Each `AuthorizationManager`:

* Reads the `Authentication` object (with `GrantedAuthority`)
* Applies authorization rules
* Returns an `AuthorizationDecision`

### Two common provided implementations:

### ✔ **AuthorityAuthorizationManager**

Checks for:

* role
* authority
* any role
* any authority

Example:

```java
.requestMatchers("/delete/**").access(AuthorityAuthorizationManager.hasRole("ADMIN"))
```

### ✔ **AuthenticatedAuthorizationManager**

Checks:

* authenticated()
* fullyAuthenticated()
* rememberMe()
* anonymous()

Example:

```java
.requestMatchers("/profile").access(AuthenticatedAuthorizationManager.fullyAuthenticated())
```

---

# **7. Creating Custom AuthorizationManager**

You can implement your own rules:

```java
@Component
public class MyCustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision authorize(Supplier<Authentication> authentication,
                                           RequestAuthorizationContext context) {

        Authentication auth = authentication.get();
        String username = auth.getName();

        boolean allowed = username.startsWith("a"); // some business rule

        return new AuthorizationDecision(allowed);
    }
}
```

Usage:

```java
.requestMatchers("/special/**").access(myCustomAuthorizationManager)
```

---

# **8. The AuthorizationManagerFactory**

Spring Security 7 introduces the **AuthorizationManagerFactory** interface:

```java
<T> AuthorizationManagerFactory<T> authorizationManagerFactory() {
    DefaultAuthorizationManagerFactory<T> f = new DefaultAuthorizationManagerFactory<>();
    f.setRolePrefix("ROLE_");
    return f;
}
```

It produces:

* `hasRole`
* `hasAuthority`
* `anonymous`
* `fullyAuthenticated`
* etc.

---

# **9. Role Hierarchy**

Example: ADMIN > STAFF > USER > GUEST

```java
@Bean
static RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("STAFF")
        .role("STAFF").implies("USER")
        .role("USER").implies("GUEST")
        .build();
}
```

Now, a user with **ADMIN** automatically gets all roles below.

---

# **10. Testing the Delete Rule**

When a non-admin user hits:

```
DELETE /delete/123
```

Spring Security rejects it:

```
HTTP 403 FORBIDDEN
```

Meaning:

* The rule is active
* Authorization is enforced

---

# **11. Summary and Takeaways**

### ✔ What you learned:

* How URL authorization works
* The difference between `hasRole` vs `hasAuthority`
* How to match multiple roles/authorities
* IP-based restrictions
* The new AuthorizationManager–based API in Spring Security 6/7
* How to write custom authorization logic
* The importance of rule ordering
* Role hierarchies for streamlined access control

### ✔ Why it matters:

URL authorization is the most common first layer of securing a web application.
Understanding it enables you to build correct, safe, and maintainable access control.

---

