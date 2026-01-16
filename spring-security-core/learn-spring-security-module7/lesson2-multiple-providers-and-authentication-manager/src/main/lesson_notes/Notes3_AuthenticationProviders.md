# Authentication Providers

## Part 1: Introduction to Authentication in Spring Security

### 1.1 What is Authentication?

Authentication in Spring Security is the process of verifying the identity of a user (or client) attempting to access a protected resource. In simple terms, it answers the question:

> **“Who are you?”**

During authentication, Spring Security:

1. Collects credentials (username/password, token, certificate, etc.)
2. Validates those credentials using one or more **AuthenticationProviders**
3. Creates an authenticated `Authentication` object
4. Stores it in the **SecurityContext**

---

### 1.2 Authentication vs Authorization

| Aspect         | Authentication          | Authorization         |
| -------------- | ----------------------- | --------------------- |
| Purpose        | Verify identity         | Decide access         |
| Question       | Who are you?            | What can you do?      |
| Core Interface | AuthenticationProvider  | AccessDecisionManager |
| Result         | Authenticated principal | Permit / Deny         |

---

## Part 2: Authentication Architecture in Spring Security 7

### 2.1 High-Level Authentication Flow

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ HTTP Request
     ▼
┌────────────────────────┐
│ Security Filter Chain  │
│ (UsernamePasswordFilter│
│  JwtFilter, etc.)      │
└────┬───────────────────┘
     │ Authentication Token
     ▼
┌────────────────────────┐
│ AuthenticationManager  │
│  (ProviderManager)     │
└────┬───────────────────┘
     │ delegates
     ▼
┌─────────────────────────────┐
│ AuthenticationProvider(s)   │
│  - DaoAuthenticationProvider│
│  - CustomAuthenticationProv │
│  - JwtAuthenticationProvider│
└────┬────────────────────────┘
     │ success
     ▼
┌────────────────────────┐
│ SecurityContextHolder  │
└────────────────────────┘
```

---

### 2.2 Key Core Interfaces

#### Authentication

Represents the authentication request and result.

Important properties:

* `principal` – user identity
* `credentials` – password/token
* `authorities` – roles & permissions
* `authenticated` – true/false

#### AuthenticationManager

Responsible for coordinating authentication.

In practice, Spring uses:

```
ProviderManager implements AuthenticationManager
```

#### AuthenticationProvider (Most Important)

Responsible for *actual credential verification*.

---

## Part 3: AuthenticationProvider Interface (Deep Dive)

```java
public interface AuthenticationProvider {
    Authentication authenticate(Authentication authentication)
            throws AuthenticationException;

    boolean supports(Class<?> authentication);
}
```

### 3.1 authenticate(Authentication authentication)

Responsibilities:

* Validate credentials
* Perform additional checks (locked account, expired password)
* Load user authorities
* Return **fully authenticated Authentication**

Possible outcomes:

* ✔ Success → return authenticated token
* ❌ Failure → throw AuthenticationException

---

### 3.2 supports(Class<?> authentication)

Controls **which Authentication tokens** the provider can handle.

Example:

```java
@Override
public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class
            .isAssignableFrom(authentication);
}
```

---

## Part 4: Multiple Authentication Providers (ProviderManager)

### 4.1 Why Multiple Providers?

Real-world systems often need:

* Database login
* API token authentication
* LDAP login
* Social login

Spring Security supports this via **Provider chaining**.

### 4.2 Provider Selection Flow

```
ProviderManager
   |
   |-- Provider 1 (supports?) → ❌ skip
   |-- Provider 2 (supports?) → ✔ authenticate
   |-- Provider 3 → not called
```

Rules:

* Providers are tried **in order**
* First provider that supports the token wins
* If all fail → authentication error

---

## Part 5: DaoAuthenticationProvider (Database Authentication)

### 5.1 How it works

* DaoAuthenticationProvider is an authentication provider in Spring Security that is used to authenticate users stored in a database. It implements the AuthenticationProvider interface and can be used with any database that provides a JDBC driver.
* DaoAuthenticationProvider retrieves the user's credentials, such as username and password, from the database and compares them to the credentials provided by the user during login. If the credentials match, the provider creates an Authentication object representing the authenticated user.
* To use DaoAuthenticationProvider, you must provide a UserDetailsService implementation that retrieves user information from the database. UserDetailsService provides the username, password, and authorities for a given user. DaoAuthenticationProvider uses UserDetailsService to retrieve user information from the database during authentication.
* To use the DaoAuthenticationProvider, you need to configure it in your Spring Security configuration file.

### 5.2 Internal Flow

```
UsernamePasswordAuthenticationToken
        │
        ▼
DaoAuthenticationProvider
        │
        ├── loadUserByUsername()
        ├── PasswordEncoder.matches()
        ├── Account checks
        ▼
