
---

# Custom Authentication Providers in Spring Boot (Spring Security 6)

---

## Part 1: Why Custom Authentication Providers Exist

Spring Security provides **out-of-the-box authentication mechanisms**, such as:

* Form login with username/password
* JDBC-backed authentication
* LDAP authentication
* OAuth2 / OpenID Connect

However, **real-world applications** often require **custom authentication logic**, for example:

* Authenticating against a **legacy database**
* Integrating with an **external identity service**
* Adding **extra credential checks** (OTP, employee code, tenant ID)
* Supporting **multiple authentication methods** in one application
* Replacing or extending `DaoAuthenticationProvider`

This is where **Custom Authentication Providers** come in.

> **Key idea**:
> Spring Security separates *how authentication works* from *how the application works*.
> Custom providers let you plug in **your own authentication logic** without breaking the framework.

---

## Part 2: Core Authentication Architecture in Spring Security

Spring Security authentication is built around **four core concepts**:

```
┌────────────┐
│   Filter   │  (UsernamePasswordAuthenticationFilter)
└─────┬──────┘
      │ Authentication
      ▼
┌────────────────────-┐
│AuthenticationManager│  (ProviderManager)
└─────────┬───────────┘
          │ delegates to
          ▼
┌────────────────────---┐
│AuthenticationProvider │
└─────────┬─────────--──┘
          │ uses
          ▼
┌────────────────────┐
│ UserDetailsService │
└────────────────────┘
```

### Responsibilities

| Component              | Responsibility                         |
| ---------------------- | -------------------------------------- |
| Filter                 | Extracts credentials from HTTP request |
| AuthenticationManager  | Delegates authentication               |
| AuthenticationProvider | Performs credential validation         |
| UserDetailsService     | Loads user data                        |
| PasswordEncoder        | Verifies password                      |

---

## Part 3: Authentication Flow (Step-by-Step)

### High-Level Authentication Flow

```
(1) User submits username + password
        ↓
(2) Security Filter intercepts request
        ↓
(3) UsernamePasswordAuthenticationToken created (unauthenticated)
        ↓
(4) AuthenticationManager (ProviderManager)
        ↓
(5) AuthenticationProvider supports token?
        ↓
(6) UserDetailsService loads user
        ↓
(7) PasswordEncoder verifies password
        ↓
(8) Authenticated Authentication returned
        ↓
(9) Stored in SecurityContextHolder
```

---

## Part 4: DaoAuthenticationProvider (Default Provider)

Spring Security’s **default provider** is `DaoAuthenticationProvider`.

### What DaoAuthenticationProvider Does

1. Calls `UserDetailsService.loadUserByUsername()`
2. Uses `PasswordEncoder.matches()`
3. Builds an authenticated `UsernamePasswordAuthenticationToken`
4. Stores it in `SecurityContext`

---

### Figure 1. DaoAuthenticationProvider Usage**

![img.png](img.png)

---

### Figure Explanation 

1. The authentication Filter from the Reading the Username & Password section passes a UsernamePasswordAuthenticationToken to the AuthenticationManager, which is implemented by ProviderManager.
2. The ProviderManager is configured to use an AuthenticationProvider of type DaoAuthenticationProvider.
3. DaoAuthenticationProvider looks up the UserDetails from the UserDetailsService.
4. DaoAuthenticationProvider uses the PasswordEncoder to validate the password on the UserDetails returned in the previous step.
5. When authentication is successful, the Authentication that is returned is of type UsernamePasswordAuthenticationToken and has a principal that is the UserDetails returned by the configured UserDetailsService and a set of authorities containing at least FACTOR_PASSWORD. Ultimately, the returned UsernamePasswordAuthenticationToken is set on the SecurityContextHolder by the authentication Filter.

---

## Part 5: Why Create a Custom AuthenticationProvider?

You create a custom provider when:

* Username/password is **not enough**
* Authentication depends on **extra logic**
* You want to **replace DaoAuthenticationProvider**
* You want **multiple providers** in one app

---

## Part 6: Understanding AuthenticationProvider Interface

```java
public interface AuthenticationProvider {

    Authentication authenticate(Authentication authentication)
            throws AuthenticationException;

    boolean supports(Class<?> authentication);
}
```

### Method Responsibilities

| Method       | Purpose                        |
| ------------ | ------------------------------ |
| authenticate | Performs authentication        |
| supports     | Declares supported token types |

---

## Part 7: Implementing a Custom AuthenticationProvider

### CustomAuthenticationProvider

