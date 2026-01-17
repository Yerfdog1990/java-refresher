
---

# 📘 Tracking Logged-in Users in Spring Security 

---

## 1. Overview

Tracking logged-in users in Spring Security involves **two complementary concerns**:

1. **Retrieving details of the current authenticated user**
2. **Monitoring all active user sessions across the application**

Spring Security supports this via:

* `SecurityContextHolder`
* Controller-level injection (`@AuthenticationPrincipal`, `Principal`)
* Session-level tracking (`SessionRegistry`)
* Event listeners (login / logout / session expiration)

---

## 2. Retrieving Details of the Current Logged-in User

### 2.1 Using `SecurityContextHolder`

This works anywhere in the application (service, controller, filter).

```java
Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

String username = authentication.getName();
Object principal = authentication.getPrincipal();
```

If using `UserDetails`:

```java
UserDetails userDetails = (UserDetails) authentication.getPrincipal();
String username = userDetails.getUsername();
```

### 🔁 Flow Diagram

```
HTTP Request
     |
     v
Security Filter Chain
     |
     v
SecurityContextPersistenceFilter
     |
     v
SecurityContextHolder
     |
     v
Authentication (Principal + Authorities)
```

---

### 2.2 Using `@AuthenticationPrincipal` (Recommended for Controllers)

```java
@GetMapping("/profile")
public String profile(@AuthenticationPrincipal UserDetails user) {
    return user.getUsername();
}
```

1. [x] Clean
2. [x] Type-safe
3. [x] No casting

---

### 2.3 Using `java.security.Principal`

```java
@GetMapping("/whoami")
public String whoAmI(Principal principal) {
    return principal.getName();
}
```

* ✔ Lightweight
* ❌ Username only (no roles)

---

## 3. Monitoring All Active User Sessions

To track **who is logged in right now**, Spring Security provides:

### 🔑 Core Components

| Component                   | Purpose                            |
| --------------------------- | ---------------------------------- |
| `SessionRegistry`           | Stores users + sessions            |
| `HttpSessionEventPublisher` | Publishes session lifecycle events |
| `SessionInformation`        | Represents one session             |

---

## 4. The `SessionRegistry` API (Key Methods)

```java
List<Object> getAllPrincipals();
List<SessionInformation> getAllSessions(Object principal, boolean includeExpired);
SessionInformation getSessionInformation(String sessionId);
void registerNewSession(String sessionId, Object principal);
void removeSessionInformation(String sessionId);
```

---

## 5. Modern Spring Security Configuration (Spring Security 6)

### 5.1 Security Configuration

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
        )
        .formLogin(Customizer.withDefaults())

        .sessionManagement(session -> session
            .sessionFixation(fixation -> fixation.migrateSession())
            .maximumSessions(1)
            .sessionRegistry(sessionRegistry())
        );

    return http.build();
}
```

---

### 5.2 SessionRegistry Bean

```java
@Bean
public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
}
```

---

### 5.3 HttpSessionEventPublisher (Mandatory)

```java
@Bean
public ServletListenerRegistrationBean<HttpSessionEventPublisher>
httpSessionEventPublisher() {

    return new ServletListenerRegistrationBean<>(
            new HttpSessionEventPublisher()
    );
}
```

📌 This ensures:

* Session expiration
* Logout
* Invalidation
  are **reported to the SessionRegistry**

---

## 6. Session Tracking Flow Diagram

```
User Login
     |
     v
Authentication Success
     |
     v
New HTTP Session Created
     |
     v
SessionRegistry.registerNewSession()
     |
     v
SessionRegistry holds:
   - Principal
   - Session ID
   - Last request time
```

---

## 7. Tracking Login & Logout Events

### 7.1 Login Tracking (`AuthenticationSuccessHandler`)

```java
@Component
public class LoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final ActiveUserStore store;

    public LoginSuccessHandler(ActiveUserStore store) {
        this.store = store;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        HttpSession session = request.getSession();
        session.setAttribute(
            "user",
            new LoggedUser(authentication.getName(), store)
        );
    }
}
```

---

### 7.2 Logout Tracking (`LogoutSuccessHandler`)

```java
@Component
public class LogoutHandlerImpl
        implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("user");
        }
    }
}
```

---

## 8. Mini-Project: Tracking Logged-in Users

### 🎯 Goal

Show all **currently logged-in users** at `/loggedUsers`.

---

### 8.1 Active User Store (In-Memory)

```java
@Component
public class ActiveUserStore {

    private final List<String> users = new ArrayList<>();

    public List<String> getUsers() {
        return users;
    }
}
```

---

### 8.2 `HttpSessionBindingListener`

```java
@Component
public class LoggedUser
        implements HttpSessionBindingListener, Serializable {

    private String username;
    private ActiveUserStore store;

    public LoggedUser(String username, ActiveUserStore store) {
        this.username = username;
        this.store = store;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        if (!store.getUsers().contains(username)) {
            store.getUsers().add(username);
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        store.getUsers().remove(username);
    }
}
```

---

### 8.3 Controller

```java
@Controller
public class UserController {

    private final ActiveUserStore store;

    public UserController(ActiveUserStore store) {
        this.store = store;
    }

    @GetMapping("/loggedUsers")
    public String users(Model model) {
        model.addAttribute("users", store.getUsers());
        return "users";
    }
}
```

---

### 8.4 Thymeleaf View (`users.html`)

```html
<h2>Currently Logged-in Users</h2>
<ul>
  <li th:each="u : ${users}" th:text="${u}"></li>
</ul>
```

---

## 9. Alternative: Tracking Users via `SessionRegistry`

### 9.1 Service Layer

```java
@Service
public class ActiveUserService {

    private final SessionRegistry sessionRegistry;

    public ActiveUserService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public List<String> getActiveUsers() {

        return sessionRegistry.getAllPrincipals().stream()
            .filter(p ->
                !sessionRegistry.getAllSessions(p, false).isEmpty()
            )
            .map(Object::toString)
            .toList();
    }
}
```

---

### 9.2 Flow Diagram (SessionRegistry)

```
SessionRegistry
     |
     +-- Principal A
     |      +-- Session 1 (active)
     |
     +-- Principal B
            +-- Session 2 (expired)
```

Only **non-expired sessions** are counted as logged in.

---

## 10. Comparing Approaches

| Approach                   | Scope    | Persistence | Cluster-safe |
| -------------------------- | -------- | ----------- | ------------ |
| HttpSessionBindingListener | Session  | Memory      | ❌            |
| SessionRegistry            | Security | Memory      | ❌            |
| Spring Events              | Audit    | External DB | ✅            |

---

## 11. Best Practices

* ✅ Use **SessionRegistry** for real-time tracking
* ✅ Use **SecurityContextHolder** for current user
* ❌ Don’t use session tracking with JWT
* ❌ Avoid `sessionFixation().none()`

---

## 12. Key Takeaways

* Spring Security already tracks sessions — **you just need to expose them**
* `SessionRegistry` is the **canonical solution**
* Modern configuration uses:

    * `SecurityFilterChain`
    * Lambda DSL
    * Explicit beans

---