Authenticated Token
```

### 5.3 Required Components

* UserDetailsService
* PasswordEncoder
* DataSource (optional if JDBC-based)

---

### 5.4 Example

Here's an example of how to configure the DaoAuthenticationProvider with an in-memory UserDetailsService and a BCryptPasswordEncoder:

**Step 1:** First, you need to configure a **DataSource** bean to provide the database connection

```java
@Bean 
public DataSource dataSource()
{
    DriverManagerDataSource dataSource
        = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl(
        "jdbc:postgresql://localhost:5432/mydatabase");
    dataSource.setUsername("myuser");
    dataSource.setPassword("mypassword");
    return dataSource;
}
```

**Step 2:** Next, you need to create a **UserDetailsServicebean** that will retrieve user information from the database. Here's an example implementation

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
    }

}
```

**Step 3:** Finally, you can configure **DaoAuthenticationProvider** in your SecurityConfig class

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(MyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * DaoAuthenticationProvider configuration
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * AuthenticationManager (optional but recommended if you need it elsewhere)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * SecurityFilterChain replaces configure(HttpSecurity)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Register AuthenticationProvider
                .authenticationProvider(daoAuthenticationProvider())

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )

                // Form login
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )

                // Logout
                .logout(logout -> logout
                        .permitAll()
                );

        return http.build();
    }
}

```
---

🔄 Authentication Flow (DaoAuthenticationProvider)

```
Login Form
(username/password)
|
v
UsernamePasswordAuthenticationToken
|
v
ProviderManager
|
v
DaoAuthenticationProvider
|
+--> MyUserDetailsService
|        |
|        +--> Database (PostgreSQL)
|
+--> PasswordEncoder.matches()
|
v
SecurityContextHolder
```

---

## Part 6: LDAP Authentication Provider

### 6.1 How it works

* LdapAuthenticationProvider is a specific implementation of AuthenticationProvider that authenticates users against an LDAP (Lightweight Directory Access Protocol) server.
* LDAP is a protocol used for accessing and maintaining distributed directory information services over an Internet Protocol (IP) network. It is commonly used for managing user authentication and authorization information, such as usernames, passwords, and roles.
* The LdapAuthenticationProvider class in Spring Security provides an implementation of the AuthenticationProvider interface that delegates authentication to an LDAP server. It takes a LdapAuthenticator instance, which is responsible for performing the actual authentication against the LDAP server.
* To configure a LdapAuthenticationProviderin Spring Security, you need to provide the LdapAuthenticator and LdapAuthoritiesPopulatorobjects, as well as other optional settings such as the LDAP server URL, bind credentials, and search base.

### 6.2 LDAP Authentication Flow

```
Login Request
    │
    ▼
LdapAuthenticationProvider
    │
    ├── Bind user DN
    ├── Validate password
    ├── Load groups
    ▼
Authenticated User
```

### Example:

---

## Step 1: Define `LdapContextSource`

This bean defines **how Spring connects to the LDAP server**.

```java
@Bean
public LdapContextSource contextSource() {
    LdapContextSource contextSource = new LdapContextSource();
    contextSource.setUrl("ldap://localhost:389");
    contextSource.setBase("dc=example,dc=com");
    contextSource.setUserDn("cn=admin,dc=example,dc=com");
    contextSource.setPassword("password");
    return contextSource;
}
```

### What this does

* `url` → LDAP server address
* `base` → Root DN for all searches
* `userDn` / `password` → Bind credentials used by Spring Security

---

## Step 2: Define `LdapUserSearch`

This tells Spring Security **how to find a user in LDAP**.

```java
@Bean
public LdapUserSearch ldapUserSearch(LdapContextSource contextSource) {
    return new FilterBasedLdapUserSearch(
        "ou=users",
        "(uid={0})",
        contextSource
    );
}
```

### What this does

* Searches under `ou=users`
* Uses `(uid={username})` to locate users

---

## Step 3: Define `LdapAuthoritiesPopulator`

This retrieves **roles / groups** for the authenticated user.

```java
@Bean
public LdapAuthoritiesPopulator authoritiesPopulator(LdapContextSource contextSource) {
    return new DefaultLdapAuthoritiesPopulator(contextSource, "ou=groups");
}
```

### What this does

* Loads roles from `ou=groups`
* Converts LDAP groups into `GrantedAuthority`

---

## Step 4: Define `LdapAuthenticator`

Spring Security requires an authenticator to perform the **bind / credential check**.

```java
@Bean
public BindAuthenticator ldapAuthenticator(
        LdapContextSource contextSource,
        LdapUserSearch ldapUserSearch) {

    BindAuthenticator authenticator = new BindAuthenticator(contextSource);
    authenticator.setUserSearch(ldapUserSearch);
    return authenticator;
}
```

### Why this is required

* Performs LDAP bind with the user’s DN and password
* Fails authentication if credentials are invalid

---

## Step 5: Define `LdapAuthenticationProvider`

This provider **ties everything together**.

```java
@Bean
public LdapAuthenticationProvider ldapAuthenticationProvider(
        BindAuthenticator ldapAuthenticator,
        LdapAuthoritiesPopulator authoritiesPopulator) {

    return new LdapAuthenticationProvider(ldapAuthenticator, authoritiesPopulator);
}
```

---

## Step 6: Register Provider in Spring Security 7

### `SecurityFilterChain` (Modern Replacement)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LdapAuthenticationProvider ldapAuthenticationProvider) throws Exception {

        http
            // Register LDAP provider
            .authenticationProvider(ldapAuthenticationProvider)

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            // Login mechanism
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )

            // Logout support
            .logout(logout -> logout.permitAll());

        return http.build();
    }
}
```

