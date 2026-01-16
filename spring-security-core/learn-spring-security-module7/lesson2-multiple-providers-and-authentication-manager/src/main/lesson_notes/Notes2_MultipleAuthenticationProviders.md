

# Multiple Authentication Providers

---

## 1. Overview

Spring Security is designed to **scale authentication mechanisms** as applications grow.

In real-world systems, authentication rarely comes from a single source. You may need:

* Database-backed users
* External or legacy systems
* Token-based authentication
* Custom business logic

Spring Security solves this using **multiple `AuthenticationProvider`s**, chained together through an **`AuthenticationManager`**.

This lesson demonstrates **how multiple providers work together**, how Spring **chooses which provider runs**, how to **configure them correctly**, and how to **test provider behavior using JUnit** — all using **Spring Security 6 and Spring Boot 3**.

---

## 2. Why Use Multiple Authentication Providers?

You need multiple providers when authentication comes from different sources.

| Scenario      | Provider                    |
| ------------- | --------------------------- |
| Normal users  | `DaoAuthenticationProvider` |
| Legacy users  | Custom provider             |
| API tokens    | Token provider              |
| External auth | Custom provider             |

* ✔ Spring Security **chains providers**
* ✔ Providers are tried **in order**
* ✔ The first successful provider wins

---

## 3. How Spring Security Chooses a Provider

Spring Security delegates authentication to **`ProviderManager`**, the default `AuthenticationManager` implementation.

```
AuthenticationManager (ProviderManager)
    ↓
AuthenticationProvider #1
    ↓
AuthenticationProvider #2
    ↓
AuthenticationProvider #N
```

### Provider Decision Rules

1. Provider must return `true` from `supports()`
2. First provider that successfully authenticates wins
3. If provider supports but throws an exception → chain stops
4. If provider does NOT support → next provider is tried
5. If provider returns `null` → next provider is tried

---

## 4. Provider Selection Flow Diagram

```
UsernamePasswordAuthenticationToken
          ↓
ProviderManager
   ┌─────────────────────┐
   │ CustomAuthProvider  │ supports? YES
   │ authenticate() FAIL │ → Exception → STOP
   └─────────────────────┘

   ┌─────────────────────┐
   │ DaoAuthProvider     │ NEVER REACHED
   └─────────────────────┘
```

⚠️ **Critical Rule**

If a provider **supports the token and fails**, Spring **will not fall back** to the next provider.

---

## 5. Designing Providers to Coexist Correctly

### Best-Practice Strategy

| Provider                     | Supports                              |
| ---------------------------- | ------------------------------------- |
| CustomAuthenticationProvider | Special condition or custom token     |
| DaoAuthenticationProvider    | `UsernamePasswordAuthenticationToken` |

### ❌ Bad Design

Both providers unconditionally support the same token.

---

## 6. CustomAuthenticationProvider (Selective & Safe)

To allow fallback behavior, the provider must **opt out gracefully**.

```java
@Component
public class CustomAuthenticationProvider
        implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {

        String username = auth.getName();

        if (!username.startsWith("ext_")) {
            return null; // allow fallback
        }

        return new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                List.of(new SimpleGrantedAuthority("ROLE_EXTERNAL"))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
```

### 🔑 Key Rule

Returning `null` means:

> “I don’t handle this authentication — try the next provider.”

---

## 7. DaoAuthenticationProvider Setup

```java
@Bean
public DaoAuthenticationProvider daoAuthenticationProvider(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {

    DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider();

    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);

    return provider;
}
```

---

## 8. Registering Multiple Providers (Spring Security 6)

### Modern Spring Boot 3 Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager)
            throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .authenticationManager(authenticationManager)
            .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CustomAuthenticationProvider customProvider,
            DaoAuthenticationProvider daoProvider) {

        return new ProviderManager(
                List.of(customProvider, daoProvider)
        );
    }
}
```

✔ Explicit provider order
✔ No deprecated APIs
✔ Spring Security 6 compliant

---

## 9. Authentication Flow with Multiple Providers

```
Login Request
    ↓
UsernamePasswordAuthenticationToken
    ↓
ProviderManager
    ↓
CustomAuthenticationProvider
    ├─ supports? YES
    ├─ authenticate()? returns null
    ↓
