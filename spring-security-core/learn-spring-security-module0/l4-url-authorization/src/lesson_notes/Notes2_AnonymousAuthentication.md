
---

# **📘 Lesson Notes: `permitAll()` vs `anonymous()` in Spring Security**

Spring Security provides powerful access control features, including support for **public URLs**, **anonymous users**, and **role-based authorization**. Two commonly misunderstood mechanisms are:

* `permitAll()`
* `anonymous()`

Although they appear similar, they serve **different purposes**, especially when building applications that must differentiate between *unauthenticated*, *anonymous*, and *fully authenticated* users.

---

# **1. Introduction: Why Anonymous Authentication Exists**

In secure applications, a good practice is **deny by default**:

* Everything requires authentication **unless explicitly allowed**.
* Only a few URLs (home, login, static assets) are public.

However, Spring Security wants your application to behave consistently. Therefore, instead of returning `null` for unauthenticated users, Spring Security inserts an **AnonymousAuthenticationToken** into the `SecurityContextHolder`.

This ensures:

✔ Your system ALWAYS has an Authentication object
✔ Your logs/audit trails never encounter `null`
✔ You can write controllers/services without null checks
✔ Anonymous users behave consistently across filters

**Important:**

> An “anonymous user” is still an unauthenticated user.
> Spring Security simply represents them with a token.

---

# **2. How Anonymous Authentication Works (Under the Hood)**

Spring Security internally uses three components:

### **2.1. `AnonymousAuthenticationFilter`**

* Runs **after all authentication filters**
* If no Authentication exists → inserts an `AnonymousAuthenticationToken`
* Assigns the user a principal like `"anonymousUser"`
* Assigns authority `"ROLE_ANONYMOUS"`

### **2.2. `AnonymousAuthenticationProvider`**

* Authenticates the anonymous token
* Ensures the token has a valid internal key

### **2.3. `AnonymousAuthenticationToken`**

* Represents the "anonymous user"
* Stores authorities such as:

```
anonymousUser, ROLE_ANONYMOUS
```

### Legacy XML Example

(You do NOT need this in Boot unless customizing)

```xml
<bean id="anonymousAuthFilter"
      class="org.springframework.security.web.authentication.AnonymousAuthenticationFilter">
    <property name="key" value="foobar"/>
    <property name="userAttribute" value="anonymousUser,ROLE_ANONYMOUS"/>
</bean>

<bean id="anonymousAuthenticationProvider"
      class="org.springframework.security.authentication.AnonymousAuthenticationProvider">
    <property name="key" value="foobar"/>
</bean>
```

This shows the shared key used for internal bookkeeping.

---

# **3. `permitAll()` — What It Really Does**

`permitAll()` is used to allow ANY user (authenticated or anonymous) to access a URL **without requiring authentication**.

### **When to use `permitAll()`**

✔ Public pages (home, login, signup)
✔ Static resources (CSS, JS, images)
✔ Documentation (Swagger, API docs)
✔ Health checks

### Example (Classic Spring Boot 2 / pre–Security 6)**

```java
.antMatchers("/", "/home", "/css/**", "/js/**").permitAll()
```

### What it means:

* Spring Security **does not check roles or authentication**.
* It simply allows the request.

### **Modern Spring Security 6+ version (using requestMatchers):**

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/", "/home", "/css/**", "/js/**").permitAll()
    .anyRequest().authenticated()
)
```

---

# **4. `anonymous()` — What It Really Does**

`anonymous()` **enables anonymous authentication**, meaning:

* Every unauthenticated request receives an `AnonymousAuthenticationToken`.
* Your system can still identify the user (as anonymous) for auditing/logging.

### **anonymous() does *not* open any URLs.**

It simply **enables anonymous identities** for unauthenticated users.

To actually allow anonymous users to access a URL, you STILL use `permitAll()`.

### Example:

```java
http
    .authorizeHttpRequests()
        .requestMatchers("/public/**").permitAll()
        .anyRequest().authenticated()
        .and()
    .anonymous();
```

### Result:

* `/public/**` → accessible by everyone
* other URLs → require authentication
* anonymous users get a fake principal & `"ROLE_ANONYMOUS"`

---

# **5. Side-by-Side Comparison**

| Feature                                     | `permitAll()` | `anonymous()`   |
| ------------------------------------------- | ------------- | --------------- |
| Opens URL to all users                      | ✔ Yes         | ❌ No            |
| Allows logged users                         | ✔ Yes         | ✔ Yes           |
| Allows anonymous users                      | ✔ Yes         | ✔ Yes           |
| Inserts AnonymousAuthenticationToken        | ❌ No          | ✔ Yes           |
| Used for authorization                      | ✔ Yes         | ❌ No            |
| Used for identity consistency               | ❌ No          | ✔ Yes           |
| Exposes principal for unauthenticated users | ❌ null        | ✔ anonymousUser |

**Summary:**

> `permitAll()` → controls *access*
> `anonymous()` → controls *identity for unauthenticated users*

---

# **6. Practical Example with Both**

### **SecurityConfig (Spring Security 6, recommended modern version)**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .anonymous(); // enable anonymous authentication

        return http.build();
    }
}
```

### Behavior:

* Home, login, CSS/JS → public
* `/public/**` → public
* `/secure` → requires login
* Anonymous users still get a token `"anonymousUser"` with `"ROLE_ANONYMOUS"`

---

# **7. Example Controller**

```java
@Controller
public class SampleController {

    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/css/styles.css")
    public String cssStyles() {
        return "styles";
    }

    @GetMapping("/js/scripts.js")
    public String jsScripts() {
        return "scripts";
    }

    @GetMapping("/secure")
    public String securePage() {
        return "secure";
    }
}
```

### Mapping:

| URL       | Access             |
| --------- | ------------------ |
| `/`       | permitAll          |
| `/login`  | permitAll          |
| `/css/**` | permitAll          |
| `/js/**`  | permitAll          |
| `/secure` | authenticated only |

---

# **8. When Should You Use Which?**

### **Use `permitAll()` when:**

* You want to open a URL to everyone
* You want to bypass authentication for a specific endpoint

### **Use `anonymous()` when:**

* You want the system to always have an Authentication object
* You want logs to show `"anonymousUser"`
* Your service or interceptor expects non-null `SecurityContextHolder.getContext().getAuthentication()`

---

# **9. Key Takeaways**

### ✔ permitAll()

* Allows unrestricted access
* Bypasses security checks
* Does **not** add an anonymous principal

### ✔ anonymous()

* Enables a placeholder authentication for unauthenticated users
* Does **not** open any URLs
* Ensures consistent identity: `"anonymousUser"`

### ✔ Most apps use **both**

* `permitAll()` for public endpoints
* `anonymous()` for internal consistency

---

# **10. Additional Considerations**

### Security Advice:

* Apply `permitAll()` sparingly to avoid exposing sensitive URLs
* Keep anonymous authentication enabled unless you have a specific reason to disable it
* Never rely on `"ROLE_ANONYMOUS"` for sensitive logic
* Review logs regularly—anonymous users should not be performing authenticated operations

---