---

## 🔄 LDAP Authentication Flow Diagram

```
User Login Form
(username / password)
        |
        v
UsernamePasswordAuthenticationToken
        |
        v
ProviderManager
        |
        v
LdapAuthenticationProvider
        |
        +--> BindAuthenticator
        |        |
        |        +--> LdapUserSearch
        |        |        |
        |        |        +--> LDAP Server (uid search)
        |        |
        |        +--> LDAP Bind (password check)
        |
        +--> LdapAuthoritiesPopulator
                 |
                 +--> LDAP Groups (roles)
        |
        v
Authenticated Authentication
        |
        v
SecurityContextHolder
```

---

## 🧠 Important Notes

* LDAP **does not need**:

    * `UserDetailsService`
    * `PasswordEncoder`
* Password validation is done by **LDAP bind**
* You can combine LDAP with DAO authentication:

```java
http
    .authenticationProvider(ldapAuthenticationProvider)
    .authenticationProvider(daoAuthenticationProvider);
```

LDAP is attempted **first**, DAO is fallback.

---

## 🎯 When LDAP Is the Right Choice

* Enterprise environments
* Centralized user management
* Single sign-on (SSO) systems
* Corporate directories (Active Directory)

---

## Part 7: OpenID Authentication Provider (Conceptual)

### 7.1 Purpose

Delegates authentication to a trusted external OpenID provider.

### 7.2 Authentication Flow

```
Client → Application → OpenID Provider
           ↑               ↓
        Callback ← Assertion
```

> ⚠ OpenID 2.0 is largely replaced by OAuth2/OIDC in modern systems.

---

## Part 8: JWT Authentication Provider

### 8.1 Stateless Authentication

* JWT-based authentication avoids sessions.
* JwtAuthenticationProvider is an implementation of the Spring Security AuthenticationProvider interface that is used to authenticate users based on JSON Web Tokens (JWTs).
* When a JWT is presented for authentication, the JwtAuthenticationProvider verifies the token's signature and extracts the user's identity information from the token. It then creates a JwtAuthenticationToken object, which encapsulates the authenticated user's details.
* To use JwtAuthenticationProvider in Spring Security, you will need to configure it with a JwtDecoder instance that will be used to decode the JWT. The JwtDecoder is responsible for verifying the signature and parsing the JWT payload.

### 8.2 JWT Authentication Flow

```
Request + JWT
     │
     ▼
JwtAuthenticationFilter
     │
     ▼
JwtAuthenticationProvider
     │
     ├── Verify signature
     ├── Validate claims
     └── Extract authorities
```

### 8.3 Benefits

* Scalable
* Stateless
* Suitable for REST APIs

---

### 8.4 Example:

Here's an example of how to configure JwtAuthenticationProvider in Spring Security:

---

**Note:** This setup assumes:

* Stateless authentication using **JWT**
* Tokens are signed with **HMAC (HS256)**
* JWT is sent in the `Authorization: Bearer <token>` header
* Spring Security acts as a **Resource Server**

---

## High-Level Architecture

```
Client ──(HTTP Request + JWT)──▶ Security Filter Chain
                                     │
                                     ▼
                            BearerTokenAuthenticationFilter
                                     │
                                     ▼
                            JwtAuthenticationProvider
                                     │
                                     ▼
                                  JwtDecoder
                                     │
                                     ▼
                          Signature + Claims Validation
                                     │
                        ┌────────────┴────────────┐
                        ▼                         ▼
                 Authentication Success     Authentication Failure
                        │                         │
                        ▼                         ▼
               SecurityContextHolder        401 / 403 Response
```

---

## Step 1: Define a `JwtDecoder` Bean

The `JwtDecoder` is responsible for:

* Verifying the **JWT signature**
* Validating token structure
* Parsing claims

### Configuration

```java
@Bean
public JwtDecoder jwtDecoder() {
    String secret = "mySecretKeymySecretKeymySecretKey"; // must be >= 256 bits for HS256

    SecretKey secretKey = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
    );

    return NimbusJwtDecoder.withSecretKey(secretKey).build();
}
```

### What Happens Here?

* `NimbusJwtDecoder` is Spring Security’s default JWT decoder
* It checks:

    * Token signature
    * Expiration (`exp`)
    * Issuer / audience (if configured later)

---

## Step 2: Create a `JwtAuthenticationProvider`

The `JwtAuthenticationProvider`:

* Delegates JWT validation to `JwtDecoder`
* Converts JWT claims into a Spring `Authentication` object

### Configuration

```java
@Bean
public JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
    return new JwtAuthenticationProvider(jwtDecoder);
}
```

### Responsibility Flow

```
JWT → JwtAuthenticationProvider → JwtDecoder → Authentication
```

---

## Step 3: Configure the `AuthenticationManager`

Spring Security 6 uses an explicit `AuthenticationManager` bean.

```java
@Bean
public AuthenticationManager authenticationManager(
        JwtAuthenticationProvider jwtAuthenticationProvider) {

    return new ProviderManager(jwtAuthenticationProvider);
}
```

### Why This Matters

* The `AuthenticationManager` decides **which provider handles authentication**
* In this case: **JWT only**

---

## Step 4: Configure the Security Filter Chain

This replaces the old `configure(HttpSecurity http)` method.

### Security Configuration

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
        );

    return http.build();
}
```

### Key Points

* **Stateless**: No HTTP session
* **JWT authentication happens automatically**
* `BearerTokenAuthenticationFilter` is auto-registered

---

## Step 5: How JWT Authentication Works Internally

Spring Security automatically wires:

```
BearerTokenAuthenticationFilter
        ↓
AuthenticationManager
        ↓
JwtAuthenticationProvider
        ↓
JwtDecoder
```

### Runtime Request Flow

```
1. Client sends request with Authorization header
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...

2. BearerTokenAuthenticationFilter extracts the token

3. Token passed to AuthenticationManager

4. JwtAuthenticationProvider validates token

5. JwtDecoder verifies:
   - Signature
   - Expiration
   - Claims

6. On success:
   - JwtAuthenticationToken created
   - Stored in SecurityContextHolder

7. Controller executes with authenticated principal
```

---

## Optional: Custom JWT Claims → Authorities Mapping

By default, Spring looks for `scope` or `scp` claims.

### Custom Converter Example

```java
@Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    authoritiesConverter.setAuthorityPrefix("ROLE_");
    authoritiesConverter.setAuthoritiesClaimName("roles");

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
    return converter;
}
```

### Plug Into Security Config

```java
.oauth2ResourceServer(oauth2 -> oauth2
    .jwt(jwt -> jwt
        .decoder(jwtDecoder())
        .jwtAuthenticationConverter(jwtAuthenticationConverter())
    )
)
```

---

## Complete Authentication Flow Diagram (Expanded)

```
┌────────────┐
│   Client   │
└─────┬──────┘
      │  Authorization: Bearer JWT
      ▼
┌─────────────────────────────────────┐
│ SecurityFilterChain                 │
│                                     │
│  BearerTokenAuthenticationFilter    │
│            │                        │
│            ▼                        │
│   AuthenticationManager             │
│            │                        │
│            ▼                        │
│   JwtAuthenticationProvider         │
│            │                        │
│            ▼                        │
│         JwtDecoder                  │
│            │                        │
│            ▼                        │
│   Signature & Claims Validation     │
│            │                        │
│     ┌──────┴──────┐                 │
│     ▼             ▼                 │
│ Success        Failure              │
│     │             │                 │
│     ▼             ▼                 │
│ SecurityContext  401 / 403          │
└─────────────────────────────────────┘
```

---

## Summary Table

| Component                         | Purpose                           |
| --------------------------------- | --------------------------------- |
| `JwtDecoder`                      | Verifies token signature & claims |
| `JwtAuthenticationProvider`       | Converts JWT → Authentication     |
| `AuthenticationManager`           | Delegates to provider             |
| `BearerTokenAuthenticationFilter` | Extracts JWT from request         |
| `SecurityContextHolder`           | Stores authenticated user         |

---

## Key Takeaways

* ✅ No custom JWT filter needed (Spring handles it)
* ✅ No `WebSecurityConfigurerAdapter`
* ✅ Fully stateless & scalable
* ✅ Production-ready structure

---

# Mini-Project Overview

## Project: `secure-api`

### Features

* `/auth/login` → generates JWT
* `/api/profile` → protected endpoint
* PostgreSQL stores users
* Two authentication modes:

    * **Mode A**: JWT only (claims-based)
    * **Mode B**: JWT + database validation

---

## Project Structure

```
secure-api
├── config
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│
├── auth
│   ├── AuthController.java
│   ├── JwtTokenService.java
│
├── user
│   ├── User.java
│   ├── UserRepository.java
│   ├── CustomUserDetailsService.java
│
└── api
    └── ProfileController.java
```

---

# PART 1 — TOKEN GENERATION FLOW (LOGIN)

This part is **shared** by both approaches.

---

## Step 1: User Entity (PostgreSQL)

```java
@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String role;
}
```

---

## Step 2: UserDetailsService (Database Auth)

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
```

---

## Step 3: JWT Token Generation Service

```java
@Service
public class JwtTokenService {

    private final SecretKey key =
            new SecretKeySpec("mySecretKeymySecretKeymySecretKey".getBytes(),
                    "HmacSHA256");

    public String generateToken(Authentication auth) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("roles", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }
}
```