```java
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
```

### What’s Happening Here?

* Extracts credentials from `Authentication`
* Loads user data
* Verifies password
* Returns **authenticated token**

---

## Part 8: Role of UserDetailsService

### CustomUserDetailsService

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}
```

### Key Point

> **AuthenticationProvider does NOT fetch users directly**
> It **delegates user lookup** to `UserDetailsService`

---

## Part 9: Registering the Custom Provider (Spring Security 6)

### Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider customProvider;

    public SecurityConfig(CustomAuthenticationProvider customProvider) {
        this.customProvider = customProvider;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(customProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Part 10: Multiple Authentication Providers

Spring Security supports **multiple providers**:

```java
@Bean
AuthenticationManager authenticationManager(
        CustomAuthenticationProvider custom,
        DaoAuthenticationProvider dao) {

    return new ProviderManager(List.of(custom, dao));
}
```

### Provider Selection Flow

```
AuthenticationManager
   ├── CustomAuthenticationProvider (supports?)
   └── DaoAuthenticationProvider (fallback)
```

---

## Part 11: Authentication vs Authorization (Quick Reminder)

| Aspect  | Authentication         | Authorization    |
| ------- | ---------------------- | ---------------- |
| Purpose | Who are you?           | What can you do? |
| Happens | First                  | After            |
| Classes | AuthenticationProvider | @PreAuthorize    |
| Output  | Authentication         | Access decision  |

---

# Part 12: Mini Spring Boot Lab Project (Hands-On)

## 🎯 Lab Objective

Build a **CRUD application** that uses a **Custom AuthenticationProvider** instead of the default one.

---

## Project Structure

```
src/main/java
 └── com.example.demo
     ├── config
     │   └── SecurityConfig.java
     ├── security
     │   ├── CustomAuthenticationProvider.java
     │   └── CustomUserDetailsService.java
     ├── user
     │   ├── User.java
     │   ├── UserRepository.java
     │   └── UserController.java
     └── DemoApplication.java
```

---

## Entity

```java
@Entity
public class User {

    @Id @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String role;

    // getters/setters
}
```

---

## Repository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

---

## Controller (CRUD)

```java
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> findAll() {
        return repo.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return repo.save(user);
    }
}
```

---

## Test Flow

1. Insert user with BCrypt password
2. Access `/login`
3. Credentials verified by **CustomAuthenticationProvider**
4. CRUD endpoints unlocked

---

## Part 13: Diagram – Custom Authentication Flow

```
HTTP Login Request
        ↓
UsernamePasswordAuthenticationFilter
        ↓
AuthenticationManager
        ↓
CustomAuthenticationProvider
        ↓
CustomUserDetailsService
        ↓
PasswordEncoder
        ↓
Authenticated Token
        ↓