DaoAuthenticationProvider
    ├─ supports? YES
    ├─ authenticate()? SUCCESS
    ↓
SecurityContextHolder
```

---

## 10. Mini Project Structure

```
src/main/java
 └── com.example.security
     ├── config
     │   └── SecurityConfig.java
     ├── auth
     │   ├── CustomAuthenticationProvider.java
     │   └── ExternalAuthService.java
     ├── user
     │   ├── UserEntity.java
     │   ├── UserRepository.java
     │   └── CustomUserDetailsService.java
     └── controller
         └── DemoController.java
```

---

## 11. Controller for Testing Authentication

```java
@RestController
public class DemoController {

    @GetMapping("/me")
    public Authentication me(Authentication auth) {
        return auth;
    }
}
```

---

## 12. Testing Provider Order with JUnit

### External User → Custom Provider

```java
@SpringBootTest
class ProviderOrderTest {

    @Autowired
    AuthenticationManager authManager;

    @Test
    void externalUser_usesCustomProvider() {

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "ext_john", "pass");

        Authentication result =
                authManager.authenticate(auth);

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_EXTERNAL")));
    }
}
```

---

## 13. Database Authentication Tests

```java
@SpringBootTest
public class ProviderOrderTest {

    @Autowired
    AuthenticationManager authManager;

    @Test
    void givenDatabase_whenAuthenticatePersistedUser_thenSuccess() {

        Authentication auth =
            new UsernamePasswordAuthenticationToken(
                "bob@example.com", "bob123");

        Authentication result =
            authManager.authenticate(auth);

        boolean roleUser =
            result.getAuthorities().stream()
                .anyMatch(a ->
                    Objects.equals(
                        a.getAuthority(),
                        "ROLE_ADMIN"));

        assertTrue(roleUser);
    }

    @Test
    void givenDatabase_whenAuthenticateNonPersistedUser_thenFailure() {

        Authentication auth =
            new UsernamePasswordAuthenticationToken(
                "xyz@example.com", "xyz123");

        AuthenticationException exception =
            assertThrows(AuthenticationException.class,
                () -> authManager.authenticate(auth));

        assertInstanceOf(
            BadCredentialsException.class,
            exception);

        assertTrue(
            exception.getMessage()
                .contains("Bad credentials"));
    }
}
```

---

## 14. Common Mistakes & Fixes 🚨

### ❌ Both Providers Throw Exceptions

✔ Return `null` instead
✔ Allow fallback

### ❌ Provider Order Ignored

✔ ProviderManager order **matters**

### ❌ supports() Too Broad

✔ Narrow logic
✔ Or use a custom token

---

## 15. Using a Custom Authentication Token (Best Practice)

For complex systems, define a **dedicated token**:

```java
public class ExternalAuthToken
        extends AbstractAuthenticationToken {
    // custom fields
}
```

```java
@Override
public boolean supports(Class<?> auth) {
    return ExternalAuthToken.class.isAssignableFrom(auth);
}
```

✔ Prevents collisions
✔ Cleaner provider separation

---

## 16. Advanced: Explicit AuthenticationManager Control

### Why Use ProviderManager Directly?

* Explicit provider ordering
* Credential erasure control
* Parent AuthenticationManager fallback

---

### Disabling Credential Erasure (Use Carefully)

```java
providerManager.setEraseCredentialsAfterAuthentication(false);
```

⚠️ Only disable when absolutely required.

---

## 17. Authentication Hierarchy (Advanced)

```
Child ProviderManager
   ├─ Provider 1
   ├─ Provider 2
   ↓
Parent ProviderManager
   ├─ Provider A
```

✔ Rare but powerful
✔ True fallback across managers

---

## 18. Final Summary

✔ Spring Security **natively supports multiple providers**
✔ Providers are **chained, not replaced**
✔ `supports()` + `authenticate()` control flow
✔ Provider order matters
✔ Return `null` to allow fallback
✔ Use custom tokens for complex systems
✔ JUnit tests validate provider behavior

---

## Final Conclusion

Spring Security allows authentication to **grow gradually**:

1️⃣ Single provider
2️⃣ Multiple providers
3️⃣ Explicit AuthenticationManager
4️⃣ Parent fallback manager

This layered design gives you **maximum flexibility with strong security guarantees**, provided you understand **provider order, supports logic, and exception behavior**.

---