---

## Step 4: Login Controller

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenService tokenService;

    public AuthController(AuthenticationManager authManager,
                          JwtTokenService tokenService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        Authentication authentication =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(), request.password()
                        )
                );

        return tokenService.generateToken(authentication);
    }
}
```

---

## TOKEN GENERATION FLOW DIAGRAM

```
Client
  │  username + password
  ▼
AuthenticationManager
  │
  ▼
DaoAuthenticationProvider
  │
  ▼
UserDetailsService (Postgres)
  │
  ▼
PasswordEncoder
  │
  ▼
Authentication SUCCESS
  │
  ▼
JwtTokenService
  │
  ▼
JWT returned to client
```

---

# PART 2 — MODE A: JWT-ONLY (CLAIMS-BASED)

### 🔹 No database access per request

### 🔹 Fast & fully stateless

---

## Security Configuration (JWT Only)

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 ->
            oauth2.jwt(Customizer.withDefaults())
        );

    return http.build();
}
```

---

## Runtime Request Flow (JWT-Only)

```
Client ── JWT ──▶ API
                     │
                     ▼
          BearerTokenAuthenticationFilter
                     │
                     ▼
                JwtDecoder
                     │
        Signature + Expiration Check
                     │
                     ▼
            Authorities from JWT
                     │
                     ▼
              Controller Executes
```

### ❗ Limitation

* If user is **disabled or deleted**, token still works until expiry

---

# PART 3 — MODE B: JWT + DATABASE USER VALIDATION

### 🔹 Stronger security

### 🔹 Slightly more overhead

---

## Custom JWT Authentication Converter (DB Validation)

```java
@Component
public class DatabaseValidatingJwtConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository repo;

    public DatabaseValidatingJwtConverter(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String username = jwt.getSubject();

        User user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, authorities);
    }
}
```

---

## Plug Converter into Security Config

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http,
        DatabaseValidatingJwtConverter converter) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwt ->
                jwt.jwtAuthenticationConverter(converter)
            )
        );

    return http.build();
}
```

---

## Runtime Request Flow (JWT + DB)

```
Client ── JWT ──▶ API
                     │
                     ▼
          BearerTokenAuthenticationFilter
                     │
                     ▼
                JwtDecoder
                     │
            Signature Validation
                     │
                     ▼
          DatabaseValidatingJwtConverter
                     │
            PostgreSQL User Lookup
                     │
           ┌─────────┴─────────┐
           ▼                   ▼
     User Exists          User Missing
           │                   │
           ▼                   ▼
   SecurityContext        401 Unauthorized
```

---

# SIDE-BY-SIDE COMPARISON

| Feature                  | JWT-Only                   | JWT + DB               |
| ------------------------ | -------------------------- | ---------------------- |
| Database hit per request | ❌ No                       | ✅ Yes                  |
| Stateless                | ✅ Fully                    | ⚠️ Semi                |
| User revocation          | ❌ Token lives              | ✅ Immediate            |
| Performance              | 🚀 Faster                  | 🐢 Slightly slower     |
| Security                 | Medium                     | High                   |
| Best for                 | Public APIs, microservices | Banking, admin systems |

---

# WHEN TO USE WHICH?

### ✅ Use **JWT-Only** if:

* Microservices
* High-throughput APIs
* Short token lifetime

### ✅ Use **JWT + DB** if:

* User disabling is critical
* Compliance/security matters
* Admin or financial systems

---

## Part 9: Remember-Me Authentication Provider

### 9.1 How it works

* Automatically re-authenticate users using persistent tokens.
* RememberMeAuthenticationProvider is an implementation of the Spring Security AuthenticationProvider interface that is used to authenticate users based on previously saved remember-me tokens.
* When a user logs in with the "remember me" option selected, Spring Security will issue a remember-me token and store it in a persistent storage, such as a database or a cookie. The next time the user visits the site, Spring Security will attempt to authenticate the user using the remember-me token. This is where RememberMeAuthenticationProvider comes into play.
* RememberMeAuthenticationProvider is responsible for validating the remember-me token and creating an Authentication object based on the token. If the token is valid, RememberMeAuthenticationProvider will create a new RememberMeAuthenticationToken and return it. If the token is not valid, RememberMeAuthenticationProvider will throw a BadCredentialsException.
* To use RememberMeAuthenticationProvider in Spring Security, you will need to configure it with a UserDetailsService that will be used to load the user's details based on the remember-me token.

### 9.2 Remember-Me Flow

```
Browser Cookie
     │
     ▼
RememberMeAuthenticationProvider
     │
     ├── Load token
     ├── Validate
     └── Load UserDetails