SecurityContextHolder
```

---

## Part 14: Key Takeaways

* `AuthenticationProvider` controls **how authentication happens**
* `UserDetailsService` controls **where user data comes from**
* Custom providers are **plug-and-play**
* Spring Security 6 uses **SecurityFilterChain**, not `WebSecurityConfigurerAdapter`
* Multiple providers can coexist cleanly

---

Below is a **clear, production-grade lesson section** on **JUnit testing Custom AuthenticationProviders in Spring Security 6**, written to **fit seamlessly into your existing notes**.
It includes **unit tests**, **integration tests**, **failure scenarios**, **diagram flows**, and **common pitfalls**.

---

# JUnit Testing Custom Authentication Providers (Spring Security 6)

---

## Part 15: Why Test Authentication Providers?

Authentication providers are **security-critical components**. A bug here can:

* Allow unauthorized access
* Lock out valid users
* Break login flows silently

Testing ensures:

✅ Correct credential validation
✅ Proper exception handling
✅ Correct Authentication object creation
✅ Provider registration works

---

## Part 16: What Should Be Tested?

### Unit Test Scope

| Test Type        | What It Verifies          |
| ---------------- | ------------------------- |
| Unit Test        | Provider logic only       |
| Integration Test | Full Spring Security flow |
| Negative Test    | Bad credentials           |
| Support Test     | `supports()` method       |

---

## Part 17: Unit Testing `CustomAuthenticationProvider`

### Test Strategy

* Mock `UserDetailsService`
* Mock `PasswordEncoder`
* Test `authenticate()` directly
* No web context needed

---

## 17.1 Test Dependencies (Maven)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 17.2 Unit Test Class

```java
@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomAuthenticationProvider provider;
```

---

## 17.3 Successful Authentication Test

```java
@Test
void authenticate_successful() {

    // Arrange
    UserDetails user = User.withUsername("john")
            .password("encodedPass")
            .authorities("ROLE_USER")
            .build();

    when(userDetailsService.loadUserByUsername("john"))
            .thenReturn(user);

    when(passwordEncoder.matches("rawPass", "encodedPass"))
            .thenReturn(true);

    Authentication auth =
            new UsernamePasswordAuthenticationToken("john", "rawPass");

    // Act
    Authentication result = provider.authenticate(auth);

    // Assert
    assertTrue(result.isAuthenticated());
    assertEquals("john",
            ((UserDetails) result.getPrincipal()).getUsername());
}
```

---

## 17.4 Invalid Password Test

```java
@Test
void authenticate_invalidPassword() {

    UserDetails user = User.withUsername("john")
            .password("encodedPass")
            .authorities("ROLE_USER")
            .build();

    when(userDetailsService.loadUserByUsername("john"))
            .thenReturn(user);

    when(passwordEncoder.matches("wrong", "encodedPass"))
            .thenReturn(false);

    Authentication auth =
            new UsernamePasswordAuthenticationToken("john", "wrong");

    assertThrows(BadCredentialsException.class,
            () -> provider.authenticate(auth));
}
```

---

## 17.5 User Not Found Test

```java
@Test
void authenticate_userNotFound() {

    when(userDetailsService.loadUserByUsername("unknown"))
            .thenThrow(new UsernameNotFoundException("Not found"));

    Authentication auth =
            new UsernamePasswordAuthenticationToken("unknown", "pass");

    assertThrows(UsernameNotFoundException.class,
            () -> provider.authenticate(auth));
}
```

---

## 17.6 Supports Method Test

```java
@Test
void supports_usernamePasswordToken() {
    assertTrue(provider.supports(
            UsernamePasswordAuthenticationToken.class));
}

@Test
void supports_otherToken() {
    assertFalse(provider.supports(
            RememberMeAuthenticationToken.class));
}
```

---

## Part 18: Provider Unit Test Flow Diagram

```
Test Method
   ↓
provider.authenticate()
   ↓
Mock UserDetailsService
   ↓
Mock PasswordEncoder
   ↓
Authentication Result / Exception
```

---

## Part 19: Integration Testing (Full Spring Context)

Unit tests verify **logic**.
Integration tests verify **wiring**.

---

## 19.1 Integration Test Setup

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {

    @Autowired
    MockMvc mockMvc;
}
```

---

## 19.2 Successful Login Test

```java
@Test
void login_success() throws Exception {

    mockMvc.perform(post("/login")
            .param("username", "john")
            .param("password", "password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
}
```

---

## 19.3 Failed Login Test

```java
@Test
void login_failure() throws Exception {

    mockMvc.perform(post("/login")
            .param("username", "john")
            .param("password", "wrong"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?error"));
}
```

---

## Part 20: Testing Authorization After Authentication

```java
@Test
@WithMockUser(username = "admin", roles = "ADMIN")
void adminEndpoint_accessGranted() throws Exception {

    mockMvc.perform(get("/admin"))
            .andExpect(status().isOk());
}
```

---

## Part 21: Common Testing Pitfalls 🚨

### ❌ Mistake 1: Testing PasswordEncoder Logic

> Password encoding is already tested by Spring.

✔️ **Mock it instead**

---

### ❌ Mistake 2: Testing Filters in Unit Tests

✔️ Use **integration tests** for filters
✔️ Use **unit tests** for providers

---

### ❌ Mistake 3: Forgetting `supports()`

If `supports()` returns false:

```
AuthenticationProvider is skipped entirely
```

---

## Part 22: Security Test Matrix

| Scenario            | Test Type          |
| ------------------- | ------------------ |
| Correct credentials | Unit + Integration |
| Wrong password      | Unit               |
| User missing        | Unit               |
| Provider selection  | Unit               |
| Endpoint access     | Integration        |
| Role enforcement    | Integration        |

---

## Part 23: Debugging Failed Provider Tests

Enable logs:

```properties
logging.level.org.springframework.security=DEBUG
```

Look for:

* `ProviderManager` selection
* `supports()` evaluation
* Authentication exception types

---

## Part 24: Summary

✔ Unit tests validate **authentication logic**
✔ Integration tests validate **Spring wiring**
✔ Mock dependencies aggressively
✔ Always test negative paths
✔ Authentication bugs = security bugs

---