```

### 9.3 Persistent vs Hash-Based

| Type       | Storage     | Security |
| ---------- | ----------- | -------- |
| Hash-based | Cookie only | Weaker   |
| Persistent | Database    | Stronger |

---

## Example:

Here's an example of how to configure **RememberMeAuthenticationProvider** in Spring Security:

---

# Remember-Me Authentication (Database-Backed)

**Spring Security 6 / Spring Boot 3**

---

## High-Level Architecture

```
Browser
  │
  ├── Username + Password
  │
  └── Remember-Me Cookie (optional)
        │
        ▼
Spring Security Filter Chain
        │
        ├── UsernamePasswordAuthenticationFilter
        │
        └── RememberMeAuthenticationFilter
                │
                ▼
   RememberMeAuthenticationProvider
                │
                ▼
   PersistentTokenRepository (PostgreSQL)
```

---

# PART 1 — UserDetailsService (Database Users)

This part is **mandatory** for Remember-Me.

---

## UserDetailsService Implementation

```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
```

### Why this is required

* Remember-Me **re-authenticates users later**
* It needs `UserDetailsService` to rebuild the `Authentication`

---

## User Lookup Flow

```
Remember-Me Token
        │
        ▼
UserDetailsService
        │
        ▼
PostgreSQL users table
```

---

# PART 2 — Persistent Token Repository (Database)

Spring Security provides a ready-to-use JDBC implementation.

---

## Database Schema (Required)

```sql
CREATE TABLE persistent_logins (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL
);
```

---

## PersistentTokenRepository Bean

```java
@Bean
public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
    JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
    repo.setDataSource(dataSource);
    return repo;
}
```

### What this does

* Stores **random tokens**, not passwords
* Prevents replay attacks
* Supports logout + token invalidation

---

# PART 3 — RememberMeAuthenticationProvider

This provider **validates remember-me tokens**.

---

## Provider Bean

```java
@Bean
public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
    return new RememberMeAuthenticationProvider("myAppKey");
}
```

### Key rule

* The key **must match** the Remember-Me services key
* Used to verify token integrity

---

# PART 4 — RememberMeServices (Persistent)

⚠️ **Important**
Use **`PersistentTokenBasedRememberMeServices`**, **NOT** `TokenBasedRememberMeServices`.

---

## Remember-Me Services Bean

```java
@Bean
public PersistentTokenBasedRememberMeServices rememberMeServices(
        UserDetailsService userDetailsService,
        PersistentTokenRepository tokenRepository) {

    PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(
                    "myAppKey",
                    userDetailsService,
                    tokenRepository
            );

    services.setTokenValiditySeconds(7 * 24 * 60 * 60); // 1 week
    services.setAlwaysRemember(false);

    return services;
}
```

### Why Persistent is better

| TokenBased            | Persistent      |
| --------------------- |-----------------|
| Cookie only           | Cookie + DB     |
| Hash-based            | Random tokens   |
| Vulnerable to theft   | Theft-resistant |
| ❌ Deprecated for prod| ✅ Recommended  | 

---

# PART 5 — AuthenticationManager (Modern)

Spring Security 6 requires **explicit wiring**.

---

## AuthenticationManager Bean

```java
@Bean
public AuthenticationManager authenticationManager(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder,
        RememberMeAuthenticationProvider rememberMeProvider) {

    DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
    daoProvider.setUserDetailsService(userDetailsService);
    daoProvider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(daoProvider, rememberMeProvider);
}
```

---

## Authentication Provider Order

```
AuthenticationManager
        │
        ├── DaoAuthenticationProvider (login)
        │
        └── RememberMeAuthenticationProvider (cookie)
```

---

# PART 6 — SecurityFilterChain (Modern Configuration)

---

## Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            PersistentTokenBasedRememberMeServices rememberMeServices,
            AuthenticationManager authenticationManager) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authenticationManager(authenticationManager)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**", "/login").permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )

            .rememberMe(rm -> rm
                .rememberMeServices(rememberMeServices)
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/public")
                .deleteCookies("remember-me")
            )

            .exceptionHandling(ex ->
                ex.accessDeniedPage("/403")
            );

        return http.build();
    }
}
```

---

# PART 7 — REQUEST FLOW DIAGRAMS

---

## Normal Login Flow

```
Browser
  │ username + password
  ▼
UsernamePasswordAuthenticationFilter
  │
DaoAuthenticationProvider
  │
UserDetailsService → PostgreSQL
  │
Authentication SUCCESS
  │
PersistentTokenBasedRememberMeServices
  │
remember-me cookie + DB token saved
```

---

## Remember-Me Login Flow (No Credentials)

```
Browser
  │ remember-me cookie
  ▼
RememberMeAuthenticationFilter
  │
  ▼
RememberMeAuthenticationProvider
  │
  ▼
PersistentTokenRepository
  │
  ▼
UserDetailsService
  │
  ▼
SecurityContext restored
```

---

## Logout Flow

```
/logout
   │
   ▼
LogoutFilter
   │
   ▼
Delete remember-me cookie
   │
   ▼
Remove token from DB
```

---

# SUMMARY — WHAT YOU ACTUALLY NEED

### Required Components

* ✅ UserDetailsService
* ✅ PersistentTokenRepository
* ✅ RememberMeAuthenticationProvider
* ✅ PersistentTokenBasedRememberMeServices
* ✅ DaoAuthenticationProvider

### Not Needed

* ❌ WebSecurityConfigurerAdapter
* ❌ TokenBasedRememberMeServices
* ❌ HttpSession storage

---

# Session vs Remember-Me vs JWT

**Spring Security Authentication Models**

---

## 1. Big-Picture Comparison Table

| Feature               | Session Authentication          | Remember-Me                      | JWT Authentication        |
| --------------------- | ------------------------------- | -------------------------------- | ------------------------- |
| Primary purpose       | Maintain login during a session | Auto-login after browser restart | Stateless authentication  |
| Server state          | ✅ Yes (HttpSession)             | ✅ Yes (DB tokens)                | ❌ No                      |
| Cookie used           | JSESSIONID                      | remember-me                      | Authorization header      |
| Works without DB      | ❌                               | ❌                                | ✅                         |
| Scales horizontally   | ⚠️ Needs session sharing        | ⚠️ Needs shared DB               | ✅ Very well               |
| Token revocation      | ✅ Immediate                     | ✅ Immediate                      | ❌ Hard                    |
| Security level        | High                            | Medium-High                      | Medium                    |
| Mobile / API friendly | ❌                               | ❌                                | ✅                         |
| Common use case       | Web apps                        | “Keep me logged in”              | APIs, SPAs, microservices |

---

## 2. Session-Based Authentication

### What it is

* Traditional **stateful authentication**
* Uses **HttpSession**
* Default for **form login** in Spring Security

---

### Authentication Flow (Session)

```
Browser
  │ username + password
  ▼
UsernamePasswordAuthenticationFilter
  │
  ▼
AuthenticationManager
  │
  ▼
SecurityContext
  │
  ▼
HttpSession
  │
  ▼
JSESSIONID cookie
```

---

### How it works

1. User logs in with username/password
2. Spring Security authenticates
3. `SecurityContext` stored in session
4. Session ID sent to browser
5. Every request uses that session

---

### Strengths

* ✅ Very secure
* ✅ Easy logout
* ✅ Easy role changes
* ✅ Best for classic MVC apps

---

### Weaknesses

* ❌ Server memory usage
* ❌ Hard to scale horizontally
* ❌ Not API-friendly

---

### When to use

1. [x] Traditional web apps
2. [x] Admin panels
3. [x] Internal systems

---

## 3. Remember-Me Authentication

### What it is

* **Secondary authentication**
* Restores login **without password**
* Uses **secure random tokens stored in DB**

---

### Authentication Flow (Remember-Me)

```
Browser
  │ remember-me cookie
  ▼
RememberMeAuthenticationFilter
  │
  ▼
RememberMeAuthenticationProvider
  │
  ▼
PersistentTokenRepository
  │
  ▼
UserDetailsService
  │
  ▼
SecurityContext restored
```

---

### How it works

1. User logs in and checks “Remember Me”
2. Token stored in DB + cookie
3. Browser restarts
4. Cookie sent to server
5. Token validated and user restored

---

### Strengths

* ✅ User convenience
* ✅ Tokens are revocable
* ✅ Safer than JWT for browsers

---

### Weaknesses

* ❌ Still server-state (DB)
* ❌ Not API-friendly
* ❌ Slower than JWT

---

### When to use

1. [x] “Keep me logged in”
2. [x] Banking / enterprise apps
3. [x] Session-based systems

---

## 4. JWT Authentication

### What it is

* **Stateless authentication**
* Token contains claims
* No server storage required

---

### Authentication Flow (JWT)

```
Client
  │ username + password
  ▼
Authentication Endpoint
  │
JWT Issued
  │
Authorization: Bearer JWT
  ▼
JwtAuthenticationFilter
  │
  ▼
JwtDecoder
  │
  ▼
SecurityContext
```

---

### How it works

1. User logs in
2. Server issues signed JWT
3. Client stores token
4. Token sent on every request
5. Server validates signature

---

### Strengths

* ✅ Stateless
* ✅ Very scalable
* ✅ Perfect for APIs & mobile
* ✅ No session storage

---

### Weaknesses

* ❌ Hard to revoke
* ❌ Token theft risk
* ❌ Requires careful expiry handling
---

### When to use

1. [x] REST APIs
2. [x] Mobile apps
3. [x] SPAs
4. [x] Microservices

---

## 5. Security Comparison

| Risk             | Session | Remember-Me | JWT    |
| ---------------- | ------- | ----------- | ------ |
| Token theft      | Low     | Medium      | High   |
| Replay attack    | Low     | Low         | Medium |
| Immediate logout | Yes     | Yes         | No     |
| Rotation         | N/A     | Yes         | Hard   |
| CSRF risk        | High    | High        | Low    |

---

## 6. Logout Behavior

### Session

```
Logout → Session destroyed → User logged out
```

### Remember-Me

```
Logout → Cookie deleted + DB token removed
```

### JWT

```
Logout → Client deletes token
(Server still accepts token)
```

---

## 7. Scaling Considerations

```
Single Server
   ├── Session ✅
   ├── Remember-Me ✅
   └── JWT ✅

Multiple Servers
   ├── Session ❌ (needs Redis)
   ├── Remember-Me ⚠️ (shared DB)
   └── JWT ✅
```

---

## 8. Real-World Architecture Choices

### Enterprise Web App

```
Session + Remember-Me
```

### Public REST API

```
JWT only
```

### SPA + Backend

```
JWT + Refresh Tokens
```

### Admin Dashboard

```
Session only
```

---

## 9. Best Practice Recommendation

| Scenario      | Recommended           |
| ------------- | --------------------- |
| Web MVC       | Session + Remember-Me |
| SPA           | JWT + Refresh Token   |
| Mobile        | JWT                   |
| Microservices | JWT                   |
| Banking apps  | Session + Remember-Me |

---

## 10. Final Decision Guide

```
Do you need stateless?
   └── YES → JWT
   └── NO
        │
        ├── Need convenience?
        │       └── YES → Remember-Me
        │       └── NO → Session
```

---

## Part 10: Custom Authentication Provider

Explanation of how to create custom authentication and authorization providers:

* To create a custom authentication provider in Spring Security, you need to implement the AuthenticationProvider interface. The AuthenticationProvider interface has a single method authenticate(Authentication authentication) which takes an Authentication object as input and returns an Authentication object as output. The authenticate() method is responsible for authenticating the user based on the provided credentials and returning an Authentication object with the user's details.
* Here's a step-by-step guide on how to create a custom authentication provider in Spring Security:

### 10.1 When to Use

* Legacy systems
* Custom credential formats
* External services

### 10.2 Custom Provider Flow

```
Custom Token
     │
     ▼
CustomAuthenticationProvider
     │
     ├── Validate input
     ├── Call service
     └── Build authorities
```

### 10.3 Example Implementation

```java
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (!"secret".equals(password)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new UsernamePasswordAuthenticationToken(
                username,
                password,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authenticationProvider(customAuthenticationProvider)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/public/**").permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

```
---

### Authentication Flow (CustomAuthenticationProvider)

**High-level flow**

```
Client
  │ username + password
  ▼
UsernamePasswordAuthenticationFilter
  │
  ▼
AuthenticationManager (ProviderManager)
  │
  ▼
CustomAuthenticationProvider
  │
  ▼
UserService / Database
  │
  ▼
Authentication (SUCCESS)
  │
  ▼
SecurityContextHolder

```

---

### Detailed step-by-step flow

```
1. POST /login
2. UsernamePasswordAuthenticationFilter creates:
   UsernamePasswordAuthenticationToken (unauthenticated)

3. Token passed to AuthenticationManager

4. ProviderManager loops providers:
   └── supports() == true → CustomAuthenticationProvider

5. authenticate():
   ├── Load user from DB
   ├── Verify password
   ├── Build authorities
   └── Return authenticated token

6. SecurityContextHolder updated

7. User considered authenticated
```

---

### Why supports() Matters

```java
@Override
public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
}

```

**Purpose**

* Allows multiple AuthenticationProviders
* ProviderManager selects only compatible providers
* Enables mixing:
  1. [x] DAO authentication
  2. [x] JWT authentication
  3. [x] LDAP authentication
  4. [x] Custom authentication

---

### When Should You Use a CustomAuthenticationProvider?

| Use Case               | Recommendation               |
| ---------------------- | ---------------------------- |
| Username/password + DB | DaoAuthenticationProvider    |
| External service       | CustomAuthenticationProvider |
| Multi-factor auth      | CustomAuthenticationProvider |
| Token-based auth       | JwtAuthenticationProvider    |
| Legacy system          | CustomAuthenticationProvider |

---

## Part 11: Combining DaoAuthenticationProvider and CustomAuthenticationProvider

```java
@Bean
AuthenticationManager authenticationManager(
        DaoAuthenticationProvider daoProvider,
        CustomAuthenticationProvider customProvider) {

    return new ProviderManager(
            List.of(customProvider, daoProvider));
}
```

Execution Order Matters:

1. Custom provider tried first
2. DAO provider as fallback

---

## Part 12: Testing Authentication Providers

### 12.1 Unit Testing Strategy

* Mock dependencies
* Test success & failure
* Verify supports()

```java
@Test
void shouldAuthenticateSuccessfully() {
    Authentication auth =
        new UsernamePasswordAuthenticationToken("user", "secret");

    Authentication result = provider.authenticate(auth);

    assertTrue(result.isAuthenticated());
}
```

---

## Part 13: Summary

1. [x] AuthenticationProviders are the **core authentication engine**
2. [x] ProviderManager enables **multiple authentication strategies**
3. [x] DaoAuthenticationProvider is the most common
4. [x] Custom providers unlock advanced use cases
5. [x] Order of providers is critical

---

